/**
 * Unit tests — transactionStore.ts
 * Tests the in-memory + AsyncStorage transaction store used to persist
 * dispensing history on the device.
 */

jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}));

import AsyncStorage from '@react-native-async-storage/async-storage';
import { transactionStore as defaultStore, TransactionData } from '../../portable-refill-app/src/utils/transactionStore';

// We create a fresh instance for each test by importing the class directly
// The file only exports a singleton, so we work with it and call .clear() between tests

const mockGetItem = AsyncStorage.getItem as jest.MockedFunction<typeof AsyncStorage.getItem>;
const mockSetItem = AsyncStorage.setItem as jest.MockedFunction<typeof AsyncStorage.setItem>;

function makeTransactionInput(): Omit<TransactionData, 'id' | 'timestamp'> {
  return {
    stationName: 'Interion Station A',
    product: 'RON95',
    nozzle: 'Nozzle 1',
    volumeDispensed: 10.5,
    amountCharged: 25.45,
    refundAmount: 0,
    unitPrice: 2.42,
    currency: 'MYR',
    holdAmount: '50.00',
    elapsedTime: '2m 5s',
    stopReason: 'VOLUME_REACHED',
  };
}

let store: typeof defaultStore;

beforeEach(async () => {
  jest.clearAllMocks();
  mockGetItem.mockResolvedValue(null);
  mockSetItem.mockResolvedValue(undefined);
  store = defaultStore;
  await store.clear();
});


