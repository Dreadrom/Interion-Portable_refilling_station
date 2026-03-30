import AsyncStorage from '@react-native-async-storage/async-storage';
import { BankAccount } from '../types';

const BANK_ACCOUNTS_KEY = 'bank_accounts';

function generateId(): string {
  return `BA-${Date.now().toString(36)}-${Math.random().toString(36).substring(2, 7)}`.toUpperCase();
}

export async function getBankAccounts(): Promise<BankAccount[]> {
  try {
    const data = await AsyncStorage.getItem(BANK_ACCOUNTS_KEY);
    return data ? JSON.parse(data) : [];
  } catch {
    return [];
  }
}

export async function saveBankAccount(
  account: Omit<BankAccount, 'id' | 'createdAt'>
): Promise<BankAccount> {
  const accounts = await getBankAccounts();

  // If new default, unset all others
  let updated = accounts.map((a) =>
    account.isDefault ? { ...a, isDefault: false } : a
  );

  const newAccount: BankAccount = {
    ...account,
    id: generateId(),
    createdAt: new Date().toISOString(),
  };

  // First account is always default
  if (updated.length === 0) newAccount.isDefault = true;

  updated.push(newAccount);
  await AsyncStorage.setItem(BANK_ACCOUNTS_KEY, JSON.stringify(updated));
  return newAccount;
}

export async function deleteBankAccount(id: string): Promise<void> {
  const accounts = await getBankAccounts();
  const filtered = accounts.filter((a) => a.id !== id);
  // Ensure at least one default if any remain
  if (filtered.length > 0 && !filtered.some((a) => a.isDefault)) {
    filtered[0].isDefault = true;
  }
  await AsyncStorage.setItem(BANK_ACCOUNTS_KEY, JSON.stringify(filtered));
}

export async function setDefaultBankAccount(id: string): Promise<void> {
  const accounts = await getBankAccounts();
  const updated = accounts.map((a) => ({ ...a, isDefault: a.id === id }));
  await AsyncStorage.setItem(BANK_ACCOUNTS_KEY, JSON.stringify(updated));
}

export function maskAccountNumber(accountNumber: string): string {
  if (accountNumber.length <= 4) return accountNumber;
  return '•••• •••• ' + accountNumber.slice(-4);
}
