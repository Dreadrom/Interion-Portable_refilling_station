import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useEffect } from 'react';
import {
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Animated,
  Linking,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { format } from 'date-fns';

export default function PaymentFailedScreen() {
  const params = useLocalSearchParams<{
    paymentId: string;
    amount: string;
    currency: string;
    errorMessage?: string;
  }>();
  
  const insets = useSafeAreaInsets();
  const scaleAnim = React.useRef(new Animated.Value(0)).current;

  const amount = parseFloat(params.amount || '0');
  const currency = params.currency || 'MYR';
  const errorMessage = params.errorMessage || 'Payment could not be completed. Please try again.';

  useEffect(() => {
    // Animate error icon
    Animated.spring(scaleAnim, {
      toValue: 1,
      tension: 50,
      friction: 7,
      useNativeDriver: true,
    }).start();
  }, []);

  const handleTryAgain = () => {
    router.replace('/top-up-wallet');
  };

  const handleContactSupport = () => {
    Linking.openURL('mailto:support@mydiesel.com?subject=Payment Failed - ' + params.paymentId);
  };

  const handleCancel = () => {
    router.replace('/home');
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top + 40, paddingBottom: insets.bottom + 24 }]}>
      {/* Error Icon */}
      <Animated.View style={[styles.iconContainer, { transform: [{ scale: scaleAnim }] }]}>
        <View style={styles.iconCircle}>
          <Ionicons name="close" size={64} color="white" />
        </View>
      </Animated.View>

      {/* Error Message */}
      <Text style={styles.title}>Payment Failed</Text>
      <Text style={styles.subtitle}>
        We couldn't process your payment
      </Text>

      {/* Error Details Card */}
      <View style={styles.errorCard}>
        <View style={styles.errorHeader}>
          <Ionicons name="alert-circle" size={24} color="#DC2626" />
          <Text style={styles.errorTitle}>What happened?</Text>
        </View>
        <Text style={styles.errorMessage}>{errorMessage}</Text>
      </View>

      {/* Transaction Details */}
      <View style={styles.detailsCard}>
        <Text style={styles.detailsTitle}>Transaction Details</Text>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Amount Attempted</Text>
          <Text style={styles.detailValue}>{currency} {amount.toFixed(2)}</Text>
        </View>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Transaction ID</Text>
          <Text style={styles.detailValue}>{params.paymentId.slice(0, 12)}...</Text>
        </View>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Date & Time</Text>
          <Text style={styles.detailValue}>{format(new Date(), 'dd MMM yyyy, HH:mm')}</Text>
        </View>
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Status</Text>
          <Text style={styles.detailValueError}>Failed</Text>
        </View>
      </View>

      {/* Common Reasons */}
      <View style={styles.reasonsCard}>
        <Text style={styles.reasonsTitle}>Common Reasons:</Text>
        <View style={styles.reasonItem}>
          <Ionicons name="ellipse" size={8} color="#6B7280" />
          <Text style={styles.reasonText}>Insufficient balance in your bank account</Text>
        </View>
        <View style={styles.reasonItem}>
          <Ionicons name="ellipse" size={8} color="#6B7280" />
          <Text style={styles.reasonText}>Payment cancelled in banking app</Text>
        </View>
        <View style={styles.reasonItem}>
          <Ionicons name="ellipse" size={8} color="#6B7280" />
          <Text style={styles.reasonText}>Network connection issue</Text>
        </View>
        <View style={styles.reasonItem}>
          <Ionicons name="ellipse" size={8} color="#6B7280" />
          <Text style={styles.reasonText}>Daily transaction limit reached</Text>
        </View>
      </View>

      <View style={styles.spacer} />

      {/* Action Buttons */}
      <TouchableOpacity style={styles.primaryButton} onPress={handleTryAgain}>
        <Ionicons name="refresh" size={20} color="white" style={{ marginRight: 8 }} />
        <Text style={styles.primaryButtonText}>Try Again</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.secondaryButton} onPress={handleContactSupport}>
        <Ionicons name="mail-outline" size={20} color="#0156CC" style={{ marginRight: 8 }} />
        <Text style={styles.secondaryButtonText}>Contact Support</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.textButton} onPress={handleCancel}>
        <Text style={styles.textButtonText}>Cancel</Text>
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
    backgroundColor: '#DC2626',
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#DC2626',
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
  errorCard: {
    width: '100%',
    backgroundColor: '#FEE2E2',
    borderRadius: 12,
    padding: 20,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#FCA5A5',
  },
  errorHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  errorTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#991B1B',
    marginLeft: 8,
  },
  errorMessage: {
    fontSize: 14,
    color: '#7F1D1D',
    lineHeight: 20,
  },
  detailsCard: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 16,
  },
  detailsTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 12,
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
  detailValueError: {
    fontSize: 14,
    color: '#DC2626',
    fontWeight: '600',
  },
  reasonsCard: {
    width: '100%',
    backgroundColor: '#F9FAFB',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  reasonsTitle: {
    fontSize: 12,
    fontWeight: '600',
    color: '#6B7280',
    marginBottom: 12,
    textTransform: 'uppercase',
  },
  reasonItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  reasonText: {
    fontSize: 14,
    color: '#374151',
    marginLeft: 8,
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
    flexDirection: 'row',
    justifyContent: 'center',
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
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 12,
  },
  secondaryButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#0156CC',
  },
  textButton: {
    paddingVertical: 12,
  },
  textButtonText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#6B7280',
  },
});
