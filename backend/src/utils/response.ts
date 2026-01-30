/**
 * Response utilities for Lambda functions
 */

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: any;
  };
  message?: string;
  timestamp: string;
}

/**
 * Success response
 */
export function successResponse<T>(
  data: T,
  statusCode: number = 200,
  message?: string
) {
  return {
    statusCode,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Credentials': true,
    },
    body: JSON.stringify({
      success: true,
      data,
      message,
      timestamp: new Date().toISOString(),
    } as ApiResponse<T>),
  };
}

/**
 * Error response
 */
export function errorResponse(
  code: string,
  message: string,
  statusCode: number = 400,
  details?: any
) {
  return {
    statusCode,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Credentials': true,
    },
    body: JSON.stringify({
      success: false,
      error: {
        code,
        message,
        details,
      },
      timestamp: new Date().toISOString(),
    } as ApiResponse),
  };
}

/**
 * Validation error response
 */
export function validationError(message: string, field?: string) {
  return errorResponse('VALIDATION_ERROR', message, 422, { field });
}

/**
 * Unauthorized error response
 */
export function unauthorizedError(message: string = 'Unauthorized') {
  return errorResponse('UNAUTHORIZED', message, 401);
}

/**
 * Not found error response
 */
export function notFoundError(message: string = 'Resource not found') {
  return errorResponse('NOT_FOUND', message, 404);
}

/**
 * Internal server error response
 */
export function internalError(message: string = 'Internal server error') {
  return errorResponse('INTERNAL_SERVER_ERROR', message, 500);
}
