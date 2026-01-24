/**
 * Payment-related types
 */

export type PaymentStatus = 
  | 'PENDING'   // Payment created, awaiting user action
  | 'SUCCESS'   // Payment completed successfully
  | 'FAILED'    // Payment failed
  | 'EXPIRED'   // Payment expired (timeout)
  | 'CANCELLED'; // User cancelled

export type PaymentMethod = 
  | 'FIUU_QR'       // Fiuu QR code payment
  | 'FIUU_FPX'      // Fiuu online banking
  | 'FIUU_EWALLET'; // Fiuu e-wallet

export interface Payment {
  id: string; // Our internal payment ID
  userId: string;
  stationId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  method: PaymentMethod;
  
  // Fiuu-specific fields
  fiuuPaymentId?: string;
  fiuuOrderId?: string;
  qrCodeData?: string; // QR code string for display
  qrCodeImageUrl?: string; // Optional QR image URL from Fiuu
  
  // Timestamps
  createdAt: string;
  updatedAt?: string;
  expiresAt: string;
  completedAt?: string;
  
  // Additional info
  description?: string;
  metadata?: Record<string, any>;
}

export interface CreatePaymentRequest {
  stationId: string;
  amount: number;
  currency: string;
  method: PaymentMethod;
  description?: string;
  
  // Optional metadata for tracking
  metadata?: {
    product?: string;
    volumeLitres?: number;
    unitPrice?: number;
  };
}

export interface CreatePaymentResponse {
  payment: Payment;
  message?: string;
}

export interface GetPaymentResponse {
  payment: Payment;
}

// Fiuu webhook payload (for backend processing)
export interface FiuuWebhookPayload {
  amount: string;
  orderid: string;
  appcode: string;
  tranID: string;
  domain: string;
  status: string; // "00" = success, "11" = failed, "22" = pending
  currency: string;
  paydate: string;
  channel: string;
  skey: string; // Signature for validation
  error_code?: string;
  error_desc?: string;
}

export interface PaymentPollingOptions {
  interval?: number; // milliseconds, default 2000
  maxAttempts?: number; // default 60 (2 minutes at 2s interval)
  timeout?: number; // milliseconds, default 120000 (2 minutes)
}
