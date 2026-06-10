/**
 * Payment Handler
 * Handles Fiuu payment integration for wallet top-up
 */

import {
  APIGatewayProxyEvent,
  APIGatewayProxyResult,
} from 'aws-lambda';
import { v4 as uuidv4 } from 'uuid';
import { query, queryOne, getPool } from '../database/connection';
import { verifyToken } from '../utils/jwt';
import { generateFiuuVcode, verifyFiuuSkey } from '../utils/crypto';

// Fiuu configuration
const FIUU_CONFIG = {
  merchantId: process.env.FIUU_MERCHANT_ID || 'SB_bluediesel',
  verifyKey: process.env.FIUU_VERIFY_KEY || 'f90028941214219e6d815fe27efd2937',
  secretKey: process.env.FIUU_SECRET_KEY || 'dc66f1d6cd273b828dace4f8ada74dd8',
  sandboxBaseUrl: 'https://sandbox-payment.fiuu.com/RMS/pay',
  productionBaseUrl: 'https://pay.fiuu.com/RMS/pay',
  isSandbox: process.env.FIUU_SANDBOX !== 'false', // default to sandbox
  returnUrl: process.env.FIUU_RETURN_URL || 'https://your-domain.com/payment/return',
  callbackUrl: process.env.FIUU_CALLBACK_URL || 'https://your-domain.com/payment/fiuu/callback',
  notificationUrl: process.env.FIUU_NOTIFICATION_URL || 'https://your-domain.com/payment/fiuu/notify',
  cancelUrl: process.env.FIUU_CANCEL_URL || 'https://your-domain.com/payment/cancel',
};

interface Payment {
  paymentid: string;
  userid: string;
  amount: string;
  currency: string;
  gatewaytransactionid?: string;
  gatewayname: string;
  status: string;
  paymentmethod?: string;
  createdat: Date;
  completedat?: Date;
  metadata?: any;
}

interface Wallet {
  walletid: string;
  userid: string;
  balance: string;
  currency: string;
}

/**
 * Main handler for payment routes
 */
export async function handler(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const path = event.path;
  const method = event.httpMethod;

  console.log(`[PaymentHandler] ${method} ${path}`);

  try {
    // Route to appropriate handler
    if (path === '/payment/create' && method === 'POST') {
      return await handleCreatePayment(event);
    }

    if (path.startsWith('/payment/') && path !== '/payment/fiuu/return' && 
        path !== '/payment/fiuu/notify' && path !== '/payment/fiuu/callback' && 
        method === 'GET') {
      return await handleGetPayment(event);
    }

    if (path === '/payment/fiuu/return' && method === 'POST') {
      return await handleFiuuReturn(event);
    }

    if (path === '/payment/fiuu/notify' && method === 'POST') {
      return await handleFiuuNotification(event);
    }

    if (path === '/payment/fiuu/callback' && method === 'POST') {
      return await handleFiuuCallback(event);
    }

    return {
      statusCode: 404,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Route not found' }),
    };
  } catch (error: any) {
    console.error('[PaymentHandler] Error:', error);
    return {
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        error: 'Internal server error',
        message: error.message,
      }),
    };
  }
}

/**
 * POST /payment/create
 * Create a new payment and return Fiuu hosted payment URL
 */
