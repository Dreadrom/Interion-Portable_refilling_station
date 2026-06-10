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
  Switch,
} from 'react-native';
import {
  getBankAccounts,
  saveBankAccount,
  deleteBankAccount,
  setDefaultBankAccount,
  maskAccountNumber,
} from '../../src/utils/bankAccountStore';
import { BankAccount, MALAYSIA_BANKS } from '../../src/types';

export default function BankAccountScreen() {
  const [accounts, setAccounts] = useState<BankAccount[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [isDuitNow, setIsDuitNow] = useState(false);

  // Form fields
  const [holderName, setHolderName] = useState('');
  const [bankName, setBankName] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [duitNowPhone, setDuitNowPhone] = useState('');
  const [saving, setSaving] = useState(false);
  const [showBankPicker, setShowBankPicker] = useState(false);

  const reload = useCallback(async () => {
    setAccounts(await getBankAccounts());
  }, []);

  useEffect(() => { reload(); }, [reload]);

  const resetForm = () => {
    setHolderName(''); setBankName(''); setAccountNumber('');
    setDuitNowPhone(''); setIsDuitNow(false); setShowForm(false); setShowBankPicker(false);
  };

  const handleSave = async () => {
    if (isDuitNow) {
      const digits = duitNowPhone.replace(/\D/g, '');
      if (digits.length < 9) {
        Alert.alert('Invalid', 'Please enter a valid Malaysian phone number');
        return;
      }
      setSaving(true);
      await saveBankAccount({
        accountHolderName: holderName || duitNowPhone,
        bankName: 'DuitNow',
        accountNumber: '',
        isDuitNow: true,
        duitNowPhone: digits.startsWith('60') ? `+${digits}` : `+60${digits.replace(/^0/, '')}`,
        isDefault: accounts.length === 0,
      });
    } else {
      if (!holderName.trim() || !bankName || !accountNumber.trim()) {
        Alert.alert('Incomplete', 'Please fill in all bank account fields');
        return;
      }
      setSaving(true);
      await saveBankAccount({
        accountHolderName: holderName,
        bankName,
        accountNumber,
        isDuitNow: false,
        isDefault: accounts.length === 0,
      });
    }
    setSaving(false);
    resetForm();
    reload();
  };

  const handleDelete = (acc: BankAccount) => {
    Alert.alert(
      'Remove Account',
      `Remove ${acc.isDuitNow ? 'DuitNow account' : `${acc.bankName} ${maskAccountNumber(acc.accountNumber)}`}?`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Remove', style: 'destructive',
          onPress: async () => { await deleteBankAccount(acc.id); reload(); },
        },
      ]
    );
  };

  const handleSetDefault = async (acc: BankAccount) => {
    await setDefaultBankAccount(acc.id);
    reload();
  };

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior="padding">
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()}>
          <Text style={styles.backText}>← Back</Text>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Bank Accounts</Text>
        <Text style={styles.headerSubtitle}>For wallet cash-out and refunds</Text>
      </View>

      {/* Existing accounts */}
      {accounts.map((acc) => (
        <View key={acc.id} style={styles.accountCard}>
          <View style={styles.accountCardLeft}>
            {acc.isDuitNow ? (
              <>
                <Text style={styles.bankName}>DuitNow Transfer</Text>
                <Text style={styles.accountNum}>{acc.duitNowPhone}</Text>
              </>
            ) : (
              <>
                <Text style={styles.bankName}>{acc.bankName}</Text>
                <Text style={styles.accountNum}>{maskAccountNumber(acc.accountNumber)}</Text>
                <Text style={styles.holderName}>{acc.accountHolderName}</Text>
              </>
            )}
          </View>
          <View style={styles.accountCardRight}>
            {acc.isDefault ? (
              <View style={styles.defaultBadge}>
                <Text style={styles.defaultBadgeText}>Default</Text>
              </View>
            ) : (
              <TouchableOpacity onPress={() => handleSetDefault(acc)} style={styles.setDefaultBtn}>
                <Text style={styles.setDefaultText}>Set default</Text>
              </TouchableOpacity>
            )}
            <TouchableOpacity onPress={() => handleDelete(acc)} style={styles.deleteBtn}>
              <Text style={styles.deleteText}>Remove</Text>
            </TouchableOpacity>
          </View>
        </View>
      ))}

      {accounts.length === 0 && !showForm && (
        <View style={styles.emptyBox}>
          <Text style={styles.emptyText}>No accounts added yet.</Text>
          <Text style={styles.emptySubtext}>Add a bank account or DuitNow number to receive your cash-out and refunds.</Text>
        </View>
      )}

      {/* Add account form */}
      {showForm ? (
        <View style={styles.formCard}>
          <Text style={styles.formTitle}>Add Account</Text>

          {/* DuitNow toggle */}
          <View style={styles.toggleRow}>
            <View>
              <Text style={styles.toggleLabel}>Use DuitNow (by phone number)</Text>
              <Text style={styles.toggleSub}>Fastest — works with any Malaysian bank</Text>
            </View>
            <Switch
              value={isDuitNow}
              onValueChange={setIsDuitNow}
              trackColor={{ false: '#ddd', true: '#10B981' }}
              thumbColor="#fff"
            />
          </View>

          {isDuitNow ? (
            <>
              <Text style={styles.fieldLabel}>DuitNow Phone Number</Text>
              <TextInput
                style={styles.input}
                value={duitNowPhone}
                onChangeText={setDuitNowPhone}
                placeholder="e.g. 0123456789"
                keyboardType="phone-pad"
              />
              <Text style={styles.fieldHint}>Must be registered for DuitNow with your bank</Text>
            </>
          ) : (
            <>
              <Text style={styles.fieldLabel}>Account Holder Name</Text>
              <TextInput style={styles.input} value={holderName} onChangeText={setHolderName} placeholder="As per bank records" />

              <Text style={styles.fieldLabel}>Bank</Text>
              <TouchableOpacity style={styles.bankSelector} onPress={() => setShowBankPicker(!showBankPicker)}>
                <Text style={[styles.bankSelectorText, !bankName && { color: '#aaa' }]}>
                  {bankName || 'Select bank...'}
                </Text>
                <Text style={styles.bankSelectorArrow}>{showBankPicker ? '▲' : '▼'}</Text>
              </TouchableOpacity>

              {showBankPicker && (
                <View style={styles.bankList}>
                  {MALAYSIA_BANKS.map((b) => (
                    <TouchableOpacity
                      key={b}
                      style={[styles.bankListItem, bankName === b && styles.bankListItemSelected]}
                      onPress={() => { setBankName(b); setShowBankPicker(false); }}
                    >
                      <Text style={[styles.bankListText, bankName === b && styles.bankListTextSelected]}>{b}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}

              <Text style={styles.fieldLabel}>Account Number</Text>
              <TextInput
                style={styles.input}
                value={accountNumber}
                onChangeText={setAccountNumber}
                placeholder="Bank account number"
                keyboardType="number-pad"
              />
            </>
          )}

          <View style={styles.formButtons}>
            <TouchableOpacity style={styles.cancelBtn} onPress={resetForm}>
              <Text style={styles.cancelBtnText}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.saveBtn} onPress={handleSave} disabled={saving}>
              <Text style={styles.saveBtnText}>{saving ? 'Saving...' : 'Save Account'}</Text>
            </TouchableOpacity>
          </View>
        </View>
      ) : (
        <TouchableOpacity style={styles.addButton} onPress={() => setShowForm(true)}>
          <Text style={styles.addButtonText}>+ Add Bank Account / DuitNow</Text>
        </TouchableOpacity>
      )}

      <View style={styles.infoBox}>
        <Text style={styles.infoTitle}>Why do we need this?</Text>
        <Text style={styles.infoText}>
          When you pump less than your preset (e.g. 80L of 100L), we automatically refund the difference back to your wallet. You can then cash-out to your bank account or DuitNow any time you want.{'\n\n'}
          Your account details are stored securely on your device.
        </Text>
      </View>
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
  accountCard: {
    backgroundColor: '#fff', borderRadius: 12, padding: 16, marginBottom: 10,
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start',
    shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 3, elevation: 2,
  },
  accountCardLeft: { flex: 1, marginRight: 12 },
  accountCardRight: { alignItems: 'flex-end', gap: 8 },
  bankName: { fontSize: 15, fontWeight: '600', color: '#1a1a2e' },
  accountNum: { fontSize: 13, color: '#555', marginTop: 2, fontFamily: 'monospace' },
  holderName: { fontSize: 12, color: '#888', marginTop: 2 },
  defaultBadge: { backgroundColor: '#EFF9F5', paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  defaultBadgeText: { fontSize: 11, color: '#10B981', fontWeight: '700' },
  setDefaultBtn: { padding: 4 },
  setDefaultText: { fontSize: 12, color: '#10B981' },
  deleteBtn: { padding: 4 },
  deleteText: { fontSize: 12, color: '#EF4444' },
  emptyBox: { alignItems: 'center', paddingVertical: 32 },
  emptyText: { fontSize: 16, color: '#888', fontWeight: '500', marginBottom: 8 },
  emptySubtext: { fontSize: 13, color: '#aaa', textAlign: 'center', lineHeight: 18 },
  formCard: { backgroundColor: '#fff', borderRadius: 12, padding: 16, marginBottom: 16, shadowColor: '#000', shadowOffset: { width: 0, height: 1 }, shadowOpacity: 0.06, shadowRadius: 3, elevation: 2 },
  formTitle: { fontSize: 16, fontWeight: '700', color: '#1a1a2e', marginBottom: 16 },
  toggleRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, paddingBottom: 16, borderBottomWidth: 1, borderBottomColor: '#f0f0f0' },
  toggleLabel: { fontSize: 14, fontWeight: '600', color: '#1a1a2e' },
  toggleSub: { fontSize: 11, color: '#888', marginTop: 2 },
  fieldLabel: { fontSize: 12, fontWeight: '600', color: '#666', marginBottom: 6, marginTop: 12, textTransform: 'uppercase', letterSpacing: 0.5 },
  fieldHint: { fontSize: 11, color: '#aaa', marginTop: 4 },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, padding: 12, fontSize: 15, color: '#1a1a2e', backgroundColor: '#fafafa' },
  bankSelector: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, padding: 12, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#fafafa' },
  bankSelectorText: { fontSize: 15, color: '#1a1a2e' },
  bankSelectorArrow: { color: '#888' },
  bankList: { borderWidth: 1, borderColor: '#ddd', borderRadius: 8, marginTop: 4, backgroundColor: '#fff', maxHeight: 200, overflow: 'hidden' },
  bankListItem: { padding: 12, borderBottomWidth: 1, borderBottomColor: '#f0f0f0' },
  bankListItemSelected: { backgroundColor: '#EFF9F5' },
  bankListText: { fontSize: 14, color: '#1a1a2e' },
  bankListTextSelected: { color: '#10B981', fontWeight: '600' },
  formButtons: { flexDirection: 'row', marginTop: 20, gap: 10 },
  cancelBtn: { flex: 1, borderWidth: 1, borderColor: '#ddd', borderRadius: 10, paddingVertical: 12, alignItems: 'center' },
  cancelBtnText: { color: '#666', fontWeight: '600' },
  saveBtn: { flex: 2, backgroundColor: '#10B981', borderRadius: 10, paddingVertical: 12, alignItems: 'center' },
  saveBtnText: { color: '#fff', fontWeight: '700' },
  addButton: { backgroundColor: '#fff', borderRadius: 12, borderWidth: 1.5, borderColor: '#10B981', borderStyle: 'dashed', paddingVertical: 16, alignItems: 'center', marginBottom: 16 },
  addButtonText: { color: '#10B981', fontSize: 15, fontWeight: '600' },
  infoBox: { backgroundColor: '#EFF9F5', borderRadius: 12, padding: 14, borderLeftWidth: 3, borderLeftColor: '#10B981' },
  infoTitle: { fontSize: 13, fontWeight: '700', color: '#1a1a2e', marginBottom: 6 },
  infoText: { fontSize: 12, color: '#555', lineHeight: 18 },
});
