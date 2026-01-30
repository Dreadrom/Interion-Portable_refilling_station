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
import { globalStyles } from '../styles/globalStyles';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';

export default function RefuelingCompleteScreen() {
  const params = useLocalSearchParams();
  const [transaction, setTransaction] = useState<TransactionData | null>(null);
  const [loading, setLoading] = useState(true);

  const transactionId = params.transactionId as string;

  useEffect(() => {
    loadTransaction();
  }, []);

  const loadTransaction = () => {
    if (!transactionId) {
      Alert.alert('Error', 'Invalid transaction ID', [
        { text: 'OK', onPress: () => router.push('./home') }
      ]);
      return;
    }

    const txn = transactionStore.getTransaction(transactionId);
    
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
    Alert.alert(
      'Receipt Saved',
      'Your receipt has been saved to your transaction history.',
      [{ text: 'OK' }]
    );
  };

  const handleDone = () => {
    router.push('./home');
  };

  const getStopReasonText = () => {
    switch (stopReason) {
      case 'TARGET_REACHED':
        return 'Preset target reached';
      case 'USER_STOPPED':
        return 'Manually stopped by user';
      case 'EMERGENCY_STOP':
        return 'Emergency stop activated';
      default:
        return 'Completed';
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      {/* Success Header */}
      <View style={styles.successHeader}>
        <View style={styles.successIconContainer}>
          <Text style={styles.successIcon}>✓</Text>
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
        <Text style={styles.cardTitle}>💳 Payment Breakdown</Text>
        
        <View style={styles.paymentRow}>
          <Text style={styles.paymentLabel}>Hold Amount:</Text>
          <Text style={styles.paymentValue}>{holdAmount}</Text>
        </View>

        <View style={styles.paymentRow}>
          <Text style={styles.paymentLabel}>Amount to Charge:</Text>
          <Text style={styles.paymentValueCharge}>{currency} {amountCharged.toFixed(2)}</Text>
        </View>

        {refundAmount > 0 && (
          <View style={styles.paymentRow}>
            <Text style={styles.paymentLabel}>Refund Amount:</Text>
            <Text style={styles.paymentValueRefund}>{currency} {refundAmount.toFixed(2)}</Text>
          </View>
        )}

        <View style={styles.separator} />

        <View style={styles.finalChargeRow}>
          <Text style={styles.finalChargeLabel}>Final Charge:</Text>
          <Text style={styles.finalChargeValue}>{currency} {amountCharged.toFixed(2)}</Text>
        </View>
      </View>

      {/* Info Note */}
      <View style={styles.infoCard}>
        <Text style={styles.infoIcon}>✓</Text>
        <Text style={styles.infoText}>
          Payment of {currency} {amountCharged.toFixed(2)} has been charged to your wallet.
          {refundAmount > 0 && ` ${currency} ${refundAmount.toFixed(2)} has been refunded.`}
        </Text>
      </View>

      {/* Action Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={globalStyles.primaryButton}
          onPress={handleDone}
        >
          <Text style={globalStyles.primaryButtonText}>Done</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={handleSaveReceipt}
        >
          <Text style={globalStyles.secondaryButtonText}>Save Receipt</Text>
        </TouchableOpacity>
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
    paddingTop: 60,
    paddingHorizontal: 20,
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
  successIcon: {
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
  infoIcon: {
    fontSize: 20,
    marginRight: 12,
    color: '#3B82F6',
  },
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
});
