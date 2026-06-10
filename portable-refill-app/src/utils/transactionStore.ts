// Secure in-memory store for transaction data
// In production, this would be replaced with secure backend storage

import AsyncStorage from '@react-native-async-storage/async-storage';

const HISTORY_KEY = 'transaction_history';

export interface TransactionData {
  id: string;
  timestamp: number;          // Unix ms — when dispensing ended (receipt issued)
  dispensingStartTime: number; // Unix ms — when pumping actually started
  stationName: string;
  stationId: string;
  stationAddress: string;
  stationPhone: string;
  product: string;
  nozzle: string;
  volumeDispensed: number;
  amountCharged: number;
  refundAmount: number;
  unitPrice: number;
  currency: string;
  holdAmount: string;
  elapsedTime: string;
  stopReason: string;
  terminalId: string;         // e.g. "ACEREV-<stationId>"
  approvalCode: string;       // wallet auth reference
  paymentRef: string;         // transaction ID shown on payment line
}

class TransactionStore {
  private transactions: Map<string, TransactionData> = new Map();

  // Store a transaction and return its ID
  async storeTransaction(data: Omit<TransactionData, 'id' | 'timestamp'>): Promise<string> {
    const id = this.generateTransactionId();
    const timestamp = Date.now();
    
    const transaction: TransactionData = {
      ...data,
      id,
      timestamp,
    };
    
    this.transactions.set(id, transaction);

    // Persist to AsyncStorage
    try {
      const existing = await AsyncStorage.getItem(HISTORY_KEY);
      const list: TransactionData[] = existing ? JSON.parse(existing) : [];
      list.unshift(transaction); // newest first
      // Keep last 100 transactions
      if (list.length > 100) list.splice(100);
      await AsyncStorage.setItem(HISTORY_KEY, JSON.stringify(list));
    } catch {
      // Non-critical — in-memory copy still valid
    }
    
    // Auto-cleanup in-memory entry after 1 hour
    // .unref() prevents this timer from keeping the Node.js process alive (relevant in tests)
    const cleanup = setTimeout(() => {
      this.transactions.delete(id);
    }, 60 * 60 * 1000);
    (cleanup as any).unref?.();
    
    return id;
  }

  // Retrieve a transaction by ID (in-memory or from AsyncStorage)
  async getTransaction(id: string): Promise<TransactionData | null> {
    if (this.transactions.has(id)) {
      return this.transactions.get(id)!;
    }
    try {
      const existing = await AsyncStorage.getItem(HISTORY_KEY);
      if (existing) {
        const list: TransactionData[] = JSON.parse(existing);
        return list.find((t) => t.id === id) || null;
      }
    } catch {}
    return null;
  }

  // Get all transactions from persistent storage
  async getAllTransactions(): Promise<TransactionData[]> {
    try {
      const existing = await AsyncStorage.getItem(HISTORY_KEY);
      return existing ? JSON.parse(existing) : [];
    } catch {
      return Array.from(this.transactions.values()).sort((a, b) => b.timestamp - a.timestamp);
    }
  }

  // Generate a unique transaction ID
  private generateTransactionId(): string {
    const timestamp = Date.now().toString(36);
    const random = Math.random().toString(36).substring(2, 9);
    return `TXN-${timestamp}-${random}`.toUpperCase();
  }

  // Clear all transactions (for testing)
  async clear(): Promise<void> {
    this.transactions.clear();
    await AsyncStorage.removeItem(HISTORY_KEY);
  }
}

// Export singleton instance
export const transactionStore = new TransactionStore();
