/**
 * WebSocketHandler — API Gateway WebSocket Lambda
 *
 * Manages the real-time push channel between the cloud and the driver app.
 * Deploy this as a single Lambda behind an API Gateway WebSocket API with
 * routes: $connect, $disconnect, subscribe
 *
 * DynamoDB table: AceRevWsConnections
 *   PK: transactionId  (string)
 *   SK: connectionId   (string)
 *   GSI: connectionId-index (PK=connectionId) — used for $disconnect cleanup
 *   TTL attribute: ttl (number, unix seconds)
 */

import {
  APIGatewayProxyEvent,
  APIGatewayProxyResult,
} from 'aws-lambda';
import {
  DynamoDBClient,
  PutItemCommand,
  DeleteItemCommand,
  QueryCommand,
} from '@aws-sdk/client-dynamodb';

const REGION     = process.env.AWS_REGION ?? 'ap-southeast-1';
const TABLE_NAME = process.env.WS_CONNECTIONS_TABLE ?? 'AceRevWsConnections';
const TTL_HOURS  = 2; // connections older than 2 h are auto-expired

const dynamo = new DynamoDBClient({ region: REGION });

// ---------------------------------------------------------------------------

export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const ctx            = event.requestContext as any;
  const routeKey       = ctx.routeKey   as string;
  const connectionId   = ctx.connectionId as string;

  try {
    switch (routeKey) {
      case '$connect':
        // Nothing to store yet — transactionId arrives in first subscribe message
        return ok('Connected');

      case '$disconnect':
        await deleteByConnection(connectionId);
        return ok('Disconnected');

      case 'subscribe':
      case '$default': {
        const body = parseBody(event.body);
        if (body.action === 'subscribe' && body.transactionId) {
          await storeConnection(body.transactionId, connectionId);
          return ok('Subscribed');
        }
        if (body.action === 'stop' && body.transactionId) {
          // Driver stopped — delete mapping so no further pushes arrive
          await deleteConnection(body.transactionId, connectionId);
          return ok('Unsubscribed');
        }
        return ok('Unknown action');
      }

      default:
        return ok('No handler for ' + routeKey);
    }
  } catch (err: any) {
    console.error('WebSocketHandler error:', err.message);
    return { statusCode: 500, body: 'Internal error' };
  }
}

// ── DynamoDB helpers ─────────────────────────────────────────────────────────

async function storeConnection(
  transactionId: string,
  connectionId: string
): Promise<void> {
  const ttl = Math.floor(Date.now() / 1000) + TTL_HOURS * 3600;
  await dynamo.send(new PutItemCommand({
    TableName: TABLE_NAME,
    Item: {
      transactionId: { S: transactionId },
      connectionId:  { S: connectionId  },
      ttl:           { N: String(ttl)   },
    },
  }));
}

async function deleteConnection(
  transactionId: string,
  connectionId: string
): Promise<void> {
  await dynamo.send(new DeleteItemCommand({
    TableName: TABLE_NAME,
    Key: {
      transactionId: { S: transactionId },
      connectionId:  { S: connectionId  },
    },
  }));
}

/**
 * On $disconnect we only know the connectionId.
 * Use the GSI (connectionId-index) to find + delete the matching item.
 */
async function deleteByConnection(connectionId: string): Promise<void> {
  const result = await dynamo.send(new QueryCommand({
    TableName:              TABLE_NAME,
    IndexName:              'connectionId-index',
    KeyConditionExpression: 'connectionId = :cid',
    ExpressionAttributeValues: { ':cid': { S: connectionId } },
  }));

  for (const item of result.Items ?? []) {
    const txnId = item.transactionId?.S;
    if (txnId) {
      await deleteConnection(txnId, connectionId);
    }
  }
}

// ── Utility ──────────────────────────────────────────────────────────────────

function ok(body: string): APIGatewayProxyResult {
  return { statusCode: 200, body };
}

function parseBody(raw: string | null): Record<string, any> {
  try {
    return JSON.parse(raw ?? '{}');
  } catch {
    return {};
  }
}
