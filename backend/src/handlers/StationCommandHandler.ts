/**
 * StationCommandHandler — REST API to publish dispense commands to a station
 *
 * Exposed as:
 *   POST /stations/{stationId}/command
 *
 * This endpoint validates the caller's JWT, then publishes a JSON command
 * to the station's MQTT topic via AWS IoT Core Data Plane.
 *
 * MQTT command topic:  acerev/stations/{stationId}/commands
 *
 * Supported actions:
 *   AUTHORIZE    — start a new dispensing session (creates DB transaction)
 *   STOP         — abort the current session (emergency stop)
 *   PING         — health check; station replies on telemetry/status
 *
 * Station MQTT command payload:
 * {
 *   action:        'AUTHORIZE' | 'STOP' | 'PING',
 *   transactionId: string,    // UUID created here for AUTHORIZE
 *   maxVolumeLitres?: number, // max allowed volume for AUTHORIZE
 *   timestamp:     string,    // ISO 8601
 * }
 */

import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { IoTDataPlaneClient, PublishCommand } from '@aws-sdk/client-iot-data-plane';
import { verifyToken } from '../utils/jwt';
import { successResponse, errorResponse } from '../utils/response';
import { getPool } from '../database/connection';
import crypto       from 'crypto';

const REGION        = process.env.AWS_REGION          ?? 'ap-southeast-1';
// Set to your IoT ATS endpoint, e.g.: abc123-ats.iot.ap-southeast-1.amazonaws.com
const IOT_ENDPOINT  = process.env.IOT_ENDPOINT!;

const iot = new IoTDataPlaneClient({ region: REGION, endpoint: `https://${IOT_ENDPOINT}` });

// ---------------------------------------------------------------------------

type CommandAction = 'AUTHORIZE' | 'STOP' | 'PING';

interface CommandRequest {
  action:           CommandAction;
  maxVolumeLitres?: number;   // required for AUTHORIZE
  transactionId?:  string;   // required for STOP (provided by client)
}

export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {

  // --- Auth ---
  const authHeader = event.headers?.Authorization ?? event.headers?.authorization ?? '';
  const token      = authHeader.replace(/^Bearer\s+/i, '');
  if (!token) return errorResponse('UNAUTHORIZED', 'Missing Authorization header', 401);

  let userId: number;
  try {
    const decoded = verifyToken(token);
    userId = (decoded as any).id ?? (decoded as any).userId;
  } catch {
    return errorResponse('INVALID_TOKEN', 'Invalid or expired token', 401);
  }

  // --- Route params ---
  const stationId = event.pathParameters?.stationId;
  if (!stationId) return errorResponse('BAD_REQUEST', 'Missing stationId', 400);

  // --- Body ---
  let body: CommandRequest;
  try {
    body = JSON.parse(event.body ?? '{}') as CommandRequest;
  } catch {
    return errorResponse('BAD_REQUEST', 'Invalid JSON body', 400);
  }

  const { action, maxVolumeLitres } = body;
  if (!action || !['AUTHORIZE', 'STOP', 'PING'].includes(action)) {
    return errorResponse('BAD_REQUEST', 'action must be AUTHORIZE | STOP | PING', 400);
  }

  // --- Handle each action ---
  const topic = `acerev/stations/${stationId}/commands`;
  const pool = getPool();
  let transactionId: string | undefined;

  if (action === 'AUTHORIZE') {
    if (!maxVolumeLitres || maxVolumeLitres <= 0) {
      return errorResponse('BAD_REQUEST', 'maxVolumeLitres required and > 0 for AUTHORIZE', 400);
    }

    // Create transaction record in PostgreSQL
    transactionId = crypto.randomUUID();
    await pool.query(
      `INSERT INTO transactions
         (id, user_id, station_id, status, max_volume_litres, created_at)
       VALUES ($1, $2, $3, 'PENDING_AUTH', $4, NOW())`,
      [transactionId, userId, stationId, maxVolumeLitres]
    );

    await publishCommand(topic, {
      action,
      transactionId,
      maxVolumeLitres,
    });

    return successResponse(
      { transactionId, stationId, status: 'AUTHORIZING' },
      200,
      'Authorize command sent to station'
    );
  }

  if (action === 'STOP') {
    transactionId = body.transactionId;
    if (!transactionId) return errorResponse('BAD_REQUEST', 'transactionId required for STOP', 400);

    // Verify this transaction belongs to the caller
    const { rows } = await pool.query(
      `SELECT id FROM transactions WHERE id = $1 AND user_id = $2`,
      [transactionId, userId]
    );
    if (rows.length === 0) return errorResponse('NOT_FOUND', 'Transaction not found', 404);

    await publishCommand(topic, { action, transactionId });
    await pool.query(
      `UPDATE transactions SET status = 'STOPPED', stopped_at = NOW() WHERE id = $1`,
      [transactionId]
    );

    return successResponse({ transactionId, status: 'STOPPED' }, 200, 'Stop command sent');
  }

  if (action === 'PING') {
    transactionId = crypto.randomUUID();
    await publishCommand(topic, { action, transactionId });
    return successResponse({ stationId, pingSent: true }, 200, 'Ping sent to station');
  }

  return errorResponse('BAD_REQUEST', 'Unhandled action', 400);
}

// ---------------------------------------------------------------------------

async function publishCommand(
  topic: string,
  payload: Record<string, unknown>
): Promise<void> {
  const body = JSON.stringify({ ...payload, timestamp: new Date().toISOString() });
  await iot.send(new PublishCommand({
    topic,
    payload:  Buffer.from(body),
    qos:      1,  // at-least-once delivery
  }));
}
