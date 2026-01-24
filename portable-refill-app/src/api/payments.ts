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

/**
 * Create a new payment
 */
export async function createPayment(data: CreatePaymentRequest): Promise<CreatePaymentResponse> {
  return post<CreatePaymentResponse>(API_ENDPOINTS.PAYMENT_CREATE, data);
}

/**
 * Get payment by ID
 */
export async function getPayment(paymentId: string): Promise<GetPaymentResponse> {
  return get<GetPaymentResponse>(API_ENDPOINTS.PAYMENT_DETAIL(paymentId));
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
