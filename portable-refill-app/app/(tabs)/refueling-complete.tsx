import { router, useLocalSearchParams } from 'expo-router';
import React, { useState, useEffect } from 'react';
import {
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { globalStyles } from '../../src/styles/globalStyles';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';
import { useAuthStore } from '../../src/stores/useAuthStore';

export default function RefuelingCompleteScreen() {
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams();
  const { isGuest, user } = useAuthStore();
  const [transaction, setTransaction] = useState<TransactionData | null>(null);
  const [loading, setLoading] = useState(true);

  const transactionId = params.transactionId as string;
  const refundAmountParam = parseFloat(params.refundAmount as string) || 0;
  const currencyParam = (params.currency as string) || 'MYR';

  useEffect(() => {
    loadTransaction();
  }, []);

  const loadTransaction = async () => {
    if (!transactionId) {
      Alert.alert('Error', 'Invalid transaction ID', [
        { text: 'OK', onPress: () => router.push('./home') }
      ]);
      return;
    }

    const txn = await transactionStore.getTransaction(transactionId);
    
    if (!txn) {
      Alert.alert('Error', 'Transaction not found or has expired', [
        { text: 'OK', onPress: () => router.push('./home') }
      ]);
      return;
    }

    setTransaction(txn);
    setLoading(false);
  };

  if (loading || !transaction) {
    return (
      <View style={[styles.container, { justifyContent: 'center', alignItems: 'center' }]}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={{ marginTop: 16, color: '#6B7280' }}>Loading transaction...</Text>
      </View>
    );
  }

  const { stationName, product, nozzle, volumeDispensed, amountCharged, unitPrice, currency, holdAmount, elapsedTime, stopReason } = transaction;

  const holdAmountValue = parseFloat(holdAmount.split(' ')[1] || '0');
  const chargedValue = amountCharged;
  const refundAmount = Math.max(holdAmountValue - chargedValue, 0);

  const handleSaveReceipt = () => {
    router.push('./transaction-history' as any);
  };

  const handleDone = () => {
    router.push('./home');
  };

  const getStopReasonText = () => {
    switch (stopReason) {
      case 'TARGET_REACHED':
        return 'Order Fulfilled';
      case 'USER_STOPPED':
        return 'Manually stopped by user';
      case 'TANK_FULL':
        return 'Vehicle tank full — pump auto-stopped';
      case 'EMERGENCY_STOP':
        return 'Emergency stop activated';
      default:
        return 'Completed';
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={[styles.contentContainer, { paddingTop: insets.top + 24 }]}>
      {/* Success Header */}
      <View style={styles.successHeader}>
        <View style={styles.successIconContainer}>
          <Ionicons name="checkmark" size={48} color="#fff" />
        </View>
        <Text style={styles.successTitle}>Refueling Complete!</Text>
        <Text style={styles.successSubtitle}>Transaction completed successfully</Text>
        <Text style={styles.cardTitle}>Transaction Summary</Text>
        
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Station:</Text>
          <Text style={styles.summaryValue}>{stationName}</Text>
        </View>

        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Nozzle:</Text>
          <Text style={styles.summaryValue}>#{nozzle}</Text>
        </View>

        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Product:</Text>
          <Text style={styles.summaryValue}>{product}</Text>
        </View>

        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Duration:</Text>
          <Text style={styles.summaryValue}>{elapsedTime}</Text>
        </View>

        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Status:</Text>
          <Text style={styles.summaryValue}>{getStopReasonText()}</Text>
        </View>
      </View>

      {/* Fuel & Cost Details */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>Fuel Details</Text>
        
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Volume Dispensed:</Text>
          <Text style={styles.detailValue}>{volumeDispensed.toFixed(2)} L</Text>
        </View>

        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Unit Price:</Text>
          <Text style={styles.detailValue}>{currency} {unitPrice.toFixed(2)}/L</Text>
        </View>

        <View style={styles.separator} />

        <View style={styles.totalRow}>
          <Text style={styles.totalLabel}>Total Amount:</Text>
          <Text style={styles.totalValue}>{currency} {amountCharged.toFixed(2)}</Text>
        </View>
      </View>

      {/* Payment Breakdown */}
      <View style={[styles.card, styles.paymentCard]}>
        <Text style={styles.cardTitle}>Payment Breakdown</Text>
        
        <View style={styles.paymentRow}>
          <Text style={styles.paymentLabel}>Hold Amount:</Text>
          <Text style={styles.paymentValue}>{holdAmount}</Text>
        </View>

        <View style={styles.paymentRow}>
          <Text style={styles.paymentLabel}>Amount to Charge:</Text>
          <Text style={styles.paymentValueCharge}>{currency} {amountCharged.toFixed(2)}</Text>
        </View>

        <View style={styles.separator} />

        <View style={styles.finalChargeRow}>
          <Text style={styles.finalChargeLabel}>Final Charge:</Text>
          <Text style={styles.finalChargeValue}>{currency} {amountCharged.toFixed(2)}</Text>
        </View>
      </View>

      {/* Updated wallet balance card */}
      {!isGuest && (
        <View style={[styles.card, { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }]}>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 10 }}>
            <Ionicons name="wallet-outline" size={22} color="#10B981" />
            <Text style={{ fontSize: 15, color: '#6B7280' }}>Wallet Balance</Text>
          </View>
          <Text style={{ fontSize: 22, fontWeight: '700', color: '#111827' }}>
            {currencyParam} {(user?.walletBalance ?? 0).toFixed(2)}
          </Text>
        </View>
      )}

      {/* Payment confirmation */}
      <View style={styles.infoCard}>
        <Ionicons name="checkmark-circle" size={20} color="#3B82F6" style={styles.infoIconStyle} />
        <Text style={styles.infoText}>
          {currency} {amountCharged.toFixed(2)} has been charged from your wallet.
        </Text>
      </View>

      {/* Guest: create account prompt */}
      {isGuest && (
        <View style={styles.guestPromptCard}>
          <Text style={styles.guestPromptTitle}>Save Your Transaction History</Text>
          <Text style={styles.guestPromptText}>
            You're using Quick Dispense mode. Create a free account to track your refill history and manage your wallet.
          </Text>
          <TouchableOpacity
            style={[globalStyles.primaryButton, { marginTop: 12 }]}
            onPress={() => router.push('./create-account')}
          >
            <Text style={globalStyles.primaryButtonText}>Create Account</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Action Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={globalStyles.primaryButton}
          onPress={handleDone}
        >
          <Text style={globalStyles.primaryButtonText}>Done</Text>
        </TouchableOpacity>

        {!isGuest && (
          <TouchableOpacity
            style={[globalStyles.secondaryButton, { marginTop: 12 }]}
            onPress={handleSaveReceipt}
          >
            <Text style={globalStyles.secondaryButtonText}>View Transaction History</Text>
          </TouchableOpacity>
        )}
      </View>

      <View style={{ height: 40 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  contentContainer: {
    paddingHorizontal: 20,
    paddingBottom: 40,
  },
  successHeader: {
    alignItems: 'center',
    marginBottom: 24,
  },
  successIconContainer: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#10B981',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
    shadowColor: '#10B981',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
  },
  successIcon: {  // kept for compatibility
    fontSize: 48,
    color: '#fff',
    fontWeight: 'bold',
  },
  successTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 8,
  },
  successSubtitle: {
    fontSize: 16,
    color: '#6B7280',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6B7280',
    textTransform: 'uppercase',
    marginBottom: 16,
    letterSpacing: 0.5,
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  summaryLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  summaryValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  detailRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  detailLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  detailValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  separator: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginVertical: 12,
  },
  totalRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingTop: 8,
  },
  totalLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
  },
  totalValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#10B981',
  },
  paymentCard: {
    backgroundColor: '#F0FDF4',
    borderWidth: 2,
    borderColor: '#10B981',
  },
  paymentRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  paymentLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  paymentValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  paymentValueCharge: {
    fontSize: 15,
    fontWeight: '600',
    color: '#3B82F6',
  },
  paymentValueRefund: {
    fontSize: 15,
    fontWeight: '600',
    color: '#10B981',
  },
  finalChargeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingTop: 8,
  },
  finalChargeLabel: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  finalChargeValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#10B981',
  },
  infoCard: {
    backgroundColor: '#EFF6FF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
    flexDirection: 'row',
    alignItems: 'flex-start',
    borderWidth: 1,
    borderColor: '#3B82F6',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  infoIcon: {  // kept for compatibility
    fontSize: 20,
    marginRight: 12,
    color: '#3B82F6',
  },
  infoIconStyle: { marginRight: 12, marginTop: 1 },
  infoText: {
    flex: 1,
    fontSize: 14,
    color: '#1E40AF',
    lineHeight: 20,
  },
  buttonContainer: {
    marginTop: 8,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  refundBanner: {
    backgroundColor: '#D1FAE5',
    borderRadius: 16,
    padding: 16,
    marginBottom: 16,
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#10B981',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  refundBannerIcon: {  // kept for compatibility
    fontSize: 32,
    marginRight: 12,
  },
  refundBannerIconStyle: { marginRight: 12 },
  refundBannerContent: {
    flex: 1,
  },
  refundBannerTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#065F46',
    marginBottom: 4,
  },
  refundBannerSubtitle: {
    fontSize: 13,
    color: '#047857',
    lineHeight: 18,
  },
  guestPromptCard: {
    backgroundColor: '#EFF6FF',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#3B82F6',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  guestPromptTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1E40AF',
    marginBottom: 8,
  },
  guestPromptText: {
    fontSize: 14,
    color: '#1E3A8A',
    lineHeight: 20,
  },
});
