/**
 * Transaction API
 */

import { get } from './client';
import { 
  Transaction,
  TransactionSummary,
  GetTransactionsRequest,
  GetTransactionsResponse,
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

/**
 * Get user's transactions
 */
export async function getTransactions(params?: GetTransactionsRequest): Promise<GetTransactionsResponse> {
  return get<GetTransactionsResponse>(API_ENDPOINTS.TRANSACTIONS, params);
}

/**
 * Get transaction by ID
 */
export async function getTransactionById(id: string): Promise<TransactionSummary> {
  return get<TransactionSummary>(API_ENDPOINTS.TRANSACTION_DETAIL(id));
}
