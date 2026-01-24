/**
 * Transaction and Dispense-related types
 */

import { ProductType } from './station';

export type TransactionStatus = 
  | 'PENDING'      // Created but not started
  | 'IN_PROGRESS'  // Currently dispensing
  | 'COMPLETED'    // Successfully completed
  | 'STOPPED'      // Manually stopped
  | 'FAILED'       // Failed/error
  | 'CANCELLED';   // Cancelled before start

export type StopReason = 
  | 'TARGET_REACHED'    // Reached preset volume/amount
  | 'USER_STOPPED'      // User pressed stop
  | 'EMERGENCY_STOP'    // Emergency stop triggered
  | 'TANK_EMPTY'        // Tank ran out
  | 'SYSTEM_ERROR'      // System/hardware error
  | 'PAYMENT_EXPIRED'   // Payment authorization expired
  | 'TIMEOUT';          // Timeout

export type DispensePresetType = 'VOLUME' | 'AMOUNT';

export interface Transaction {
  id: string;
  stationId: string;
  userId: string;
  paymentId: string;
  
  // Dispense details
  nozzle: number;
  product: ProductType;
  
  // Preset (what user requested)
  presetType: DispensePresetType;
  presetVolumeLitres?: number;
  presetAmount?: number;
  
  // Actual results
  actualVolumeLitres: number;
  unitPrice: number;
  totalAmount: number;
  
  // Status
  status: TransactionStatus;
  stopReason?: StopReason;
  
  // Timestamps
  startTime?: string;
  endTime?: string;
  createdAt: string;
  updatedAt?: string;
  
  // Additional info
  metadata?: Record<string, any>;
}

export interface DispenseProgress {
  transactionId: string;
  stationId: string;
  
  // Current values
  currentVolumeLitres: number;
  currentAmount: number;
  
  // Target values (from preset)
  targetVolumeLitres?: number;
  targetAmount?: number;
  
  // Progress percentage (0-100)
  progressPercent: number;
  
  // Flow rate
  flowRateLitresPerMinute: number;
  
  // Estimated time remaining (seconds)
  estimatedTimeRemaining?: number;
  
  // Status
  status: TransactionStatus;
  
  // Timestamp
  timestamp: string;
}

export interface DispenseStartRequest {
  stationId: string;
  paymentId: string;
  product: ProductType;
  nozzle: number;
  
  // Preset: either volume or amount
  presetType: DispensePresetType;
  presetVolumeLitres?: number; // If presetType is 'VOLUME'
  presetAmount?: number;       // If presetType is 'AMOUNT'
}

export interface DispenseStartResponse {
  transaction: Transaction;
  message?: string;
}

export interface DispenseStopRequest {
  transactionId: string;
  reason?: StopReason;
}

export interface DispenseStopResponse {
  transaction: Transaction;
  message?: string;
}

export interface DispenseProgressResponse {
  progress: DispenseProgress;
}

export interface TransactionSummary {
  transaction: Transaction;
  station: {
    id: string;
    name: string;
  };
  receipt: {
    transactionId: string;
    date: string;
    station: string;
    product: string;
    volume: string;
    unitPrice: string;
    totalAmount: string;
    paymentMethod: string;
  };
}

// Request types
export interface GetTransactionsRequest {
  userId?: string;
  stationId?: string;
  status?: TransactionStatus[];
  startDate?: string;
  endDate?: string;
  limit?: number;
  offset?: number;
}

export interface GetTransactionsResponse {
  transactions: Transaction[];
  total: number;
  limit: number;
  offset: number;
}