async function handleCreatePayment(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Verify authentication
  const authHeader = event.headers.Authorization || event.headers.authorization;
  if (!authHeader) {
    return {
      statusCode: 401,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Missing authorization header' }),
    };
  }

  const token = authHeader.replace('Bearer ', '');
  const decoded = verifyToken(token);
  if (!decoded || !decoded.userId) {
    return {
      statusCode: 401,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Invalid or expired token' }),
    };
  }

  const userId = decoded.userId;

  // Parse request body
  const body = JSON.parse(event.body || '{}');
  const { amount, currency = 'MYR', method: paymentMethod, channel, stationId, description, metadata } = body;

  if (!amount || isNaN(parseFloat(amount)) || parseFloat(amount) <= 0) {
    return {
      statusCode: 400,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Invalid amount' }),
    };
  }

  // Get user details for bill_name and bill_email
  const user = await queryOne<any>(
    `SELECT UserID as userid, UserName as name, UserEmail as email, UserPhone as phone
     FROM Users
     WHERE UserID = $1`,
    [userId]
  );

  if (!user) {
    return {
      statusCode: 404,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'User not found' }),
    };
  }

  // Create payment record
  const paymentId = uuidv4();
  const amountFormatted = parseFloat(amount).toFixed(2);

  await query(
    `INSERT INTO Payments 
     (PaymentID, UserID, Amount, Currency, GatewayName, Status, PaymentMethod, Metadata)
     VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
    [
      paymentId,
      userId,
      amountFormatted,
      currency,
      'FIUU',
      'PENDING',
      paymentMethod || 'FIUU_HOSTED',
      JSON.stringify({
        channel: channel || null,
        createdVia: 'api',
        stationId: stationId || null,
        description: description || null,
        originalMetadata: metadata || null,
      }),
    ]
  );

  // Generate Fiuu vcode
  const vcode = generateFiuuVcode(
    amountFormatted,
    FIUU_CONFIG.merchantId,
    paymentId,
    FIUU_CONFIG.verifyKey,
    currency
  );

  // Build Fiuu hosted payment URL
  const baseUrl = FIUU_CONFIG.isSandbox
    ? FIUU_CONFIG.sandboxBaseUrl
    : FIUU_CONFIG.productionBaseUrl;

  let paymentUrl = `${baseUrl}/${FIUU_CONFIG.merchantId}/`;

  // Add channel-specific path if provided
  if (channel) {
    paymentUrl += `${channel}.php`;
  }

  // Build payment form data
  const paymentData = {
    amount: amountFormatted,
    orderid: paymentId,
    bill_name: user.name || 'Customer',
    bill_email: user.email,
    bill_mobile: user.phone || '60123456789',
    bill_desc: 'Wallet Top Up',
    country: 'MY',
    currency: currency,
    vcode: vcode,
    returnurl: FIUU_CONFIG.returnUrl,
    callbackurl: FIUU_CONFIG.callbackUrl,
    notificationurl: FIUU_CONFIG.notificationUrl,
    cancelurl: FIUU_CONFIG.cancelUrl,
  };

  // If channel is specified, add it
  if (channel) {
    (paymentData as any).channel = channel;
  }

  console.log('[PaymentHandler] Created payment:', paymentId);

  // Match frontend Payment interface
  const payment = {
    id: paymentId,
    userId: userId,
    stationId: stationId || '',
    amount: parseFloat(amountFormatted),
    currency: currency,
    status: 'PENDING' as const,
    method: paymentMethod || 'FIUU_HOSTED',
    paymentUrl: paymentUrl,
    paymentData: paymentData,
    createdAt: new Date().toISOString(),
    expiresAt: new Date(Date.now() + 30 * 60 * 1000).toISOString(), // 30 minutes
    description: description,
    metadata: {
      channel: channel || null,
      stationId: stationId || null,
    },
  };

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      payment,
      message: 'Payment created. Redirect user to paymentUrl with paymentData.',
    }),
  };
}

/**
 * GET /payment/:id
 * Get payment status
 */
async function handleGetPayment(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  // Verify authentication
  const authHeader = event.headers.Authorization || event.headers.authorization;
  if (!authHeader) {
    return {
      statusCode: 401,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Missing authorization header' }),
    };
  }

  const token = authHeader.replace('Bearer ', '');
  const decoded = verifyToken(token);
  if (!decoded || !decoded.userId) {
    return {
      statusCode: 401,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Invalid or expired token' }),
    };
  }

  const userId = decoded.userId;

  // Extract payment ID from path
  const pathParts = event.path.split('/');
  const paymentId = pathParts[pathParts.length - 1];

  if (!paymentId) {
    return {
      statusCode: 400,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Missing payment ID' }),
    };
  }

  // Get payment
  const paymentRow = await queryOne<Payment>(
    `SELECT PaymentID as paymentid, UserID as userid, Amount as amount, 
            Currency as currency, GatewayTransactionID as gatewaytransactionid,
            Status as status, PaymentMethod as paymentmethod, 
            CreatedAt as createdat, CompletedAt as completedat, Metadata as metadata
     FROM Payments 
     WHERE PaymentID = $1 AND UserID = $2`,
    [paymentId, userId]
  );

  if (!paymentRow) {
    return {
      statusCode: 404,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ error: 'Payment not found' }),
    };
  }

  // Map backend status to frontend status
  const frontendStatus = mapStatusToFrontend(paymentRow.status);
  const paymentMetadata = paymentRow.metadata || {};

  // Match frontend Payment interface
  const payment = {
    id: paymentRow.paymentid,
    userId: paymentRow.userid,
    stationId: paymentMetadata.stationId || '',
    amount: parseFloat(paymentRow.amount),
    currency: paymentRow.currency,
    status: frontendStatus,
    method: paymentRow.paymentmethod || 'FIUU_HOSTED',
    fiuuPaymentId: paymentRow.gatewaytransactionid,
    createdAt: paymentRow.createdat,
    completedAt: paymentRow.completedat,
    expiresAt: new Date(new Date(paymentRow.createdat).getTime() + 30 * 60 * 1000).toISOString(),
    description: paymentMetadata.description,
    metadata: paymentMetadata,
  };

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ payment }),
  };
}

/**
 * POST /payment/fiuu/return
 * Handle Fiuu return URL (browser redirect)
 */
async function handleFiuuReturn(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = event.body || '';
  const params = new URLSearchParams(body);

  const tranID = params.get('tranID') || '';
  const orderid = params.get('orderid') || '';
  const status = params.get('status') || '';
  const domain = params.get('domain') || '';
  const amount = params.get('amount') || '';
  const currency = params.get('currency') || '';
  const paydate = params.get('paydate') || '';
  const appcode = params.get('appcode') || '';
  const skey = params.get('skey') || '';

  console.log('[PaymentHandler] Return URL hit:', { orderid, tranID, status });

  // Verify skey
  const isValid = verifyFiuuSkey(
    tranID,
    orderid,
    status,
    domain,
    amount,
    currency,
    paydate,
    appcode,
    FIUU_CONFIG.secretKey,
    skey
  );

  if (!isValid) {
    console.error('[PaymentHandler] Invalid skey in return URL');
    return {
      statusCode: 400,
      headers: { 'Content-Type': 'text/html' },
      body: '<html><body><h1>Payment verification failed</h1></body></html>',
    };
  }

  // Update payment status
  await updatePaymentStatus(orderid, tranID, status, params);

  // Return a simple HTML page
  const statusMessage = status === '00' ? 'Payment Successful' :
                        status === '11' ? 'Payment Failed' :
                        'Payment Pending';

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'text/html' },
    body: `
      <html>
        <head>
          <title>${statusMessage}</title>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <style>
            body { font-family: sans-serif; text-align: center; padding: 50px; }
            h1 { color: ${status === '00' ? '#10B981' : status === '11' ? '#EF4444' : '#F59E0B'}; }
          </style>
        </head>
        <body>
          <h1>${statusMessage}</h1>
          <p>Transaction ID: ${tranID}</p>
          <p>Order ID: ${orderid}</p>
          <p>You can close this window and return to the app.</p>
        </body>
      </html>
    `,
  };
}

/**
 * POST /payment/fiuu/notify
 * Handle Fiuu notification URL (IPN)
 */
async function handleFiuuNotification(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = event.body || '';
  const params = new URLSearchParams(body);

  const tranID = params.get('tranID') || '';
  const orderid = params.get('orderid') || '';
  const status = params.get('status') || '';
  const domain = params.get('domain') || '';
  const amount = params.get('amount') || '';
  const currency = params.get('currency') || '';
  const paydate = params.get('paydate') || '';
  const appcode = params.get('appcode') || '';
  const skey = params.get('skey') || '';

  console.log('[PaymentHandler] Notification URL hit:', { orderid, tranID, status });

  // Verify skey
  const isValid = verifyFiuuSkey(
    tranID,
    orderid,
    status,
    domain,
    amount,
    currency,
    paydate,
    appcode,
    FIUU_CONFIG.secretKey,
    skey
  );

  if (!isValid) {
    console.error('[PaymentHandler] Invalid skey in notification');
    return {
      statusCode: 400,
      headers: { 'Content-Type': 'text/plain' },
      body: 'Invalid signature',
    };
  }

  // Update payment status
  await updatePaymentStatus(orderid, tranID, status, params);

  // Send IPN acknowledgment if configured
  // According to Fiuu docs, backend should POST back to Fiuu with treq=1
  // This is optional but recommended for production
  console.log('[PaymentHandler] Notification processed successfully');

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'text/plain' },
    body: 'OK',
  };
}

/**
 * POST /payment/fiuu/callback
 * Handle Fiuu callback URL
 */
async function handleFiuuCallback(
  event: APIGatewayProxyEvent
): Promise<APIGatewayProxyResult> {
  const body = event.body || '';
  const params = new URLSearchParams(body);

  const tranID = params.get('tranID') || '';
  const orderid = params.get('orderid') || '';
  const status = params.get('status') || '';
  const domain = params.get('domain') || '';
  const amount = params.get('amount') || '';
  const currency = params.get('currency') || '';
  const paydate = params.get('paydate') || '';
  const appcode = params.get('appcode') || '';
  const skey = params.get('skey') || '';

  console.log('[PaymentHandler] Callback URL hit:', { orderid, tranID, status });

  // Verify skey
  const isValid = verifyFiuuSkey(
    tranID,
    orderid,
    status,
    domain,
    amount,
    currency,
    paydate,
    appcode,
    FIUU_CONFIG.secretKey,
    skey
  );

  if (!isValid) {
    console.error('[PaymentHandler] Invalid skey in callback');
    return {
      statusCode: 400,
      headers: { 'Content-Type': 'text/plain' },
      body: 'Invalid signature',
    };
  }

  // Update payment status
  await updatePaymentStatus(orderid, tranID, status, params);

  // Return callback acknowledgment token
  // Fiuu requires exact response: CBTOKEN:MPSTATOK
  console.log('[PaymentHandler] Callback processed successfully');

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'text/plain' },
    body: 'CBTOKEN:MPSTATOK',
  };
}

/**
 * Update payment status and credit wallet if successful
 */
async function updatePaymentStatus(
  orderid: string,
  tranID: string,
  status: string,
  params: URLSearchParams
): Promise<void> {
  const pool = getPool();
  const client = await pool.connect();

  try {
    await client.query('BEGIN');

    // Get current payment
    const paymentResult = await client.query<Payment>(
      `SELECT PaymentID as paymentid, UserID as userid, Amount as amount, 
              Currency as currency, Status as status
       FROM Payments 
       WHERE PaymentID = $1 FOR UPDATE`,
      [orderid]
    );

    if (paymentResult.rows.length === 0) {
      console.error('[PaymentHandler] Payment not found:', orderid);
      await client.query('ROLLBACK');
      return;
    }

    const payment = paymentResult.rows[0];

    // Skip if already completed (idempotency)
    if (payment.status === 'COMPLETED' || payment.status === 'FAILED') {
      console.log('[PaymentHandler] Payment already finalized:', orderid);
      await client.query('ROLLBACK');
      return;
    }

    // Map Fiuu status to internal status
    let newStatus = 'PENDING';
    if (status === '00') {
      newStatus = 'COMPLETED';
    } else if (status === '11') {
      newStatus = 'FAILED';
    } else if (status === '22') {
      newStatus = 'PENDING';
    }

    // Collect all Fiuu params for metadata
    const fiuuData: any = {};
    params.forEach((value, key) => {
      fiuuData[key] = value;
    });

    // Update payment
    await client.query(
      `UPDATE Payments 
       SET Status = $1, 
         GatewayTransactionID = $2, 
         CompletedAt = $3,
         Metadata = Metadata || $4::jsonb
       WHERE PaymentID = $5`,
      [
        newStatus,
        tranID,
        newStatus === 'COMPLETED' || newStatus === 'FAILED' ? new Date() : null,
        JSON.stringify({ fiuuResponse: fiuuData }),
        orderid,
      ]
    );

    // Credit wallet if successful
    if (status === '00') {
      // Get or create wallet
      let walletResult = await client.query<Wallet>(
        `SELECT WalletID as walletid, UserID as userid, Balance as balance, Currency as currency
         FROM Wallets 
         WHERE UserID = $1 FOR UPDATE`,
        [payment.userid]
      );

      let wallet: Wallet;
      if (walletResult.rows.length === 0) {
        // Create wallet
        const walletId = uuidv4();
        await client.query(
          `INSERT INTO Wallets (WalletID, UserID, Balance, Currency)
           VALUES ($1, $2, 0, $3)`,
          [walletId, payment.userid, payment.currency]
        );
        wallet = {
          walletid: walletId,
          userid: payment.userid,
          balance: '0.00',
          currency: payment.currency,
        };
      } else {
        wallet = walletResult.rows[0];
      }

      const balanceBefore = parseFloat(wallet.balance);
      const balanceAfter = balanceBefore + parseFloat(payment.amount);

      // Update wallet balance
      await client.query(
        `UPDATE Wallets 
         SET Balance = $1, UpdatedAt = CURRENT_TIMESTAMP
         WHERE WalletID = $2`,
        [balanceAfter.toFixed(2), wallet.walletid]
      );

      // Insert wallet transaction
      const walletTxId = uuidv4();
      await client.query(
        `INSERT INTO WalletTransactions 
         (WalletTxID, WalletID, Type, Amount, BalanceBefore, BalanceAfter, Reference, Description)
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8)`,
        [
          walletTxId,
          wallet.walletid,
          'TOP_UP',
          payment.amount,
          balanceBefore.toFixed(2),
          balanceAfter.toFixed(2),
          orderid,
          `Fiuu payment - TranID: ${tranID}`,
        ]
      );

      console.log('[PaymentHandler] Wallet credited:', {
        userId: payment.userid,
        amount: payment.amount,
        newBalance: balanceAfter.toFixed(2),
      });
    }

    await client.query('COMMIT');
    console.log('[PaymentHandler] Payment updated:', orderid, newStatus);
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('[PaymentHandler] Error updating payment:', error);
    throw error;
  } finally {
    client.release();
  }
}

/**
 * Map internal status to frontend status names
 */
function mapStatusToFrontend(status: string): string {
  const statusMap: { [key: string]: string } = {
    PENDING: 'PENDING',
    PROCESSING: 'PENDING',
    COMPLETED: 'SUCCESS',
    FAILED: 'FAILED',
    CANCELLED: 'CANCELLED',
    REFUNDED: 'REFUNDED',
  };
  return statusMap[status] || status;
}
