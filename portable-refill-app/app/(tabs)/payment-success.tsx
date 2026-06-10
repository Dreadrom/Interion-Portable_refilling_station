import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useEffect } from 'react';
import {
  Alert,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Animated,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useAuthStore } from '../../src/stores/useAuthStore';
import { format } from 'date-fns';

export default function PaymentSuccessScreen() {
  const params = useLocalSearchParams<{
    paymentId: string;
    amount: string;
    currency: string;
  }>();
  
  const insets = useSafeAreaInsets();
  const { user, topUpBalance } = useAuthStore();
  const scaleAnim = React.useRef(new Animated.Value(0)).current;

  const amount = parseFloat(params.amount || '0');
  const currency = params.currency || 'MYR';
  const oldBalance = user?.walletBalance ?? 0;
  const newBalance = oldBalance + amount;

  useEffect(() => {
    // Update wallet balance with error handling
    topUpBalance(amount)
      .catch((error) => {
        console.error('Failed to update wallet:', error);
        Alert.alert(
          'Balance Update Issue',
          'Payment was successful, but wallet balance update failed. Please restart the app to refresh your balance.',
          [{ text: 'OK' }]
        );
      });

    // Animate checkmark
    Animated.spring(scaleAnim, {
      toValue: 1,
      tension: 50,
      friction: 7,
      useNativeDriver: true,
    }).start();
  }, []);

  const handleDone = () => {
    // Navigate to home/wallet screen
    router.replace('/home');
  };

  const handleViewWallet = () => {
    router.replace('/profile');
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top + 40, paddingBottom: insets.bottom + 24 }]}>
      {/* Success Icon */}
      <Animated.View style={[styles.iconContainer, { transform: [{ scale: scaleAnim }] }]}>
        <View style={styles.iconCircle}>
          <Ionicons name="checkmark" size={64} color="white" />
        </View>
      </Animated.View>

      {/* Success Message */}
      <Text style={styles.title}>Payment Successful!</Text>
      <Text style={styles.subtitle}>Your wallet has been topped up</Text>

      {/* Amount Card */}
      <View style={styles.amountCard}>
        <Text style={styles.amountLabel}>Amount Added</Text>
        <Text style={styles.amountValue}>
          {currency} {amount.toFixed(2)}
        </Text>
      </View>

      {/* Balance Summary */}
      <View style={styles.summaryCard}>
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>Previous Balance:</Text>
          <Text style={styles.summaryValue}>{currency} {oldBalance.toFixed(2)}</Text>
        </View>
        <View style={styles.summaryDivider} />
        <View style={styles.summaryRow}>
          <Text style={styles.summaryLabel}>New Balance:</Text>
          <Text style={styles.summaryValueBold}>{currency} {newBalance.toFixed(2)}</Text>
        </View>
      </View>

      {/* Transaction Details */}
      <View style={styles.detailsCard}>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Transaction ID</Text>
          <Text style={styles.detailValue}>{params.paymentId.slice(0, 12)}...</Text>
        </View>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Date & Time</Text>
          <Text style={styles.detailValue}>{format(new Date(), 'dd MMM yyyy, HH:mm')}</Text>
        </View>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Payment Method</Text>
          <Text style={styles.detailValue}>Fiuu QR Payment</Text>
        </View>
      </View>

      <View style={styles.spacer} />

      {/* Action Buttons */}
      <TouchableOpacity style={styles.primaryButton} onPress={handleDone}>
        <Text style={styles.primaryButtonText}>Done</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.secondaryButton} onPress={handleViewWallet}>
        <Text style={styles.secondaryButtonText}>View Wallet</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F3F4F6',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  iconContainer: {
    marginBottom: 24,
  },
  iconCircle: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: '#10B981',
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#10B981',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
  },
  title: {
    fontSize: 28,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 8,
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#6B7280',
    marginBottom: 32,
    textAlign: 'center',
  },
  amountCard: {
    width: '100%',
    backgroundColor: '#ECFDF5',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    marginBottom: 20,
    borderWidth: 2,
    borderColor: '#10B981',
  },
  amountLabel: {
    fontSize: 14,
    color: '#059669',
    marginBottom: 4,
    fontWeight: '500',
  },
  amountValue: {
    fontSize: 36,
    fontWeight: '700',
    color: '#047857',
  },
  summaryCard: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 16,
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  summaryDivider: {
    height: 1,
    backgroundColor: '#E5E7EB',
    marginVertical: 12,
  },
  summaryLabel: {
    fontSize: 14,
    color: '#6B7280',
  },
  summaryValue: {
    fontSize: 14,
    color: '#374151',
    fontWeight: '500',
  },
  summaryValueBold: {
    fontSize: 18,
    color: '#111827',
    fontWeight: '700',
  },
  detailsCard: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 16,
  },
  detailRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  detailLabel: {
    fontSize: 14,
    color: '#6B7280',
  },
  detailValue: {
    fontSize: 14,
    color: '#111827',
    fontWeight: '500',
  },
  spacer: {
    flex: 1,
  },
  primaryButton: {
    width: '100%',
    backgroundColor: '#0156CC',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    marginBottom: 12,
  },
  primaryButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: 'white',
  },
  secondaryButton: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#0156CC',
  },
  secondaryButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#0156CC',
  },
});
