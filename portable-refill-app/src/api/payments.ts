/**
 * Payment API
 */

import { post, get } from './client';
import { 
  CreatePaymentRequest,
  CreatePaymentResponse,
  GetPaymentResponse,
  Payment,
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';
import { env } from '../config/env';

// Mock payment storage for demo mode
const mockPayments: Map<string, Payment> = new Map();

function saveMockPaymentToStorage(paymentId: string, payment: Payment) {
  mockPayments.set(paymentId, payment);
  if (typeof localStorage !== 'undefined') {
    try {
      localStorage.setItem(`mock_payment_${paymentId}`, JSON.stringify(payment));
    } catch (e) {
      console.warn('Failed to save mock payment to localStorage:', e);
    }
  }
}

function loadMockPaymentFromStorage(paymentId: string): Payment | null {
  // Check in-memory map first
  if (mockPayments.has(paymentId)) {
    return mockPayments.get(paymentId)!;
  }
  
  // Try localStorage
  if (typeof localStorage !== 'undefined') {
    try {
      const stored = localStorage.getItem(`mock_payment_${paymentId}`);
      if (stored) {
        const payment = JSON.parse(stored);
        mockPayments.set(paymentId, payment);
        return payment;
      }
    } catch (e) {
      console.warn('Failed to load mock payment from localStorage:', e);
    }
  }
  
  return null;
}

/**
 * Create a mock payment for demo mode
 */
function createMockPayment(data: CreatePaymentRequest): CreatePaymentResponse {
  const paymentId = `demo-payment-${Date.now()}`;
  const payment: Payment = {
    id: paymentId,
    userId: 'demo-user',
    stationId: data.stationId || '',
    amount: data.amount,
    currency: data.currency || 'MYR',
    status: 'PENDING',
    method: data.method || 'FIUU_FPX',
    createdAt: new Date().toISOString(),
    expiresAt: new Date(Date.now() + 30 * 60 * 1000).toISOString(),
    metadata: data.metadata,
  };

  // Create mock Fiuu payment URL (uses sandbox simulator)
  const fiuuSandboxUrl = 'https://bank-simulator.fiuu.com';
  const mockPaymentUrl = `${fiuuSandboxUrl}?amount=${data.amount}&currency=${data.currency || 'MYR'}&merchantref=${paymentId}`;

  // Store payment in mock storage
  saveMockPaymentToStorage(paymentId, payment);

  // Auto-complete payment after 3 seconds (simulate successful payment)
  setTimeout(() => {
    const storedPayment = loadMockPaymentFromStorage(paymentId);
    if (storedPayment && storedPayment.status === 'PENDING') {
      storedPayment.status = 'SUCCESS';
      storedPayment.completedAt = new Date().toISOString();
      storedPayment.fiuuPaymentId = `mock-txn-${Date.now()}`;
      saveMockPaymentToStorage(paymentId, storedPayment);
      console.log('✅ Mock payment auto-completed:', paymentId);
    }
  }, 3000);

  return {
    payment: {
      ...payment,
      paymentUrl: mockPaymentUrl,
      paymentData: {
        merchantId: 'SB_bluediesel',
        amount: data.amount.toString(),
        orderid: paymentId,
        vcode: 'mock-vcode',
      },
    },
  };
}

/**
 * Get mock payment by ID
 */
function getMockPayment(paymentId: string): GetPaymentResponse {
  const payment = loadMockPaymentFromStorage(paymentId);
  if (!payment) {
    throw new Error('Payment not found');
  }
  
  // Auto-complete pending payments after 3 seconds
  if (payment.status === 'PENDING') {
    const createdTime = new Date(payment.createdAt).getTime();
    const now = Date.now();
    if (now - createdTime >= 3000) {
      payment.status = 'SUCCESS';
      payment.completedAt = new Date().toISOString();
      payment.fiuuPaymentId = `mock-txn-${Date.now()}`;
      saveMockPaymentToStorage(paymentId, payment);
      console.log('✅ Mock payment auto-completed on retrieval:', paymentId);
    }
  }
  
  return { payment };
}

/**
 * Create a new payment
 */
export async function createPayment(data: CreatePaymentRequest): Promise<CreatePaymentResponse> {
  try {
    return await post<CreatePaymentResponse>(API_ENDPOINTS.PAYMENT_CREATE, data);
  } catch (error: any) {
    console.log('Payment creation error:', error);
    
    // If backend is not available (404, 503, etc.), use demo mode
    // Check both error.code (ApiError format) and error itself
    const errorCode = error?.code || error;
    if (errorCode === 'SERVICE_UNAVAILABLE' || errorCode === 'NOT_FOUND' || 
        errorCode === 'GATEWAY_TIMEOUT' || errorCode === 'INTERNAL_SERVER_ERROR' ||
        errorCode === 'ECONNABORTED' || errorCode === 'ETIMEDOUT') {
      console.log('✅ Backend unavailable, using demo payment mode');
      return createMockPayment(data);
    }
    
    // Re-throw other errors
    throw error;
  }
}

/**
 * Get payment by ID
 */
export async function getPayment(paymentId: string): Promise<GetPaymentResponse> {
  // Check if it's a demo payment
  if (paymentId.startsWith('demo-payment-')) {
    return getMockPayment(paymentId);
  }
  
  try {
    return await get<GetPaymentResponse>(API_ENDPOINTS.PAYMENT_DETAIL(paymentId));
  } catch (error: any) {
    // If backend is not available, try mock storage
    if (error.code === 'SERVICE_UNAVAILABLE' || error.code === 'NOT_FOUND' || 
        error.code === 'GATEWAY_TIMEOUT' || error.code === 'INTERNAL_SERVER_ERROR') {
      return getMockPayment(paymentId);
    }
    throw error;
  }
}

/**
 * Poll payment status until completed or timeout
 */
export async function pollPaymentStatus(
  paymentId: string,
  options: {
    interval?: number;
    maxAttempts?: number;
    onUpdate?: (payment: Payment) => void;
  } = {}
): Promise<Payment> {
  const { interval = 2000, maxAttempts = 60, onUpdate } = options;
  
  let attempts = 0;
  
  return new Promise((resolve, reject) => {
    const checkStatus = async () => {
      try {
        attempts++;
        const { payment } = await getPayment(paymentId);
        
        // Call update callback
        if (onUpdate) {
          onUpdate(payment);
        }
        
        // Check if payment is in terminal state
        if (payment.status === 'SUCCESS') {
          resolve(payment);
          return;
        }
        
        if (payment.status === 'FAILED' || payment.status === 'CANCELLED') {
          reject(new Error(`Payment ${payment.status.toLowerCase()}`));
          return;
        }
        
        if (payment.status === 'EXPIRED') {
          reject(new Error('Payment expired'));
          return;
        }
        
        // Check if max attempts reached
        if (attempts >= maxAttempts) {
          reject(new Error('Payment polling timeout'));
          return;
        }
        
        // Continue polling
        setTimeout(checkStatus, interval);
      } catch (error) {
        reject(error);
      }
    };
    
    checkStatus();
  });
}
