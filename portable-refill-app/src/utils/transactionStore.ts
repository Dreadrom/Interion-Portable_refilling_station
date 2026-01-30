// Secure in-memory store for transaction data
// In production, this would be replaced with secure backend storage

export interface TransactionData {
  id: string;
  timestamp: number;
  stationName: string;
  product: string;
  nozzle: string;
  volumeDispensed: number;
  amountCharged: number;
  unitPrice: number;
  currency: string;
  holdAmount: string;
  elapsedTime: string;
  stopReason: string;
}

class TransactionStore {
  private transactions: Map<string, TransactionData> = new Map();

  // Store a transaction and return its ID
  storeTransaction(data: Omit<TransactionData, 'id' | 'timestamp'>): string {
    const id = this.generateTransactionId();
    const timestamp = Date.now();
    
    const transaction: TransactionData = {
      ...data,
      id,
      timestamp,
    };
    
    this.transactions.set(id, transaction);
    
    // Auto-cleanup old transactions after 1 hour
    setTimeout(() => {
      this.transactions.delete(id);
    }, 60 * 60 * 1000);
    
    return id;
  }

  // Retrieve a transaction by ID
  getTransaction(id: string): TransactionData | null {
    return this.transactions.get(id) || null;
  }

  // Generate a unique transaction ID
  private generateTransactionId(): string {
    const timestamp = Date.now().toString(36);
    const random = Math.random().toString(36).substring(2, 9);
    return `TXN-${timestamp}-${random}`.toUpperCase();
  }

  // Clear all transactions (for testing)
  clear(): void {
    this.transactions.clear();
  }
}

// Export singleton instance
export const transactionStore = new TransactionStore();
