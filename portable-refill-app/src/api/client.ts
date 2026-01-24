/**
 * API Client - Axios instance with interceptors
 */

import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';
import * as SecureStore from 'expo-secure-store';
import { env, isDevelopment } from '../config/env';
import { ApiResponse, ApiError, HttpErrorCode } from '../types';
import { STORAGE_KEYS } from '../utils/constants';

/**
 * Create axios instance
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: env.apiBaseUrl,
  timeout: env.apiTimeout,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Get stored auth token
 */
async function getAuthToken(): Promise<string | null> {
  try {
    return await SecureStore.getItemAsync(STORAGE_KEYS.AUTH_TOKEN);
  } catch (error) {
    console.error('Error getting auth token:', error);
    return null;
  }
}

/**
 * Save auth token
 */
export async function saveAuthToken(token: string): Promise<void> {
  try {
    await SecureStore.setItemAsync(STORAGE_KEYS.AUTH_TOKEN, token);
  } catch (error) {
    console.error('Error saving auth token:', error);
  }
}

/**
 * Remove auth token
 */
export async function removeAuthToken(): Promise<void> {
  try {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.AUTH_TOKEN);
  } catch (error) {
    console.error('Error removing auth token:', error);
  }
}

/**
 * Request interceptor - Add auth token to headers
 */
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await getAuthToken();
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Log request in development
    if (isDevelopment && env.enableDebugLogging) {
      console.log('🚀 API Request:', {
        method: config.method?.toUpperCase(),
        url: config.url,
        data: config.data,
      });
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle errors and unwrap responses
 */
apiClient.interceptors.response.use(
  (response) => {
    // Log response in development
    if (isDevelopment && env.enableDebugLogging) {
      console.log('✅ API Response:', {
        url: response.config.url,
        status: response.status,
        data: response.data,
      });
    }
    
    return response;
  },
  async (error: AxiosError<ApiResponse>) => {
    // Log error in development
    if (isDevelopment) {
      console.error('❌ API Error:', {
        url: error.config?.url,
        status: error.response?.status,
        data: error.response?.data,
      });
    }
    
    // Handle 401 - Token expired, logout user
    if (error.response?.status === 401) {
      await removeAuthToken();
      // You can emit an event here to redirect to login
      // or use a global state management solution
    }
    
    return Promise.reject(normalizeError(error));
  }
);

/**
 * Normalize error to ApiError format
 */
function normalizeError(error: AxiosError<ApiResponse>): ApiError {
  // Server returned an error response
  if (error.response?.data?.error) {
    return error.response.data.error;
  }
  
  // Network error
  if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
    return {
      code: HttpErrorCode.GATEWAY_TIMEOUT,
      message: 'Request timeout. Please try again.',
    };
  }
  
  if (error.message === 'Network Error') {
    return {
      code: HttpErrorCode.SERVICE_UNAVAILABLE,
      message: 'Network error. Please check your connection.',
    };
  }
  
  // HTTP error status
  const status = error.response?.status;
  switch (status) {
    case 400:
      return {
        code: HttpErrorCode.BAD_REQUEST,
        message: error.response?.data?.message || 'Bad request',
      };
    case 401:
      return {
        code: HttpErrorCode.UNAUTHORIZED,
        message: 'Unauthorized. Please login again.',
      };
    case 403:
      return {
        code: HttpErrorCode.FORBIDDEN,
        message: 'You do not have permission to perform this action.',
      };
    case 404:
      return {
        code: HttpErrorCode.NOT_FOUND,
        message: 'Resource not found.',
      };
    case 409:
      return {
        code: HttpErrorCode.CONFLICT,
        message: error.response?.data?.message || 'Conflict',
      };
    case 422:
      return {
        code: HttpErrorCode.VALIDATION_ERROR,
        message: error.response?.data?.message || 'Validation error',
        details: error.response?.data?.error?.details,
      };
    case 500:
    case 502:
    case 503:
      return {
        code: HttpErrorCode.INTERNAL_SERVER_ERROR,
        message: 'Server error. Please try again later.',
      };
    default:
      return {
        code: HttpErrorCode.INTERNAL_SERVER_ERROR,
        message: error.message || 'An unexpected error occurred.',
      };
  }
}

/**
 * Helper to unwrap API response
 */
export function unwrapResponse<T>(response: ApiResponse<T>): T {
  if (!response.success) {
    throw response.error || {
      code: HttpErrorCode.INTERNAL_SERVER_ERROR,
      message: 'Unknown error',
    };
  }
  
  if (!response.data) {
    throw {
      code: HttpErrorCode.INTERNAL_SERVER_ERROR,
      message: 'No data in response',
    };
  }
  
  return response.data;
}

/**
 * Generic GET request
 */
export async function get<T>(url: string, params?: any): Promise<T> {
  const response = await apiClient.get<ApiResponse<T>>(url, { params });
  return unwrapResponse(response.data);
}

/**
 * Generic POST request
 */
export async function post<T>(url: string, data?: any): Promise<T> {
  const response = await apiClient.post<ApiResponse<T>>(url, data);
  return unwrapResponse(response.data);
}

/**
 * Generic PUT request
 */
export async function put<T>(url: string, data?: any): Promise<T> {
  const response = await apiClient.put<ApiResponse<T>>(url, data);
  return unwrapResponse(response.data);
}

/**
 * Generic PATCH request
 */
export async function patch<T>(url: string, data?: any): Promise<T> {
  const response = await apiClient.patch<ApiResponse<T>>(url, data);
  return unwrapResponse(response.data);
}

/**
 * Generic DELETE request
 */
export async function del<T>(url: string): Promise<T> {
  const response = await apiClient.delete<ApiResponse<T>>(url);
  return unwrapResponse(response.data);
}

export default apiClient;
