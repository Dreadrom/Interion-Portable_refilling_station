/**
 * Transactions Handler
 * Handles transaction-related API endpoints
 */

import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { query } from '../database/connection';
import { extractToken, verifyToken } from '../utils/jwt';
import { successResponse, errorResponse, unauthorizedError } from '../utils/response';

interface UserTransaction {
  transactionid: string;
  stationid: string;
  userid: string;
  product: string;
  volume: number;
  amount: number;
  currency: string;
  status: string;
  paymentmethod: string;
  paymentreference: string;
  startedat: string;
  completedat: string;
  createdat: string;
}

/**
 * Main handler function
 */
export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const path = event.path;
  const method = event.httpMethod;

  try {
    // Get transactions for current user
    if (path === '/transactions' && method === 'GET') {
      return await handleGetTransactions(event);
    }

    // Get specific transaction
    if (path.startsWith('/transactions/') && method === 'GET') {
      const transactionId = path.split('/')[2];
      return await handleGetTransaction(event, transactionId);
    }

    return errorResponse('NOT_FOUND', 'Endpoint not found', 404);
  } catch (error) {
    console.error('Transaction handler error:', error);
    return errorResponse(
      'INTERNAL_ERROR',
      error instanceof Error ? error.message : 'Internal server error'
    );
  }
}

/**
 * Get all transactions for current user
 */
async function handleGetTransactions(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Extract and verify token
  const token = extractToken(event.headers?.Authorization || event.headers?.authorization);
  if (!token) {
    return unauthorizedError('No token provided');
  }

  const decoded = verifyToken(token);
  if (!decoded) {
    return unauthorizedError('Invalid or expired token');
  }

  const userId = decoded.userId;

  try {
    // Get transactions for user
    const result = await query<UserTransaction[]>(
      `SELECT 
        TransactionID as transactionid,
        StationID as stationid,
        UserID as userid,
        Product as product,
        Volume as volume,
        Amount as amount,
        Currency as currency,
        Status as status,
        PaymentMethod as paymentmethod,
        PaymentReference as paymentreference,
        StartedAt as startedat,
        CompletedAt as completedat,
        CreatedAt as createdat
      FROM Transactions
      WHERE UserID = $1
      ORDER BY CreatedAt DESC
      LIMIT 100`,
      [userId]
    );

    // Ensure result is an array
    const rows = Array.isArray(result) ? result : [];
    
    const transactions = rows.map((tx: UserTransaction) => ({
      id: tx.transactionid,
      stationId: tx.stationid,
      userId: tx.userid,
      product: tx.product,
      volume: parseFloat(tx.volume as any),
      amount: parseFloat(tx.amount as any),
      currency: tx.currency,
      status: tx.status,
      paymentMethod: tx.paymentmethod,
      paymentReference: tx.paymentreference,
      startedAt: tx.startedat,
      completedAt: tx.completedat,
      createdAt: tx.createdat,
    }));

    return successResponse({ transactions });
  } catch (error) {
    console.error('Get transactions error:', error);
    return errorResponse(
      'DATABASE_ERROR',
      error instanceof Error ? error.message : 'Failed to fetch transactions'
    );
  }
}

/**
 * Get specific transaction by ID
 */
async function handleGetTransaction(
  event: APIGatewayProxyEvent,
  transactionId: string
): Promise<APIGatewayProxyResult> {
  // Extract and verify token
  const token = extractToken(event.headers?.Authorization || event.headers?.authorization);
  if (!token) {
    return unauthorizedError('No token provided');
  }

  const decoded = verifyToken(token);
  if (!decoded) {
    return unauthorizedError('Invalid or expired token');
  }

  const userId = decoded.userId;

  try {
    // Get transaction by ID (ensure it belongs to user)
    const result = await query<UserTransaction[]>(
      `SELECT 
        TransactionID as transactionid,
        StationID as stationid,
        UserID as userid,
        Product as product,
        Volume as volume,
        Amount as amount,
        Currency as currency,
        Status as status,
        PaymentMethod as paymentmethod,
        PaymentReference as paymentreference,
        StartedAt as startedat,
        CompletedAt as completedat,
        CreatedAt as createdat
      FROM Transactions
      WHERE TransactionID = $1 AND UserID = $2`,
      [transactionId, userId]
    );

    if (result.length === 0) {
      return errorResponse('NOT_FOUND', 'Transaction not found', 404);
    }

    const tx = result[0];
    const transaction = {
      id: tx.transactionid,
      stationId: tx.stationid,
      userId: tx.userid,
      product: tx.product,
      volume: parseFloat(tx.volume as any),
      amount: parseFloat(tx.amount as any),
      currency: tx.currency,
      status: tx.status,
      paymentMethod: tx.paymentmethod,
      paymentReference: tx.paymentreference,
      startedAt: tx.startedat,
      completedAt: tx.completedat,
      createdAt: tx.createdat,
    };

    return successResponse({ transaction });
  } catch (error) {
    console.error('Get transaction error:', error);
    return errorResponse(
      'DATABASE_ERROR',
      error instanceof Error ? error.message : 'Failed to fetch transaction'
    );
  }
}
