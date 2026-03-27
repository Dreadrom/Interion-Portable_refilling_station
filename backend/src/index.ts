/**
 * Express Server for Portable Refill Station Backend
 * Wraps AWS Lambda handlers for traditional Node.js deployment
 */

import 'dotenv/config';
import express, { Request, Response } from 'express';
import cors from 'cors';
import { APIGatewayProxyEvent, APIGatewayProxyResult } from 'aws-lambda';
import { handler as authHandler } from './handlers/AuthHandler';
import { handler as stationsHandler } from './handlers/StationsHandler';

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Request logging
app.use((req, res, next) => {
  console.log(`[${new Date().toISOString()}] ${req.method} ${req.path}`);
  next();
});

/**
 * Convert Express request to Lambda APIGatewayProxyEvent
 */
function convertToLambdaEvent(req: Request): APIGatewayProxyEvent {
  return {
    httpMethod: req.method,
    path: req.path,
    headers: req.headers as { [name: string]: string },
    queryStringParameters: req.query as { [name: string]: string } | null,
    body: req.body ? JSON.stringify(req.body) : null,
    isBase64Encoded: false,
    pathParameters: req.params as { [name: string]: string } | null,
    stageVariables: null,
    requestContext: {
      accountId: '',
      apiId: '',
      protocol: 'HTTP/1.1',
      httpMethod: req.method,
      path: req.path,
      stage: 'prod',
      requestId: Math.random().toString(36).substring(7),
      requestTime: new Date().toISOString(),
      requestTimeEpoch: Date.now(),
      identity: {
        sourceIp: req.ip || '',
        userAgent: req.get('user-agent') || '',
        cognitoIdentityPoolId: null,
        cognitoIdentityId: null,
        cognitoAuthenticationType: null,
        cognitoAuthenticationProvider: null,
        accountId: null,
        caller: null,
        apiKey: null,
        apiKeyId: null,
        accessKey: null,
        principalOrgId: null,
        user: null,
        userArn: null,
        clientCert: null,
      },
      resourceId: '',
      resourcePath: req.route?.path || req.path,
      domainName: req.hostname,
      domainPrefix: '',
      authorizer: null,
    },
    resource: '',
    multiValueHeaders: {},
    multiValueQueryStringParameters: null,
  };
}

/**
 * Convert Lambda response to Express response
 */
function sendLambdaResponse(res: Response, lambdaResult: APIGatewayProxyResult) {
  res.status(lambdaResult.statusCode);
  
  if (lambdaResult.headers) {
    Object.entries(lambdaResult.headers).forEach(([key, value]) => {
      res.setHeader(key, value as string);
    });
  }
  
  res.send(lambdaResult.body);
}

/**
 * Create Express handler from Lambda handler
 */
function wrapLambdaHandler(lambdaHandler: (event: APIGatewayProxyEvent) => Promise<APIGatewayProxyResult>) {
  return async (req: Request, res: Response) => {
    try {
      const lambdaEvent = convertToLambdaEvent(req);
      const lambdaResult = await lambdaHandler(lambdaEvent);
      sendLambdaResponse(res, lambdaResult);
    } catch (error) {
      console.error('Handler error:', error);
      res.status(500).json({
        error: 'INTERNAL_SERVER_ERROR',
        message: error instanceof Error ? error.message : 'Internal server error',
      });
    }
  };
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
  });
});

// Auth routes
app.post('/auth/register', wrapLambdaHandler(authHandler));
app.post('/auth/login', wrapLambdaHandler(authHandler));
app.get('/auth/me', wrapLambdaHandler(authHandler));
app.post('/auth/forgot-password', wrapLambdaHandler(authHandler));
app.post('/auth/reset-password', wrapLambdaHandler(authHandler));
app.post('/auth/refresh', wrapLambdaHandler(authHandler));
app.post('/auth/logout', wrapLambdaHandler(authHandler));

// Station routes
app.get('/stations', wrapLambdaHandler(stationsHandler));
app.get('/stations/:id', wrapLambdaHandler(stationsHandler));
app.get('/stations/:id/tanks', wrapLambdaHandler(stationsHandler));
app.get('/stations/:id/prices', wrapLambdaHandler(stationsHandler));
app.get('/stations/:id/totalizers', wrapLambdaHandler(stationsHandler));

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    error: 'NOT_FOUND',
    message: `Route ${req.method} ${req.path} not found`,
  });
});

// Error handler
app.use((err: Error, req: Request, res: Response, next: any) => {
  console.error('Unhandled error:', err);
  res.status(500).json({
    error: 'INTERNAL_SERVER_ERROR',
    message: err.message || 'Internal server error',
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`
╔════════════════════════════════════════════════╗
║   Portable Refill Station API Server          ║
║   Server running on http://localhost:${PORT}    ║
║   Environment: ${process.env.NODE_ENV || 'development'}                  ║
╚════════════════════════════════════════════════╝
  `);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down gracefully...');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('SIGINT received, shutting down gracefully...');
  process.exit(0);
});
