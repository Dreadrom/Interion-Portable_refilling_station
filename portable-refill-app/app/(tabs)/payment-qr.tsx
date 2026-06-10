import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useState, useEffect, useRef } from 'react';
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  ActivityIndicator,
} from 'react-native';
import QRCode from 'react-native-qrcode-svg';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { getPayment, pollPaymentStatus } from '../../src/api/payments';
import type { Payment } from '../../src/types';

export default function PaymentQRScreen() {
  const params = useLocalSearchParams<{
    paymentId: string;
    amount: string;
    currency: string;
    qrCodeData?: string;
    expiresAt: string;
  }>();
  
  const insets = useSafeAreaInsets();
  const [payment, setPayment] = useState<Payment | null>(null);
  const [timeRemaining, setTimeRemaining] = useState<number>(0);
  const [isPolling, setIsPolling] = useState(true);
  const pollingRef = useRef<boolean>(true);

  const amount = parseFloat(params.amount || '0');
  const currency = params.currency || 'MYR';

  useEffect(() => {
    loadPayment();
    startPolling();

    return () => {
      pollingRef.current = false;
    };
  }, []);

  useEffect(() => {
    const timer = setInterval(() => {
      if (payment?.expiresAt) {
        const remaining = Math.max(0, new Date(payment.expiresAt).getTime() - Date.now());
        setTimeRemaining(remaining);
        
        if (remaining === 0) {
          handleExpired();
        }
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [payment]);

  const loadPayment = async () => {
    try {
      const { payment: paymentData } = await getPayment(params.paymentId);
      setPayment(paymentData);
      
      const remaining = Math.max(0, new Date(paymentData.expiresAt).getTime() - Date.now());
      setTimeRemaining(remaining);
    } catch (error: any) {
      Alert.alert('Error', 'Failed to load payment details');
    }
  };

  const startPolling = async () => {
    try {
      await pollPaymentStatus(params.paymentId, {
        interval: 2000,
        maxAttempts: 150, // 5 minutes
        onUpdate: (updatedPayment) => {
          setPayment(updatedPayment);
          
          if (updatedPayment.status === 'SUCCESS') {
            pollingRef.current = false;
            setIsPolling(false);
            router.replace({
              pathname: '/payment-success',
              params: {
                paymentId: updatedPayment.id,
                amount: updatedPayment.amount.toString(),
                currency: updatedPayment.currency,
              },
            });
          } else if (updatedPayment.status === 'FAILED') {
            pollingRef.current = false;
            setIsPolling(false);
            router.replace({
              pathname: '/payment-failed',
              params: {
                paymentId: updatedPayment.id,
                amount: updatedPayment.amount.toString(),
                currency: updatedPayment.currency,
              },
            });
          }
        },
      });
    } catch (error: any) {
      if (error.message === 'Payment expired') {
        handleExpired();
      } else {
        Alert.alert('Error', error.message || 'Failed to check payment status');
      }
    }
  };

  const handleExpired = () => {
    pollingRef.current = false;
    setIsPolling(false);
    router.replace({
      pathname: '/payment-expired',
      params: {
        paymentId: params.paymentId,
        amount: params.amount,
        currency: params.currency,
      },
    });
  };

  const handleCancel = () => {
    Alert.alert(
      'Cancel Payment',
      'Are you sure you want to cancel this payment?',
      [
        { text: 'No', style: 'cancel' },
        {
          text: 'Yes, Cancel',
          style: 'destructive',
          onPress: () => {
            pollingRef.current = false;
            setIsPolling(false);
            router.back();
          },
        },
      ]
    );
  };

  const formatTime = (ms: number): string => {
    const minutes = Math.floor(ms / 60000);
    const seconds = Math.floor((ms % 60000) / 1000);
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  const qrData = payment?.qrCodeData || params.qrCodeData || 'PAYMENT_PENDING';

  return (
    <ScrollView 
      style={styles.container} 
      contentContainerStyle={[styles.contentContainer, { paddingTop: insets.top + 24, paddingBottom: insets.bottom + 24 }]}
    >
      {/* Header */}
      <View style={styles.header}>
        <Ionicons name="qr-code" size={48} color="#0156CC" />
        <Text style={styles.headerTitle}>Scan QR to Pay</Text>
        <Text style={styles.headerSubtitle}>Use any banking or e-wallet app</Text>
      </View>

      {/* Amount */}
      <View style={styles.amountCard}>
        <Text style={styles.amountLabel}>Amount to Pay</Text>
        <Text style={styles.amountValue}>{currency} {amount.toFixed(2)}</Text>
      </View>

      {/* QR Code */}
      <View style={styles.qrCard}>
        <View style={styles.qrContainer}>
          <QRCode
            value={qrData}
            size={220}
            backgroundColor="white"
            color="black"
          />
        </View>
        
        {/* Timer */}
        <View style={styles.timerContainer}>
          <Ionicons 
            name="time-outline" 
            size={20} 
            color={timeRemaining < 60000 ? '#DC2626' : '#6B7280'} 
          />
          <Text style={[
            styles.timerText,
            timeRemaining < 60000 && styles.timerTextUrgent
          ]}>
            Expires in {formatTime(timeRemaining)}
          </Text>
        </View>
      </View>

      {/* Instructions */}
      <View style={styles.instructionsCard}>
        <Text style={styles.instructionsTitle}>How to Pay:</Text>
        <View style={styles.instructionStep}>
          <Text style={styles.stepNumber}>1</Text>
          <Text style={styles.stepText}>Open your banking app or e-wallet</Text>
        </View>
        <View style={styles.instructionStep}>
          <Text style={styles.stepNumber}>2</Text>
          <Text style={styles.stepText}>Scan this QR code</Text>
        </View>
        <View style={styles.instructionStep}>
          <Text style={styles.stepNumber}>3</Text>
          <Text style={styles.stepText}>Confirm the payment in your app</Text>
        </View>
        <View style={styles.instructionStep}>
          <Text style={styles.stepNumber}>4</Text>
          <Text style={styles.stepText}>Your wallet will be updated automatically</Text>
        </View>
      </View>

      {/* Supported Methods */}
      <View style={styles.methodsCard}>
        <Text style={styles.methodsTitle}>Supported Payment Methods:</Text>
        <Text style={styles.methodsList}>
          DuitNow QR • Touch 'n Go • Boost • GrabPay • ShopeePay • Online Banking
        </Text>
      </View>

      {/* Status Indicator */}
      {isPolling && (
        <View style={styles.statusCard}>
          <ActivityIndicator size="small" color="#0156CC" />
          <Text style={styles.statusText}>Waiting for payment...</Text>
        </View>
      )}

      {/* Cancel Button */}
      <TouchableOpacity style={styles.cancelButton} onPress={handleCancel}>
        <Text style={styles.cancelButtonText}>Cancel Payment</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F3F4F6',
  },
  contentContainer: {
    padding: 20,
    alignItems: 'center',
  },
  header: {
    alignItems: 'center',
    marginBottom: 24,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: '#111827',
    marginTop: 12,
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#6B7280',
    marginTop: 4,
  },
  amountCard: {
    width: '100%',
    backgroundColor: '#DBEAFE',
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
    marginBottom: 24,
  },
  amountLabel: {
    fontSize: 14,
    color: '#1E40AF',
    marginBottom: 4,
  },
  amountValue: {
    fontSize: 32,
    fontWeight: '700',
    color: '#1E3A8A',
  },
  qrCard: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  qrContainer: {
    padding: 16,
    backgroundColor: 'white',
    borderRadius: 12,
  },
  timerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 20,
    paddingHorizontal: 16,
    paddingVertical: 8,
    backgroundColor: '#F3F4F6',
    borderRadius: 20,
  },
  timerText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
    marginLeft: 8,
  },
  timerTextUrgent: {
    color: '#DC2626',
  },
  instructionsCard: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 16,
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 16,
  },
  instructionStep: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  stepNumber: {
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: '#0156CC',
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
    textAlign: 'center',
    lineHeight: 24,
    marginRight: 12,
  },
  stepText: {
    flex: 1,
    fontSize: 14,
    color: '#4B5563',
    lineHeight: 24,
  },
  methodsCard: {
    width: '100%',
    backgroundColor: '#F9FAFB',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  methodsTitle: {
    fontSize: 12,
    fontWeight: '600',
    color: '#6B7280',
    marginBottom: 8,
    textTransform: 'uppercase',
  },
  methodsList: {
    fontSize: 14,
    color: '#374151',
    lineHeight: 20,
  },
  statusCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#FEF3C7',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    width: '100%',
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#92400E',
    marginLeft: 12,
  },
  cancelButton: {
    width: '100%',
    paddingVertical: 16,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: '#DC2626',
    alignItems: 'center',
  },
  cancelButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#DC2626',
  },
});
