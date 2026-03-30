import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
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
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { globalStyles } from '../../src/styles/globalStyles';
import { getStationById } from '../../src/api/stations';
import { StationDetail, Pricing } from '../../src/types';
import { getDemoStationById } from '../../src/data/demoStations';

export default function StationInfoScreen() {
  const insets = useSafeAreaInsets();
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
      // Backend unavailable — fall back to demo data
      const demoData = getDemoStationById(params.stationId);
      if (demoData) {
        // Expected in demo/dev mode: backend not reachable, using local demo station
        console.warn('Station API unavailable, using demo data for:', params.stationId);
        setStation(demoData);
        if (demoData.pricing && demoData.pricing.length > 0) {
          setSelectedProduct(demoData.pricing[0].product);
        }
      } else {
        setError(err.message || 'Failed to load station information');
        Alert.alert('Error', err.message || 'Failed to load station information');
      }
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

    // Maximum limits — reduced to 10 L when tank lowLevelAlarm is active
    const { maxVol: maxVolume, maxAmt: maxAmount, isLow: isLowTankAlarm } = getEffectiveLimits();

    if (requestedVolume > maxVolume) {
      Alert.alert(
        isLowTankAlarm ? 'Low Tank Limit' : 'Amount Too High',
        isLowTankAlarm
          ? `This station's tank is critically low. To keep the service running for all vehicles, refills are limited to ${maxVolume} L until the tanker arrives.`
          : `Maximum refill is ${maxVolume} liters per transaction (${price.currency} ${(maxVolume * price.unitPrice).toFixed(2)})`
      );
      return;
    }

    if (requestedAmount > maxAmount) {
      Alert.alert(
        isLowTankAlarm ? 'Low Tank Limit' : 'Amount Too High',
        isLowTankAlarm
          ? `This station's tank is critically low. To keep the service running for all vehicles, refills are limited to ${price.currency} ${maxAmount.toFixed(2)} until the tanker arrives.`
          : `Maximum refill amount is ${price.currency} ${maxAmount.toFixed(2)} per transaction (${(maxAmount / price.unitPrice).toFixed(2)} liters)`
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
        'Insufficient Solution',
        `The station only has ${selectedTank.levelLitres.toFixed(2)} L of Solution available.\n\nPlease reduce your refill amount or choose a different station.`
      );
      return;
    }

    // Warning for low tank levels (< 20% after refill)
    const remainingAfterRefill = selectedTank.levelLitres - requestedVolume;
    const percentageRemaining = (remainingAfterRefill / selectedTank.capacityLitres) * 100;
    
    if (percentageRemaining < 20 && percentageRemaining >= 0) {
      Alert.alert(
        'Low Tank Warning',
        `This refill will leave the tank at ${percentageRemaining.toFixed(0)}% capacity. The station may run out of Solution soon.\n\nDo you want to proceed?`,
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

  /**
   * When the tank is at or below 10% (lowLevelAlarm), cap transactions at 20 L
   * so enough solution remains for all users before the tanker arrives (within 24 h).
   */
  const LOW_TANK_MAX_VOLUME = 20;
  const getEffectiveLimits = () => {
    const tank = station?.tankStatus?.find(t => t.product === selectedProduct);
    const isLow = tank?.lowLevelAlarm === true;
    const price = getSelectedPrice();
    const configMax = station?.config?.maxDispenseVolume || 100;
    const maxVol = isLow ? Math.min(LOW_TANK_MAX_VOLUME, configMax) : configMax;
    const maxAmt = isLow
      ? maxVol * (price?.unitPrice || 10)
      : (station?.config?.maxDispenseAmount || 500);
    return { isLow, maxVol, maxAmt };
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
      <View style={[styles.header, { paddingTop: insets.top + 24 }]}>
        <Text style={styles.stationName}>{station.name}</Text>
        <View style={[styles.statusBadge, { backgroundColor: getStatusColor(station.status) }]}>
          <Text style={styles.statusText}>{station.status}</Text>
        </View>
      </View>

      {station.location?.address && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Location</Text>
          <Text style={styles.address}>{station.location.address}</Text>
          <Text style={styles.coordinates}>
            {station.location.latitude.toFixed(6)}, {station.location.longitude.toFixed(6)}
          </Text>
        </View>
      )}

      {/* In-use banner when station is currently dispensing */}
      {station.status === 'DISPENSING' && (
        <View style={styles.inUseBanner}>
          <Ionicons name="water" size={18} color="#1D4ED8" />
          <Text style={styles.inUseBannerText}>Station currently in use</Text>
        </View>
      )}

      {/* Low-tank critical banner */}
      {station.tankStatus?.[0]?.lowLevelAlarm && (
        <View style={styles.lowTankBanner}>
          <Ionicons name="warning" size={18} color="#92400E" />
          <Text style={styles.lowTankBannerText}>
            Tank critically low — owner has been notified to dispatch a tanker
          </Text>
        </View>
      )}

      {/* Pump status */}
      {station.pumps && station.pumps.length > 0 && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Pumps</Text>
          <View style={styles.pumpsRow}>
            {station.pumps.map((pump) => {
              const pumpColor =
                pump.status === 'IDLE' ? '#10B981'
                : pump.status === 'IN_USE' ? '#3B82F6'
                : pump.status === 'FAULT' ? '#EF4444'
                : '#6B7280';
              const pumpIcon =
                pump.status === 'IDLE' ? 'checkmark-circle-outline'
                : pump.status === 'IN_USE' ? 'water'
                : pump.status === 'FAULT' ? 'alert-circle-outline'
                : 'close-circle-outline';
              return (
                <View key={pump.id} style={styles.pumpCard}>
                  <Ionicons name={pumpIcon as any} size={24} color={pumpColor} />
                  <Text style={styles.pumpNumber}>Pump {pump.pumpNumber}</Text>
                  <Text style={[styles.pumpStatus, { color: pumpColor }]}>{pump.status.replace('_', ' ')}</Text>
                </View>
              );
            })}
          </View>
        </View>
      )}

      {/* AdBlue info card */}
      {(() => {
        const price = station.pricing?.[0];
        const tank = station.tankStatus?.[0];
        if (!price) return null;
        const fillPct = tank
          ? Math.min(100, (tank.levelLitres / tank.capacityLitres) * 100)
          : 0;
        const barColor = tank?.lowLevelAlarm ? '#F59E0B' : '#10B981';
        return (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>AdBlue</Text>

            <View style={styles.solutionPriceRow}>
              <Ionicons name="pricetag-outline" size={20} color="#10B981" />
              <Text style={styles.solutionPrice}>
                {price.currency} {price.unitPrice.toFixed(2)}
                <Text style={styles.solutionPriceUnit}> / L</Text>
              </Text>
            </View>

            {tank && (
              <View style={styles.tankSection}>
                <View style={styles.tankLabelRow}>
                  <View style={styles.tankLabelLeft}>
                    <Ionicons name="flask-outline" size={16} color="#6B7280" />
                    <Text style={styles.tankLabel}>Tank Level</Text>
                  </View>
                  <Text style={[styles.tankPct, tank.lowLevelAlarm && styles.tankPctLow]}>
                    {Math.round(fillPct)}%
                  </Text>
                </View>
                <View style={styles.tankBarTrack}>
                  <View
                    style={[
                      styles.tankBarFill,
                      { width: `${Math.round(fillPct)}%`, backgroundColor: barColor },
                    ]}
                  />
                </View>
                <Text style={[styles.tankRemaining, tank.lowLevelAlarm && styles.tankRemainingLow]}>
                  {tank.levelLitres.toLocaleString()} L remaining
                  {tank.lowLevelAlarm ? ' — Low stock' : ''}
                </Text>
              </View>
            )}
          </View>
        );
      })()}

      {selectedProduct && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Refill Amount</Text>

          {/* Low tank warning banner */}
          {(() => {
            const { isLow, maxVol } = getEffectiveLimits();
            return isLow ? (
              <View style={styles.lowTankBanner}>
                <Ionicons name="warning" size={16} color="#92400E" />
                <Text style={styles.lowTankBannerText}>
                  Tank critically low — refills are limited to {maxVol} L per transaction to keep the service available for all vehicles until the tanker arrives (within 24 h).
                </Text>
              </View>
            ) : null;
          })()}
          
          {/* Show limits info */}
          <View style={styles.limitsInfo}>
            <Text style={styles.limitsText}>
              Min: 1L • Max: {getEffectiveLimits().maxVol}L per transaction
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
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  stationName: {
    fontSize: 22,
    fontWeight: '700',
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
  lowTankBanner: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    backgroundColor: '#FEF3C7',
    borderWidth: 1,
    borderColor: '#F59E0B',
    borderRadius: 10,
    padding: 12,
    marginBottom: 12,
    gap: 8,
  },
  lowTankBannerText: {
    flex: 1,
    fontSize: 13,
    color: '#92400E',
    lineHeight: 18,
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
  pumpsRow: {
    flexDirection: 'row',
    gap: 12,
  },
  pumpCard: {
    flex: 1,
    alignItems: 'center',
    gap: 6,
    paddingVertical: 16,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: '#E5E7EB',
    backgroundColor: '#F9FAFB',
  },
  pumpNumber: {
    fontSize: 14,
    fontWeight: '700',
    color: '#111827',
  },
  pumpStatus: {
    fontSize: 12,
    fontWeight: '600',
  },
  inUseBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    backgroundColor: '#EFF6FF',
    borderLeftWidth: 4,
    borderLeftColor: '#3B82F6',
    padding: 14,
    marginTop: 12,
  },
  inUseBannerText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1D4ED8',
  },
  solutionPriceRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    marginBottom: 20,
  },
  solutionPrice: {
    fontSize: 28,
    fontWeight: '700',
    color: '#10B981',
  },
  solutionPriceUnit: {
    fontSize: 16,
    fontWeight: '400',
    color: '#6B7280',
  },
  tankSection: {
    marginTop: 4,
  },
  tankLabelRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  tankLabelLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  tankLabel: {
    fontSize: 14,
    color: '#6B7280',
    fontWeight: '500',
  },
  tankPct: {
    fontSize: 14,
    fontWeight: '700',
    color: '#10B981',
  },
  tankPctLow: {
    color: '#F59E0B',
  },
  tankBarTrack: {
    height: 10,
    backgroundColor: '#E5E7EB',
    borderRadius: 5,
    overflow: 'hidden',
    marginBottom: 8,
  },
  tankBarFill: {
    height: '100%',
    borderRadius: 5,
  },
  tankRemaining: {
    fontSize: 14,
    color: '#374151',
    fontWeight: '500',
  },
  tankRemainingLow: {
    color: '#F59E0B',
  },
});
