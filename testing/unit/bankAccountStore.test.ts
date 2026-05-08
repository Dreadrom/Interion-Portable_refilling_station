/**
 * Unit tests — bankAccountStore.ts
 * Tests CRUD operations, masking, default-account logic, and that
 * sensitive data is not exposed unexpectedly.
 *
 * Note: AsyncStorage is mocked via jest.mock so no native module is required.
 */

// Mock AsyncStorage before any imports
jest.mock('@react-native-async-storage/async-storage', () => ({
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}));

import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  getBankAccounts,
  saveBankAccount,
  deleteBankAccount,
  setDefaultBankAccount,
  maskAccountNumber,
} from '../../portable-refill-app/src/utils/bankAccountStore';

const mockGetItem = AsyncStorage.getItem as jest.MockedFunction<typeof AsyncStorage.getItem>;
const mockSetItem = AsyncStorage.setItem as jest.MockedFunction<typeof AsyncStorage.setItem>;

function makeAccount(overrides: Partial<any> = {}) {
  return {
    bankName: 'Maybank',
    accountHolderName: 'Ali Hassan',
    accountNumber: '1234567890',
    isDuitNow: false,
    isDefault: false,
    ...overrides,
  };
}

beforeEach(() => {
  jest.clearAllMocks();
  mockGetItem.mockResolvedValue(null);
  mockSetItem.mockResolvedValue(undefined);
});

// ─────────────────────────────────────────────────────────────────────────────
// getBankAccounts
// ─────────────────────────────────────────────────────────────────────────────
describe('getBankAccounts', () => {
  test('returns empty array when nothing stored', async () => {
    mockGetItem.mockResolvedValue(null);
    const result = await getBankAccounts();
    expect(result).toEqual([]);
  });

  test('returns parsed accounts when data is stored', async () => {
    const stored = [{ id: 'BA-1', bankName: 'CIMB', accountNumber: '9999', isDefault: true, createdAt: '' }];
    mockGetItem.mockResolvedValue(JSON.stringify(stored));
    const result = await getBankAccounts();
    expect(result).toHaveLength(1);
    expect(result[0].bankName).toBe('CIMB');
  });

  test('returns empty array on JSON parse error', async () => {
    mockGetItem.mockResolvedValue('NOT_VALID_JSON{{{');
    const result = await getBankAccounts();
    expect(result).toEqual([]);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// saveBankAccount
// ─────────────────────────────────────────────────────────────────────────────
describe('saveBankAccount', () => {
  test('assigns a generated id', async () => {
    const account = await saveBankAccount(makeAccount());
    expect(account.id).toBeDefined();
    expect(account.id).toMatch(/^BA-/);
  });

  test('assigns createdAt timestamp', async () => {
    const account = await saveBankAccount(makeAccount());
    expect(new Date(account.createdAt).getTime()).not.toBeNaN();
  });

  test('first account is always set as default', async () => {
    // No existing accounts
    mockGetItem.mockResolvedValue(null);
    const account = await saveBankAccount(makeAccount({ isDefault: false }));
    expect(account.isDefault).toBe(true);
  });

  test('setting new default clears all others', async () => {
    const existing = [
      { id: 'BA-1', bankName: 'Maybank', isDefault: true, accountNumber: '111', createdAt: '' },
      { id: 'BA-2', bankName: 'CIMB', isDefault: false, accountNumber: '222', createdAt: '' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await saveBankAccount(makeAccount({ isDefault: true, accountNumber: '333' }));

    const saved = JSON.parse(savedData[savedData.length - 1]);
    const defaults = saved.filter((a: any) => a.isDefault);
    expect(defaults).toHaveLength(1);
    expect(defaults[0].accountNumber).toBe('333');
  });

  test('persists to AsyncStorage', async () => {
    await saveBankAccount(makeAccount());
    expect(mockSetItem).toHaveBeenCalled();
  });

  describe('SECURITY — bank accounts use AsyncStorage not SecureStore (Finding H3)', () => {
    test('saveBankAccount does NOT use SecureStore', async () => {
      // Confirms the vulnerability: account data goes through AsyncStorage
      // which is unencrypted on Android. This test documents the gap.
      await saveBankAccount(makeAccount());
      expect(mockSetItem).toHaveBeenCalledWith('bank_accounts', expect.any(String));
    });
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// deleteBankAccount
// ─────────────────────────────────────────────────────────────────────────────
describe('deleteBankAccount', () => {
  test('removes account by id', async () => {
    const existing = [
      { id: 'BA-1', isDefault: true, accountNumber: '111', bankName: 'MB', createdAt: '' },
      { id: 'BA-2', isDefault: false, accountNumber: '222', bankName: 'CIMB', createdAt: '' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await deleteBankAccount('BA-2');

    const saved = JSON.parse(savedData[savedData.length - 1]);
    expect(saved).toHaveLength(1);
    expect(saved[0].id).toBe('BA-1');
  });

  test('promotes the first remaining to default when the default is deleted', async () => {
    const existing = [
      { id: 'BA-1', isDefault: true, accountNumber: '111', bankName: 'MB', createdAt: '' },
      { id: 'BA-2', isDefault: false, accountNumber: '222', bankName: 'CIMB', createdAt: '' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await deleteBankAccount('BA-1');
    const saved = JSON.parse(savedData[savedData.length - 1]);
    expect(saved[0].isDefault).toBe(true);
  });

  test('handles delete of non-existent id gracefully', async () => {
    const existing = [
      { id: 'BA-1', isDefault: true, accountNumber: '111', bankName: 'MB', createdAt: '' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));
    await expect(deleteBankAccount('BA-NONEXISTENT')).resolves.not.toThrow();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// setDefaultBankAccount
// ─────────────────────────────────────────────────────────────────────────────
describe('setDefaultBankAccount', () => {
  test('sets exactly one account as default', async () => {
    const existing = [
      { id: 'BA-1', isDefault: true, accountNumber: '111', bankName: 'MB', createdAt: '' },
      { id: 'BA-2', isDefault: false, accountNumber: '222', bankName: 'CIMB', createdAt: '' },
    ];
    mockGetItem.mockResolvedValue(JSON.stringify(existing));

    const savedData: string[] = [];
    mockSetItem.mockImplementation(async (_key: string, value: string) => {
      savedData.push(value);
    });

    await setDefaultBankAccount('BA-2');
    const saved = JSON.parse(savedData[savedData.length - 1]);
    const defaultAccounts = saved.filter((a: any) => a.isDefault);
    expect(defaultAccounts).toHaveLength(1);
    expect(defaultAccounts[0].id).toBe('BA-2');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// maskAccountNumber
// ─────────────────────────────────────────────────────────────────────────────
describe('maskAccountNumber', () => {
  test('masks everything except last 4 digits', () => {
    expect(maskAccountNumber('1234567890')).toBe('•••• •••• 7890');
  });

  test('short account number (≤4) returns as-is', () => {
    expect(maskAccountNumber('1234')).toBe('1234');
  });

  test('does not expose full account number in masked form', () => {
    const masked = maskAccountNumber('9876543210');
    expect(masked).not.toContain('987654');
  });

  test('returns masked for typical 16-digit card-like number', () => {
    const masked = maskAccountNumber('4111111111111111');
    expect(masked).toMatch(/•+.*1111$/);
  });
});
