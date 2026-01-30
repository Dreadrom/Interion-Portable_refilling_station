import { router, useLocalSearchParams } from 'expo-router';
import React, { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { getStationById } from '../../src/api/stations';
import { StationDetail, Pricing } from '../../src/types';

export default function StationInfoScreen() {
  const params = useLocalSearchParams<{ stationId: string }>();
  const [station, setStation] = useState<StationDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedProduct, setSelectedProduct] = useState<string | null>(null);
  const [refillType, setRefillType] = useState<'volume' | 'amount'>('volume');
  const [refillValue, setRefillValue] = useState('');

  useEffect(() => {
    loadStationInfo();
  }, [params.stationId]);

  const loadStationInfo = async () => {
    if (!params.stationId) {
      setError('No station ID provided');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await getStationById(params.stationId);
      setStation(data);
      // Auto-select first product if available
      if (data.pricing && data.pricing.length > 0) {
        setSelectedProduct(data.pricing[0].product);
      }
    } catch (err: any) {
      console.error('Error loading station:', err);
      setError(err.message || 'Failed to load station information');
      Alert.alert('Error', err.message || 'Failed to load station information');
    } finally {
      setLoading(false);
    }
  };

  const handleStartRefill = () => {
    if (!selectedProduct) {
      Alert.alert('Error', 'Please select a product');
      return;
    }
    if (!refillValue || parseFloat(refillValue) <= 0) {
      Alert.alert('Error', `Please enter a valid ${refillType}`);
      return;
    }

    const value = parseFloat(refillValue);
    const price = getSelectedPrice();
    
    if (!price) {
      Alert.alert('Error', 'Product pricing not available');
      return;
    }

    // Calculate volume and amount
    const requestedVolume = refillType === 'volume' ? value : value / price.unitPrice;
    const requestedAmount = refillType === 'amount' ? value : value * price.unitPrice;

    // Minimum limits
    const MIN_VOLUME = 1; // 1 liter minimum
    const MIN_AMOUNT = 5; // 5 MYR minimum
    
    if (requestedVolume < MIN_VOLUME) {
      Alert.alert(
        'Amount Too Low',
        `Minimum refill is ${MIN_VOLUME} liters (${price.currency} ${(MIN_VOLUME * price.unitPrice).toFixed(2)})`
      );
      return;
    }

    if (requestedAmount < MIN_AMOUNT) {
      Alert.alert(
        'Amount Too Low',
        `Minimum refill amount is ${price.currency} ${MIN_AMOUNT.toFixed(2)} (${(MIN_AMOUNT / price.unitPrice).toFixed(2)} liters)`
      );
      return;
    }

    // Maximum limits from station config
    const maxVolume = station?.config?.maxDispenseVolume || 100; // Default 100L
    const maxAmount = station?.config?.maxDispenseAmount || 500; // Default 500 MYR

    if (requestedVolume > maxVolume) {
      Alert.alert(
        'Amount Too High',
        `Maximum refill is ${maxVolume} liters per transaction (${price.currency} ${(maxVolume * price.unitPrice).toFixed(2)})`
      );
      return;
    }

    if (requestedAmount > maxAmount) {
      Alert.alert(
        'Amount Too High',
        `Maximum refill amount is ${price.currency} ${maxAmount.toFixed(2)} per transaction (${(maxAmount / price.unitPrice).toFixed(2)} liters)`
      );
      return;
    }

    // Check tank availability
    const selectedTank = station?.tankStatus?.find(t => t.product === selectedProduct);
    
    if (!selectedTank) {
      Alert.alert('Error', 'Tank information not available for selected product');
      return;
    }

    if (selectedTank.levelLitres < requestedVolume) {
      Alert.alert(
        'Insufficient Fuel',
        `The station only has ${selectedTank.levelLitres.toFixed(2)} liters of ${selectedProduct} available.\n\nPlease reduce your refill amount or choose a different station.`
      );
      return;
    }

    // Warning for low tank levels (< 20% after refill)
    const remainingAfterRefill = selectedTank.levelLitres - requestedVolume;
    const percentageRemaining = (remainingAfterRefill / selectedTank.capacityLitres) * 100;
    
    if (percentageRemaining < 20 && percentageRemaining >= 0) {
      Alert.alert(
        'Low Tank Warning',
        `This refill will leave the tank at ${percentageRemaining.toFixed(0)}% capacity. The station may run out of ${selectedProduct} soon.\n\nDo you want to proceed?`,
        [
          { text: 'Cancel', style: 'cancel' },
          { 
            text: 'Proceed',
            onPress: () => proceedToPreAuth()
          }
        ]
      );
      return;
    }

    // All validations passed
    proceedToPreAuth();
  };

  const proceedToPreAuth = () => {
    router.push({
      pathname: './pre-authorization',
      params: {
        stationId: params.stationId,
        product: selectedProduct,
        refillType: refillType,
        refillValue: refillValue,
      },
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'IDLE':
        return '#10B981';
      case 'DISPENSING':
        return '#3B82F6';
      case 'ALARM':
        return '#EF4444';
      case 'OFFLINE':
        return '#6B7280';
      case 'MAINTENANCE':
        return '#F59E0B';
      default:
        return '#6B7280';
    }
  };

  const getSelectedPrice = () => {
    if (!station?.pricing || !selectedProduct) return null;
    return station.pricing.find(p => p.product === selectedProduct);
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Loading station information...</Text>
      </View>
    );
  }

  if (error || !station) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>{error || 'Station not found'}</Text>
        <TouchableOpacity
          style={[globalStyles.primaryButton, { marginTop: 20 }]}
          onPress={loadStationInfo}
        >
          <Text style={globalStyles.primaryButtonText}>Retry</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={() => router.back()}
        >
          <Text style={globalStyles.secondaryButtonText}>Go Back</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.stationName}>{station.name}</Text>
        <View style={[styles.statusBadge, { backgroundColor: getStatusColor(station.status) }]}>
          <Text style={styles.statusText}>{station.status}</Text>
        </View>
      </View>

      {station.location?.address && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>📍 Location</Text>
          <Text style={styles.address}>{station.location.address}</Text>
          <Text style={styles.coordinates}>
            {station.location.latitude.toFixed(6)}, {station.location.longitude.toFixed(6)}
          </Text>
        </View>
      )}

      {station.pricing && station.pricing.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>⛽ Select Product</Text>
          {station.pricing.map((price: Pricing) => {
            const tank = station.tankStatus?.find(t => t.product === price.product);
            const isAvailable = tank && tank.levelLitres > 0;
            const isLowLevel = tank && tank.lowLevelAlarm;
            
            return (
              <TouchableOpacity
                key={price.id}
                style={[
                  styles.productCard,
                  selectedProduct === price.product && styles.productCardSelected,
                  !isAvailable && styles.productCardDisabled,
                ]}
                onPress={() => isAvailable && setSelectedProduct(price.product)}
                disabled={!isAvailable}
              >
                <View style={styles.productCardContent}>
                  <View>
                    <Text style={[
                      styles.productName,
                      selectedProduct === price.product && styles.productNameSelected,
                      !isAvailable && styles.productNameDisabled,
                    ]}>
                      {price.product}
                    </Text>
                    {tank && (
                      <Text style={[
                        styles.productAvailability,
                        isLowLevel && styles.productAvailabilityLow,
                        !isAvailable && styles.productAvailabilityEmpty,
                      ]}>
                        {tank.levelLitres > 0 
                          ? `${tank.levelLitres.toLocaleString()}L available`
                          : 'Out of stock'}
                        {isLowLevel && tank.levelLitres > 0 && ' ⚠️'}
                      </Text>
                    )}
                  </View>
                  <Text style={[
                    styles.productPrice,
                    selectedProduct === price.product && styles.productPriceSelected,
                    !isAvailable && styles.productPriceDisabled,
                  ]}>
                    {price.currency} {price.unitPrice.toFixed(2)}/L
                  </Text>
                </View>
                {selectedProduct === price.product && isAvailable && (
                  <Text style={styles.checkmark}>✓</Text>
                )}
              </TouchableOpacity>
            );
          })}
        </View>
      )}

      {selectedProduct && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>💧 Refill Amount</Text>
          
          {/* Show limits info */}
          <View style={styles.limitsInfo}>
            <Text style={styles.limitsText}>
              Min: 1L • Max: {station.config?.maxDispenseVolume || 100}L per transaction
            </Text>
          </View>
          
          <View style={styles.refillTypeContainer}>
            <TouchableOpacity
              style={[
                styles.refillTypeButton,
                refillType === 'volume' && styles.refillTypeButtonActive,
              ]}
              onPress={() => setRefillType('volume')}
            >
              <Text style={[
                styles.refillTypeText,
                refillType === 'volume' && styles.refillTypeTextActive,
              ]}>
                By Volume (L)
              </Text>
            </TouchableOpacity>
            
            <TouchableOpacity
              style={[
                styles.refillTypeButton,
                refillType === 'amount' && styles.refillTypeButtonActive,
              ]}
              onPress={() => setRefillType('amount')}
            >
              <Text style={[
                styles.refillTypeText,
                refillType === 'amount' && styles.refillTypeTextActive,
              ]}>
                By Amount ({station.pricing?.[0]?.currency || 'MYR'})
              </Text>
            </TouchableOpacity>
          </View>

          <TextInput
            style={styles.refillInput}
            placeholder={`Enter ${refillType === 'volume' ? 'volume in litres' : 'amount'}`}
            keyboardType="decimal-pad"
            value={refillValue}
            onChangeText={setRefillValue}
          />

          {refillValue && getSelectedPrice() && (
            <View style={styles.estimateContainer}>
              <Text style={styles.estimateLabel}>Estimated:</Text>
              <Text style={styles.estimateValue}>
                {refillType === 'volume'
                  ? `${getSelectedPrice()?.currency} ${(parseFloat(refillValue) * (getSelectedPrice()?.unitPrice || 0)).toFixed(2)}`
                  : `${(parseFloat(refillValue) / (getSelectedPrice()?.unitPrice || 1)).toFixed(2)} L`
                }
              </Text>
            </View>
          )}
        </View>
      )}

      <View style={styles.centeredButtonContainer}>
        <TouchableOpacity
          style={globalStyles.primaryButton}
          onPress={handleStartRefill}
        >
          <Text style={globalStyles.primaryButtonText}>Start Refill</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={() => router.back()}
        >
          <Text style={globalStyles.secondaryButtonText}>Back</Text>
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
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  header: {
    backgroundColor: '#fff',
    padding: 24,
    paddingTop: 60,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  stationName: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 12,
  },
  statusBadge: {
    alignSelf: 'flex-start',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 12,
  },
  statusText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 12,
  },
  section: {
    backgroundColor: '#fff',
    padding: 20,
    marginTop: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 16,
  },
  address: {
    fontSize: 16,
    color: '#374151',
    marginBottom: 4,
  },
  coordinates: {
    fontSize: 14,
    color: '#6B7280',
  },
  productCard: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
    borderRadius: 12,
    borderWidth: 2,
    borderColor: '#E5E7EB',
    marginBottom: 12,
    backgroundColor: '#fff',
  },
  productCardSelected: {
    borderColor: '#10B981',
    backgroundColor: '#F0FDF4',
  },
  productCardDisabled: {
    borderColor: '#E5E7EB',
    backgroundColor: '#F9FAFB',
    opacity: 0.6,
  },
  productCardContent: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  productName: {
    fontSize: 18,
    fontWeight: '700',
    color: '#111827',
  },
  productNameSelected: {
    color: '#10B981',
  },
  productNameDisabled: {
    color: '#9CA3AF',
  },
  productAvailability: {
    fontSize: 13,
    color: '#10B981',
    marginTop: 4,
  },
  productAvailabilityLow: {
    color: '#F59E0B',
  },
  productAvailabilityEmpty: {
    color: '#EF4444',
  },
  productPrice: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
  },
  productPriceSelected: {
    color: '#10B981',
  },
  productPriceDisabled: {
    color: '#9CA3AF',
  },
  checkmark: {
    fontSize: 24,
    color: '#10B981',
    marginLeft: 12,
    fontWeight: 'bold',
  },
  limitsInfo: {
    backgroundColor: '#F3F4F6',
    padding: 12,
    borderRadius: 8,
    marginBottom: 16,
  },
  limitsText: {
    fontSize: 13,
    color: '#6B7280',
    textAlign: 'center',
  },
  refillTypeContainer: {
    flexDirection: 'row',
    marginBottom: 16,
    borderRadius: 8,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  refillTypeButton: {
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 16,
    backgroundColor: '#fff',
    alignItems: 'center',
  },
  refillTypeButtonActive: {
    backgroundColor: '#10B981',
  },
  refillTypeText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6B7280',
  },
  refillTypeTextActive: {
    color: '#fff',
  },
  refillInput: {
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderRadius: 8,
    padding: 16,
    fontSize: 16,
    backgroundColor: '#fff',
    marginBottom: 16,
  },
  estimateContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 12,
    backgroundColor: '#F0FDF4',
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#10B981',
  },
  estimateLabel: {
    fontSize: 14,
    color: '#6B7280',
  },
  estimateValue: {
    fontSize: 18,
    fontWeight: '700',
    color: '#10B981',
  },
  centeredButtonContainer: {
    padding: 20,
    backgroundColor: '#fff',
    marginTop: 12,
    alignItems: 'center',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
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
  },
});
