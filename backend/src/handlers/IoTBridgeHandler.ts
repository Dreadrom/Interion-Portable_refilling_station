/**
 * IoTBridgeHandler — AWS IoT Core Rule → Lambda → WebSocket push
 *
 * This Lambda is triggered by an IoT Core Rule that matches:
 *   acerev/stations/+/telemetry/#
 *   acerev/stations/+/auth/challenge
 *   acerev/stations/+/telemetry/complete
 *
 * IoT Rule SQL (configure in AWS Console or CDK/CloudFormation):
 *   SELECT *, topic() AS _topic, topic(3) AS stationId
 *   FROM 'acerev/stations/+/telemetry/#'
 *
 *   And a separate rule:
 *   SELECT *, topic(3) AS stationId
 *   FROM 'acerev/stations/+/auth/challenge'
 *
 * The Lambda receives the raw MQTT payload enriched with stationId + _topic
 * from the IoT Rule SQL, then fans the message out to all WebSocket clients
 * subscribed to the same transactionId via API Gateway Management API.
 *
 * Data flow:
 *   ODROID-M1S  →  AWS IoT Core  →  IoT Rule  →  this Lambda
 *                                                      │
 *                                              DynamoDB (find connections)
 *                                                      │
 *                                           API GW Management API
 *                                                      │
 *                                                Driver App (WebSocket)
 */

import {
  DynamoDBClient,
  QueryCommand,
  DeleteItemCommand,
} from '@aws-sdk/client-dynamodb';
import {
  ApiGatewayManagementApiClient,
  PostToConnectionCommand,
  GoneException,
} from '@aws-sdk/client-apigatewaymanagementapi';

const REGION     = process.env.AWS_REGION         ?? 'ap-southeast-1';
const TABLE_NAME = process.env.WS_CONNECTIONS_TABLE ?? 'AceRevWsConnections';
// Set this to your WebSocket API Gateway endpoint, e.g.:
// https://abc123.execute-api.ap-southeast-1.amazonaws.com/prod
const WS_ENDPOINT = process.env.WS_API_ENDPOINT!;

const dynamo = new DynamoDBClient({ region: REGION });

// ---------------------------------------------------------------------------

/**
 * The shape of events delivered by the IoT Core Rule.
 * The Rule SQL projects stationId and _topic from the MQTT metadata.
 */
interface IoTRuleEvent {
  transactionId?: string;
  stationId?:     string;
  _topic?:        string;
  // Flow telemetry
  volumeLitres?:  number;
  rateLPM?:       number;
  elapsedSeconds?: number;
  // Auth challenge
  code?:          string;
  ready?:         boolean;
  // Completion
  amountCharged?: number;
  stopReason?:    string;
  // Catch-all
  [key: string]: unknown;
}

export async function handler(event: IoTRuleEvent): Promise<void> {
  if (!event.transactionId) {
    console.warn('IoTBridgeHandler: event missing transactionId, skipping', JSON.stringify(event));
    return;
  }

  const transactionId = event.transactionId;
  const topic         = event._topic ?? '';

  // Determine the message type from the MQTT topic suffix
  let messageType: string;
  if (topic.includes('telemetry/flow'))     messageType = 'FLOW';
  else if (topic.includes('telemetry/complete')) messageType = 'COMPLETE';
  else if (topic.includes('auth/challenge')) messageType = 'AUTH_CHALLENGE';
  else if (topic.includes('telemetry/status')) messageType = 'STATUS';
  else messageType = 'UNKNOWN';

  // Build structured push payload (strip internal IoT Rule metadata)
  const { _topic, stationId: _sid, ...rest } = event;
  const payload = JSON.stringify({ type: messageType, ...rest });

  // Find all WebSocket connections subscribed to this transactionId
  const result = await dynamo.send(new QueryCommand({
    TableName:              TABLE_NAME,
    KeyConditionExpression: 'transactionId = :txn',
    ExpressionAttributeValues: { ':txn': { S: transactionId } },
  }));

  const connections = result.Items ?? [];
  if (connections.length === 0) {
    console.log('No WebSocket subscribers for transactionId:', transactionId);
    return;
  }

  const apigw   = new ApiGatewayManagementApiClient({ endpoint: WS_ENDPOINT });
  const data    = Buffer.from(payload);

  // Push concurrently; clean up stale connections (GoneException = client disconnected)
  const pushResults = await Promise.allSettled(
    connections.map(item => {
      const connectionId = item.connectionId?.S!;
      return apigw.send(new PostToConnectionCommand({ ConnectionId: connectionId, Data: data }))
        .catch(async (err) => {
          if (err instanceof GoneException) {
            // Client disconnected without sending $disconnect — clean up
            console.log('Removing stale connection:', connectionId);
            await dynamo.send(new DeleteItemCommand({
              TableName: TABLE_NAME,
              Key: {
                transactionId: { S: transactionId },
                connectionId:  { S: connectionId  },
              },
            }));
          } else {
            throw err;
          }
        });
    })
  );

  const failed = pushResults.filter(r => r.status === 'rejected');
  if (failed.length > 0) {
    console.error('Some pushes failed:', failed.map(r => (r as any).reason?.message));
  }

  console.log(`Pushed ${messageType} to ${connections.length} subscribers (txn: ${transactionId})`);
}
