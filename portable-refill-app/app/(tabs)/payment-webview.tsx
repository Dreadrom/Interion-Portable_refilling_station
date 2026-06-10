import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useState, useEffect, useRef } from 'react';
import {
  Alert,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  ActivityIndicator,
  Linking,
  Platform,
} from 'react-native';
import { WebView } from 'react-native-webview';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { usePaymentStore } from '../../src/stores/usePaymentStore';

export default function PaymentWebViewScreen() {
  const params = useLocalSearchParams<{
    paymentId: string;
    amount: string;
    currency: string;
    paymentUrl: string;
    paymentData?: string; // JSON string
  }>();
  
  const insets = useSafeAreaInsets();
  const { pollStatus, stopPolling, currentPayment } = usePaymentStore();
  const [isLoading, setIsLoading] = useState(true);
  const [currentUrl, setCurrentUrl] = useState('');
  const hasStartedPolling = useRef(false);

  const amount = parseFloat(params.amount || '0');
  const currency = params.currency || 'MYR';

  // Define polling function first
  const startStatusPolling = () => {
    console.log('🔍 Starting payment status polling...');
    pollStatus(
      (payment) => {
        // Success
        console.log('✅ Payment successful:', payment.id);
        stopPolling();
        router.replace({
          pathname: '/payment-success',
          params: {
            paymentId: payment.id,
            amount: payment.amount.toString(),
            currency: payment.currency,
          },
        });
      },
      (payment) => {
        // Failed/Cancelled/Expired
        console.log('❌ Payment failed/cancelled:', payment.status);
        stopPolling();
        if (payment.status === 'FAILED') {
          router.replace({
            pathname: '/payment-failed',
            params: {
              paymentId: payment.id,
              amount: payment.amount.toString(),
              currency: payment.currency,
            },
          });
        } else if (payment.status === 'EXPIRED') {
          router.replace({
            pathname: '/payment-expired',
            params: {
              paymentId: payment.id,
              amount: payment.amount.toString(),
              currency: payment.currency,
            },
          });
        } else {
          router.back();
        }
      }
    );
  };

  // Set current payment in store from params
  useEffect(() => {
    if (params.paymentId && !currentPayment) {
      // Initialize payment in store with data from params
      console.log('💳 Initializing payment in store:', params.paymentId);
      usePaymentStore.setState({
        currentPayment: {
          id: params.paymentId,
          userId: '',
          stationId: '',
          amount: amount,
          currency: currency,
          method: 'FIUU_QR',
          status: 'PENDING',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          expiresAt: new Date(Date.now() + 30 * 60 * 1000).toISOString(),
          paymentUrl: params.paymentUrl,
          paymentData: params.paymentData ? JSON.parse(params.paymentData) : undefined,
        },
      });
    }
  }, [params.paymentId]);

  // For web platform or demo payments, start polling immediately
  useEffect(() => {
    // Start polling after a brief delay for demo payments or web platform
    const isDemo = params.paymentId?.startsWith('demo-');
    console.log('🔄 Payment webview mounted, demo?', isDemo, 'web?', Platform.OS === 'web');
    
    if ((Platform.OS === 'web' || isDemo) && !hasStartedPolling.current && currentPayment) {
      const timer = setTimeout(() => {
        console.log('⏰ Starting auto-polling for web/demo payment');
        hasStartedPolling.current = true;
        startStatusPolling();
      }, 2000); // Wait 2 seconds before starting poll

      return () => clearTimeout(timer);
    }
  }, [params.paymentId, currentPayment]);

  // Generate HTML form to auto-submit to Fiuu
  const generatePaymentHTML = () => {
    const paymentData = params.paymentData ? JSON.parse(params.paymentData) : {};
    
    const formFields = Object.entries(paymentData)
      .map(([key, value]) => `<input type="hidden" name="${key}" value="${value}">`)
      .join('\\n');

    return `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Redirecting to Payment...</title>
        <style>
          body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: #f9fafb;
          }
          .loader {
            text-align: center;
          }
          .spinner {
            border: 4px solid #e5e7eb;
            border-top: 4px solid #0156CC;
            border-radius: 50%;
            width: 48px;
            height: 48px;
            animation: spin 1s linear infinite;
            margin: 0 auto 16px;
          }
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
          .text {
            color: #6b7280;
            font-size: 16px;
          }
        </style>
      </head>
      <body>
        <div class="loader">
          <div class="spinner"></div>
          <div class="text">Redirecting to payment gateway...</div>
        </div>
        <form id="paymentForm" method="POST" action="${params.paymentUrl}">
          ${formFields}
        </form>
        <script>
          // Auto-submit form after a brief delay
          setTimeout(function() {
            document.getElementById('paymentForm').submit();
          }, 500);
        </script>
      </body>
      </html>
    `;
  };

  const handleNavigationStateChange = (navState: any) => {
    setCurrentUrl(navState.url);
    setIsLoading(navState.loading);

    // Check if returned from payment gateway
    const url = navState.url.toLowerCase();
    if (url.includes('/payment/return') || url.includes('/payment/fiuu/return')) {
      // Payment completed, start polling for status
      if (!hasStartedPolling.current) {
        hasStartedPolling.current = true;
        startStatusPolling();
      }
    }
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
            stopPolling();
            router.back();
          },
        },
      ]
    );
  };

  const handleOpenInBrowser = () => {
    Alert.alert(
      'Open in Browser',
      'Open payment page in your default browser?',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Open',
          onPress: () => {
            Linking.openURL(params.paymentUrl);
            // Start polling immediately
            if (!hasStartedPolling.current) {
              hasStartedPolling.current = true;
              startStatusPolling();
            }
          },
        },
      ]
    );
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.headerLeft}>
          <TouchableOpacity onPress={handleCancel} style={styles.headerButton}>
            <Ionicons name="close" size={24} color="#1F2937" />
          </TouchableOpacity>
        </View>
        
        <View style={styles.headerCenter}>
          <Text style={styles.headerTitle}>Payment</Text>
          <Text style={styles.headerSubtitle}>
            {currency} {amount.toFixed(2)}
          </Text>
        </View>

        <View style={styles.headerRight}>
          <TouchableOpacity onPress={handleOpenInBrowser} style={styles.headerButton}>
            <Ionicons name="open-outline" size={24} color="#1F2937" />
          </TouchableOpacity>
        </View>
      </View>

      {/* Loading indicator */}
      {isLoading && (
        <View style={styles.loadingOverlay}>
          <ActivityIndicator size="large" color="#0156CC" />
          <Text style={styles.loadingText}>Loading payment page...</Text>
        </View>
      )}

      {/* WebView */}
      <WebView
        source={{ html: generatePaymentHTML() }}
        style={styles.webview}
        onNavigationStateChange={handleNavigationStateChange}
        startInLoadingState={true}
        javaScriptEnabled={true}
        domStorageEnabled={true}
        sharedCookiesEnabled={true}
        onError={(syntheticEvent) => {
          const { nativeEvent } = syntheticEvent;
          console.warn('WebView error: ', nativeEvent);
          Alert.alert(
            'Connection Error',
            'Failed to load payment page. Please check your internet connection and try again.',
            [
              { text: 'Cancel', onPress: () => router.back() },
              { text: 'Retry', onPress: () => {} },
            ]
          );
        }}
      />

      {/* Info Footer */}
      <View style={styles.footer}>
        <View style={styles.infoRow}>
          <Ionicons name="shield-checkmark" size={16} color="#10B981" />
          <Text style={styles.infoText}>Secure payment powered by Fiuu</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    backgroundColor: '#fff',
  },
  headerLeft: {
    width: 40,
  },
  headerCenter: {
    flex: 1,
    alignItems: 'center',
  },
  headerRight: {
    width: 40,
    alignItems: 'flex-end',
  },
  headerButton: {
    padding: 4,
  },
  headerTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: '#1F2937',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#6B7280',
    marginTop: 2,
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 10,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#6B7280',
  },
  webview: {
    flex: 1,
  },
  footer: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    backgroundColor: '#F9FAFB',
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  infoText: {
    fontSize: 13,
    color: '#6B7280',
    marginLeft: 6,
  },
});
