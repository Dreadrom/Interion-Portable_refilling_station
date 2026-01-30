import { router, useLocalSearchParams } from 'expo-router';
import React, { useState, useEffect } from 'react';
import {
  ActivityIndicator,
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { getStationById } from '../../src/api/stations';
import { StationDetail, ProductType } from '../../src/types';

export default function PreAuthorizationScreen() {
  const params = useLocalSearchParams();
  const [station, setStation] = useState<StationDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [authorizing, setAuthorizing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const stationId = params.stationId as string;
  const product = params.product as ProductType;
  const refillType = params.refillType as 'volume' | 'amount';
  const refillValue = params.refillValue as string;

  useEffect(() => {
    loadStationInfo();
  }, []);

  const loadStationInfo = async () => {
    try {
      const data = await getStationById(stationId);
      setStation(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load station');
    } finally {
      setLoading(false);
    }
  };

  const getSelectedPrice = () => {
    if (!station?.pricing || !product) return null;
    return station.pricing.find(p => p.product === product);
  };

  const calculateEstimate = () => {
    const price = getSelectedPrice();
    if (!price || !refillValue) return { volume: 0, amount: 0 };

    const value = parseFloat(refillValue);
    if (refillType === 'volume') {
      return {
        volume: value,
        amount: value * price.unitPrice,
      };
    } else {
      return {
        volume: value / price.unitPrice,
        amount: value,
      };
    }
  };

  const handleAuthorize = async () => {
    setAuthorizing(true);
    
    try {
      // Simulate wallet hold/pre-authorization
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const estimate = calculateEstimate();
      const holdAmount = estimate.amount * 1.1; // 10% buffer
      
      // Simulate pump assignment (in real scenario, this would come from the backend)
      const assignedNozzle = Math.floor(Math.random() * 4) + 1; // Random nozzle 1-4
      
      // Navigate to pump unlocked screen
      router.push({
        pathname: './pump-unlocked',
        params: {
          stationId: stationId,
          product: product,
          presetType: refillType.toUpperCase(),
          presetValue: refillValue,
          holdAmount: `${getSelectedPrice()?.currency} ${holdAmount.toFixed(2)}`,
          nozzle: assignedNozzle.toString(),
        },
      });
    } catch (err: any) {
      Alert.alert('Authorization Failed', err.message || 'Failed to authorize payment');
    } finally {
      setAuthorizing(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  if (error || !station) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>{error || 'Station not found'}</Text>
        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 20 }]}
          onPress={() => router.back()}
        >
          <Text style={globalStyles.secondaryButtonText}>Go Back</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const estimate = calculateEstimate();
  const price = getSelectedPrice();
  const holdAmount = estimate.amount * 1.1; // 10% buffer for pre-authorization

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <Text style={styles.title}>Pre-Authorization</Text>
        <Text style={styles.subtitle}>Confirm your refill and reserve a pump</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Station</Text>
        <Text style={styles.stationName}>{station.name}</Text>
        <Text style={styles.stationAddress}>{station.location?.address}</Text>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>Refill Details</Text>
        
        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Product:</Text>
          <Text style={styles.detailValue}>{product}</Text>
        </View>

        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Unit Price:</Text>
          <Text style={styles.detailValue}>
            {price?.currency} {price?.unitPrice.toFixed(2)}/L
          </Text>
        </View>

        <View style={styles.separator} />

        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Target Volume:</Text>
          <Text style={styles.detailValue}>{estimate.volume.toFixed(2)} L</Text>
        </View>

        <View style={styles.detailRow}>
          <Text style={styles.detailLabel}>Estimated Amount:</Text>
          <Text style={styles.detailValue}>
            {price?.currency} {estimate.amount.toFixed(2)}
          </Text>
        </View>
      </View>

      <View style={[styles.card, styles.authCard]}>
        <Text style={styles.cardTitle}>💳 Wallet Hold</Text>
        <Text style={styles.authDescription}>
          We'll temporarily hold the amount below in your wallet. You'll only be charged for the actual amount dispensed.
        </Text>

        <View style={styles.holdAmountContainer}>
          <Text style={styles.holdLabel}>Hold Amount:</Text>
          <Text style={styles.holdAmount}>
            {price?.currency} {holdAmount.toFixed(2)}
          </Text>
        </View>

        <Text style={styles.holdNote}>
          ⓘ Includes 10% buffer. Excess will be released after dispensing.
        </Text>
      </View>

      <View style={styles.warningCard}>
        <Text style={styles.warningTitle}>⚠️ Important</Text>
        <Text style={styles.warningText}>
          • A pump will be reserved for 5 minutes{'\n'}
          • Please proceed to the station immediately{'\n'}
          • Authorization will be cancelled if not used
        </Text>
      </View>

      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[
            globalStyles.primaryButton,
            authorizing && styles.buttonDisabled,
          ]}
          onPress={handleAuthorize}
          disabled={authorizing}
        >
          {authorizing ? (
            <View style={styles.buttonContent}>
              <ActivityIndicator size="small" color="#fff" />
              <Text style={[globalStyles.primaryButtonText, { marginLeft: 12 }]}>
                Authorizing...
              </Text>
            </View>
          ) : (
            <Text style={globalStyles.primaryButtonText}>
              Authorize & Reserve Pump
            </Text>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={() => router.back()}
          disabled={authorizing}
        >
          <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
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
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#F9FAFB',
  },
  header: {
    marginBottom: 24,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 8,
  },
  subtitle: {
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
    marginBottom: 12,
    letterSpacing: 0.5,
  },
  stationName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  stationAddress: {
    fontSize: 14,
    color: '#6B7280',
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
  authCard: {
    backgroundColor: '#F0FDF4',
    borderWidth: 2,
    borderColor: '#10B981',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  authDescription: {
    fontSize: 14,
    color: '#4B5563',
    marginBottom: 16,
    lineHeight: 20,
  },
  holdAmountContainer: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    alignItems: 'center',
  },
  holdLabel: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 8,
  },
  holdAmount: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#10B981',
  },
  holdNote: {
    fontSize: 13,
    color: '#059669',
    textAlign: 'center',
  },
  warningCard: {
    backgroundColor: '#FEF3C7',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
    borderWidth: 1,
    borderColor: '#F59E0B',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  warningTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#92400E',
    marginBottom: 8,
  },
  warningText: {
    fontSize: 14,
    color: '#78350F',
    lineHeight: 20,
  },
  buttonContainer: {
    marginTop: 8,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  buttonContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#6B7280',
  },
  errorText: {
    fontSize: 16,
    color: '#EF4444',
    textAlign: 'center',
    marginBottom: 8,
  },
});
