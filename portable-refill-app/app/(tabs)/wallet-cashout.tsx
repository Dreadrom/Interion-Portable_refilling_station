import { router } from 'expo-router';
import React, { useState, useEffect, useCallback } from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  ActivityIndicator,
} from 'react-native';
import { useAuthStore } from '../../src/stores/useAuthStore';
import { getBankAccounts, maskAccountNumber } from '../../src/utils/bankAccountStore';
import { BankAccount } from '../../src/types';

const MIN_CASHOUT = 10;

export default function WalletCashoutScreen() {
  const { user, deductBalance } = useAuthStore();
  const [accounts, setAccounts] = useState<BankAccount[]>([]);
  const [selectedAccount, setSelectedAccount] = useState<BankAccount | null>(null);
  const [amount, setAmount] = useState('');
  const [processing, setProcessing] = useState(false);

  const balance = user?.walletBalance ?? 0;
  const currency = 'MYR';

  const loadAccounts = useCallback(async () => {
    const list = await getBankAccounts();
    setAccounts(list);
    const defaultAcc = list.find((a) => a.isDefault) ?? list[0] ?? null;
    setSelectedAccount(defaultAcc);
  }, []);

  useEffect(() => { loadAccounts(); }, [loadAccounts]);

  const handleCashOut = async () => {
    const amt = parseFloat(amount);
    if (isNaN(amt) || amt < MIN_CASHOUT) {
      Alert.alert('Invalid Amount', `Minimum cash-out is ${currency} ${MIN_CASHOUT.toFixed(2)}`);
      return;
    }
    if (amt > balance) {
      Alert.alert('Insufficient Balance', `Your wallet balance is ${currency} ${balance.toFixed(2)}`);
      return;
    }
    if (!selectedAccount) {
      Alert.alert('No Account', 'Please add a bank account or DuitNow account to cash out.');
      return;
    }

    setProcessing(true);
    try {
      // Simulate transfer (replace with real Fiuu payout API)
      await new Promise((r) => setTimeout(r, 2500));
      await deductBalance(amt);
      const destination = selectedAccount.isDuitNow
        ? `DuitNow (${selectedAccount.duitNowPhone})`
        : `${selectedAccount.bankName} ${maskAccountNumber(selectedAccount.accountNumber)}`;
      Alert.alert(
        'Cash-Out Successful!',
        `${currency} ${amt.toFixed(2)} has been transferred to:\n${destination}\n\nProcessing time: 1–3 business days`,
        [{ text: 'Done', onPress: () => router.back() }]
      );
    } catch (err: any) {
      Alert.alert('Transfer Failed', err.message || 'Please try again later.');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior="padding">
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Cash Out</Text>
        <Text style={styles.headerSubtitle}>Transfer wallet balance to your bank account</Text>
      </View>

      {/* Current balance */}
      <View style={styles.balanceCard}>
        <Text style={styles.balanceLabel}>Available Balance</Text>
        <Text style={styles.balanceAmount}>{currency} {balance.toFixed(2)}</Text>
      </View>

      {/* Amount input */}
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Amount to Transfer</Text>
        <View style={styles.amountRow}>
          <Text style={styles.currencyPrefix}>{currency}</Text>
          <TextInput
            style={styles.amountInput}
            value={amount}
            onChangeText={setAmount}
            keyboardType="decimal-pad"
            placeholder="0.00"
            placeholderTextColor="#bbb"
            editable={!processing}
          />
        </View>
        <View style={styles.quickAmounts}>
          {[50, 100, 200, 500].map((a) => (
            <TouchableOpacity
              key={a}
              style={[styles.quickChip, parseFloat(amount) === a && styles.quickChipSelected]}
              onPress={() => setAmount(String(a))}
              disabled={a > balance}
            >
              <Text style={[styles.quickChipText, parseFloat(amount) === a && styles.quickChipTextSelected]}>
                {a}
              </Text>
            </TouchableOpacity>
          ))}
          <TouchableOpacity
            style={styles.quickChip}
            onPress={() => setAmount(balance.toFixed(2))}
          >
            <Text style={styles.quickChipText}>All</Text>
          </TouchableOpacity>
        </View>
        <Text style={styles.minNote}>Minimum: {currency} {MIN_CASHOUT}.00</Text>
      </View>

      {/* Destination account */}
      <View style={styles.card}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Transfer To</Text>
          <TouchableOpacity onPress={() => router.push('./bank-account')}>
            <Text style={styles.addLink}>+ Add Account</Text>
          </TouchableOpacity>
        </View>

        {accounts.length === 0 ? (
          <View style={styles.noAccountBox}>
            <Text style={styles.noAccountText}>No bank account added yet.</Text>
            <TouchableOpacity style={styles.addAccountButton} onPress={() => router.push('./bank-account')}>
              <Text style={styles.addAccountButtonText}>Add Bank Account</Text>
            </TouchableOpacity>
          </View>
        ) : (
          accounts.map((acc) => (
            <TouchableOpacity
              key={acc.id}
              style={[styles.accountRow, selectedAccount?.id === acc.id && styles.accountRowSelected]}
              onPress={() => setSelectedAccount(acc)}
            >
              <View style={styles.accountRadio}>
                {selectedAccount?.id === acc.id && <View style={styles.accountRadioDot} />}
              </View>
              <View style={styles.accountInfo}>
                {acc.isDuitNow ? (
                  <>
                    <Text style={styles.accountName}>DuitNow Transfer</Text>
                    <Text style={styles.accountDetail}>{acc.duitNowPhone}</Text>
                  </>
                ) : (
                  <>
                    <Text style={styles.accountName}>{acc.bankName}</Text>
                    <Text style={styles.accountDetail}>{maskAccountNumber(acc.accountNumber)}</Text>
                    <Text style={styles.accountHolder}>{acc.accountHolderName}</Text>
                  </>
                )}
                {acc.isDefault && <Text style={styles.defaultBadge}>Default</Text>}
              </View>
            </TouchableOpacity>
          ))
        )}
      </View>

      {/* Info note */}
      <View style={styles.infoBox}>
        <Text style={styles.infoText}>
          Refunds from overpumping are automatically returned to your wallet balance. You can cash out anytime — processing takes 1–3 business days.
        </Text>
      </View>

      <TouchableOpacity
        style={[styles.cashoutButton, (processing || !selectedAccount || !amount) && styles.cashoutButtonDisabled]}
        onPress={handleCashOut}
        disabled={processing || !selectedAccount || !amount}
      >
        {processing ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={styles.cashoutButtonText}>
            Cash Out {amount ? `${currency} ${parseFloat(amount || '0').toFixed(2)}` : ''}
          </Text>
        )}
      </TouchableOpacity>
    </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  contentContainer: { padding: 16, paddingBottom: 48 },
  header: { marginBottom: 20, paddingTop: 8 },
  backText: { fontSize: 16, color: '#10B981', fontWeight: '500', marginBottom: 12 },
  headerTitle: { fontSize: 24, fontWeight: '700', color: '#1a1a2e' },
  headerSubtitle: { fontSize: 13, color: '#666', marginTop: 2 },
  balanceCard: {
    backgroundColor: '#1a1a2e',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    marginBottom: 16,
  },
  balanceLabel: { color: 'rgba(255,255,255,0.7)', fontSize: 13, marginBottom: 6 },
  balanceAmount: { color: '#10B981', fontSize: 36, fontWeight: '700' },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 16, marginBottom: 16, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 3, elevation: 2 },
  sectionTitle: { fontSize: 14, fontWeight: '700', color: '#1a1a2e', marginBottom: 12 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  addLink: { fontSize: 14, color: '#10B981', fontWeight: '500' },
  amountRow: { flexDirection: 'row', alignItems: 'center', borderWidth: 1, borderColor: '#ddd', borderRadius: 10, paddingHorizontal: 14, marginBottom: 12 },
  currencyPrefix: { fontSize: 18, color: '#555', marginRight: 8, fontWeight: '600' },
  amountInput: { flex: 1, fontSize: 28, fontWeight: '700', color: '#1a1a2e', paddingVertical: 12 },
  quickAmounts: { flexDirection: 'row', flexWrap: 'wrap', gap: 8, marginBottom: 8 },
  quickChip: { borderWidth: 1, borderColor: '#ddd', borderRadius: 20, paddingHorizontal: 14, paddingVertical: 6 },
  quickChipSelected: { borderColor: '#10B981', backgroundColor: '#EFF9F5' },
  quickChipText: { fontSize: 14, color: '#555' },
  quickChipTextSelected: { color: '#10B981', fontWeight: '600' },
  minNote: { fontSize: 11, color: '#aaa', marginTop: 4 },
  noAccountBox: { alignItems: 'center', paddingVertical: 16 },
  noAccountText: { color: '#888', marginBottom: 12 },
  addAccountButton: { borderWidth: 1, borderColor: '#10B981', borderRadius: 8, paddingVertical: 10, paddingHorizontal: 20 },
  addAccountButtonText: { color: '#10B981', fontWeight: '600' },
  accountRow: { flexDirection: 'row', alignItems: 'flex-start', padding: 12, borderRadius: 10, borderWidth: 1, borderColor: '#eee', marginBottom: 8 },
  accountRowSelected: { borderColor: '#10B981', backgroundColor: '#EFF9F5' },
  accountRadio: { width: 20, height: 20, borderRadius: 10, borderWidth: 2, borderColor: '#10B981', marginRight: 12, marginTop: 2, justifyContent: 'center', alignItems: 'center' },
  accountRadioDot: { width: 10, height: 10, borderRadius: 5, backgroundColor: '#10B981' },
  accountInfo: { flex: 1 },
  accountName: { fontSize: 15, fontWeight: '600', color: '#1a1a2e' },
  accountDetail: { fontSize: 13, color: '#666', marginTop: 2 },
  accountHolder: { fontSize: 12, color: '#888', marginTop: 1 },
  defaultBadge: { fontSize: 10, color: '#10B981', fontWeight: '700', marginTop: 4, backgroundColor: '#EFF9F5', alignSelf: 'flex-start', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 4 },
  infoBox: { backgroundColor: '#FFF9E6', borderRadius: 10, padding: 14, marginBottom: 16, borderLeftWidth: 3, borderLeftColor: '#F59E0B' },
  infoText: { fontSize: 13, color: '#444', lineHeight: 18 },
  cashoutButton: { backgroundColor: '#10B981', borderRadius: 12, paddingVertical: 16, alignItems: 'center' },
  cashoutButtonDisabled: { opacity: 0.5 },
  cashoutButtonText: { color: '#fff', fontSize: 16, fontWeight: '700' },
});