// ─────────────────────────────────────────────────────────────────────────────
// storeTransaction
// ─────────────────────────────────────────────────────────────────────────────
describe('storeTransaction', () => {
  test('returns a string id', async () => {
    const id = await store.storeTransaction(makeTransactionInput());
    expect(typeof id).toBe('string');
    expect(id.length).toBeGreaterThan(0);
  });

  test('id is unique across two calls', async () => {
    const id1 = await store.storeTransaction(makeTransactionInput());
    const id2 = await store.storeTransaction(makeTransactionInput());
    expect(id1).not.toBe(id2);
  });

  test('persists to AsyncStorage', async () => {
    await store.storeTransaction(makeTransactionInput());
    expect(mockSetItem).toHaveBeenCalled();
  });

  test('prepends to existing list (newest first)', async () => {
    const existing = [{ id: 'OLD-1', timestamp: 1000, stationName: 'OldStation', product: 'RON95',
      nozzle: '1', volumeDispensed: 5, amountCharged: 10, refundAmount: 0,
      unitPrice: 2, currency: 'MYR', holdAmount: '20', elapsedTime: '1m', stopReason: 'MANUAL' }];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    const newId = await store.storeTransaction(makeTransactionInput());
    const saved = JSON.parse(savedData[savedData.length - 1]);
    expect(saved[0].id).toBe(newId);  // newest first
    expect(saved[1].id).toBe('OLD-1');
  });

  test('caps stored list at 100 entries', async () => {
    const existing = Array.from({ length: 100 }, (_, i) => ({
      id: `TX-${i}`, timestamp: i, stationName: 'S', product: 'RON95',
      nozzle: '1', volumeDispensed: 1, amountCharged: 2, refundAmount: 0,
      unitPrice: 2, currency: 'MYR', holdAmount: '5', elapsedTime: '1s', stopReason: 'MANUAL',
    }));
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await store.storeTransaction(makeTransactionInput());
    const saved = JSON.parse(savedData[savedData.length - 1]);
    expect(saved.length).toBeLessThanOrEqual(100);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// getTransaction
// ─────────────────────────────────────────────────────────────────────────────
describe('getTransaction', () => {
  test('returns stored transaction from in-memory cache', async () => {
    const id = await store.storeTransaction(makeTransactionInput());
    const tx = await store.getTransaction(id);
    expect(tx).not.toBeNull();
    expect(tx!.id).toBe(id);
    expect(tx!.stationName).toBe('Interion Station A');
  });

  test('returns null for unknown id', async () => {
    mockGetItem.mockResolvedValue(null);
    const tx = await store.getTransaction('NONEXISTENT');
    expect(tx).toBeNull();
  });

  test('falls back to AsyncStorage when not in memory', async () => {
    const stored = [{
      id: 'TX-STORED', timestamp: Date.now(), stationName: 'Station B',
      product: 'RON95', nozzle: '2', volumeDispensed: 20, amountCharged: 48.4,
      refundAmount: 0, unitPrice: 2.42, currency: 'MYR', holdAmount: '100',
      elapsedTime: '5m', stopReason: 'FULL',
    }];
    mockGetItem.mockResolvedValue(JSON.stringify(stored));

    const freshStore = defaultStore;
    const tx = await freshStore.getTransaction('TX-STORED');
    expect(tx).not.toBeNull();
    expect(tx!.stationName).toBe('Station B');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// getAllTransactions
// ─────────────────────────────────────────────────────────────────────────────
describe('getAllTransactions', () => {
  test('returns empty array when nothing stored', async () => {
    mockGetItem.mockResolvedValue(null);
    const result = await store.getAllTransactions();
    expect(result).toEqual([]);
  });

  test('returns all stored transactions', async () => {
    const stored = [
      { id: 'TX-1', timestamp: 1000, stationName: 'A', product: 'RON95',
        nozzle: '1', volumeDispensed: 5, amountCharged: 10, refundAmount: 0,
        unitPrice: 2, currency: 'MYR', holdAmount: '20', elapsedTime: '1m', stopReason: 'MANUAL' },
      { id: 'TX-2', timestamp: 2000, stationName: 'B', product: 'RON97',
        nozzle: '2', volumeDispensed: 10, amountCharged: 26, refundAmount: 0,
        unitPrice: 2.6, currency: 'MYR', holdAmount: '50', elapsedTime: '2m', stopReason: 'AMOUNT_REACHED' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(stored));
    const result = await store.getAllTransactions();
    expect(result).toHaveLength(2);
  });

  test('returns empty array on corrupted data', async () => {
    mockGetItem.mockResolvedValue('{INVALID_JSON');
    const result = await store.getAllTransactions();
    expect(result).toEqual([]);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// Auto-cleanup (in-memory TTL of 1 hour)
// ─────────────────────────────────────────────────────────────────────────────
describe('in-memory auto-cleanup', () => {
  test('transaction is removed from memory after 1 hour', async () => {
    jest.useFakeTimers();
    const id = await store.storeTransaction(makeTransactionInput());

    // Before 1 hour: should still be retrievable from memory
    const before = await store.getTransaction(id);
    expect(before).not.toBeNull();

    // Fast-forward 1 hour + 1 second
    jest.advanceTimersByTime(60 * 60 * 1000 + 1000);

    // After 1 hour: in-memory cache is cleared; must fall back to AsyncStorage
    // AsyncStorage returns null here (no persistent mock set up)
    mockGetItem.mockResolvedValue(null);
    const after = await store.getTransaction(id);
    expect(after).toBeNull();

    jest.useRealTimers();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// SECURITY
// ─────────────────────────────────────────────────────────────────────────────
describe('SECURITY — transaction data storage', () => {
  test('transaction history is stored in unencrypted AsyncStorage (documented gap)', async () => {
    // Documents that financial transaction history uses AsyncStorage, not SecureStore.
    // On a rooted Android device this data is accessible without the app.
    await store.storeTransaction(makeTransactionInput());
    expect(mockSetItem).toHaveBeenCalledWith('transaction_history', expect.any(String));
  });

  test('no injection in stationName corrupts the JSON store', async () => {
    const maliciousInput = makeTransactionInput();
    maliciousInput.stationName = '","id":"INJECTED","fake":"';

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await store.storeTransaction(maliciousInput);

    // Data must still be valid JSON with proper escaping
    expect(() => JSON.parse(savedData[savedData.length - 1])).not.toThrow();
  });
});
