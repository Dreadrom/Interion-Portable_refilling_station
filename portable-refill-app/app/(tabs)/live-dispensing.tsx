import { router, useLocalSearchParams } from 'expo-router';
import React, { useState, useEffect, useRef } from 'react';
import {
  ActivityIndicator,
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Animated,
} from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { getStationById } from '../../src/api/stations';
import { StationDetail, ProductType } from '../../src/types';
import { transactionStore } from '../../src/utils/transactionStore';

type DispenseStatus = 'IDLE' | 'IN_PROGRESS' | 'COMPLETED' | 'STOPPED' | 'EMERGENCY_STOPPED';

export default function LiveDispensingScreen() {
  const params = useLocalSearchParams();
  const [station, setStation] = useState<StationDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState<DispenseStatus>('IN_PROGRESS'); // Start dispensing immediately
  
  // Dispensing data
  const [volumeDispensed, setVolumeDispensed] = useState(0);
  const [amountCharged, setAmountCharged] = useState(0);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [pulseAnim] = useState(new Animated.Value(1));
  
  // Use ref to store timer so we can clear it
  const dispensingTimerRef = useRef<number | null>(null);

  const stationId = params.stationId as string;
  const product = params.product as ProductType;
  const presetType = params.presetType as string;
  const presetValue = parseFloat(params.presetValue as string);
  const holdAmount = params.holdAmount as string;
  const nozzle = params.nozzle as string;

  // Get unit price from station data
  const [unitPrice, setUnitPrice] = useState(2.05);

  useEffect(() => {
    loadStationInfo();
    // Start dispensing automatically when screen loads
    startPulseAnimation();
    const timer = setTimeout(() => {
      simulateDispensing();
    }, 500); // Small delay to ensure station data is loaded
    
    return () => {
      clearTimeout(timer);
      // Cleanup dispensing timer on unmount
      if (dispensingTimerRef.current) {
        clearInterval(dispensingTimerRef.current);
      }
    };
  }, []);

  const loadStationInfo = async () => {
    try {
      const data = await getStationById(stationId);
      setStation(data);
      
      // Get unit price for selected product
      const pricing = data.pricing?.find(p => p.product === product);
      if (pricing) {
        setUnitPrice(pricing.unitPrice);
      }
    } catch (err: any) {
      console.error('Failed to load station:', err);
    } finally {
      setLoading(false);
    }
  };

  const startPulseAnimation = () => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, {
          toValue: 1.15,
          duration: 800,
          useNativeDriver: true,
        }),
        Animated.timing(pulseAnim, {
          toValue: 1,
          duration: 800,
          useNativeDriver: true,
        }),
      ])
    ).start();
  };

  const simulateDispensing = () => {
    const targetVolume = presetType === 'VOLUME' ? presetValue : presetValue / unitPrice;
    const dispensingRate = 0.08; // 0.08 liters per interval (realistic pump rate)
    const interval = 100; // Update every 100ms

    dispensingTimerRef.current = setInterval(() => {
      setVolumeDispensed((prev) => {
        const newVolume = prev + dispensingRate;
        
        // Check if target reached
        if (newVolume >= targetVolume) {
          if (dispensingTimerRef.current) {
            clearInterval(dispensingTimerRef.current);
          }
          setVolumeDispensed(targetVolume);
          setAmountCharged(targetVolume * unitPrice);
          handleDispenseComplete();
          return targetVolume;
        }
        
        return newVolume;
      });

      setAmountCharged((prev) => prev + (dispensingRate * unitPrice));
      setElapsedTime((prev) => prev + interval);
    }, interval);
  };

  const stopDispensing = () => {
    if (dispensingTimerRef.current) {
      clearInterval(dispensingTimerRef.current);
      dispensingTimerRef.current = null;
    }
  };

  const handleStartDispensing = () => {
    Alert.alert(
      'Start Dispensing',
      'Are you ready to start dispensing fuel? Make sure the nozzle is properly inserted.',
      [
        { text: 'Not Yet', style: 'cancel' },
        {
          text: 'Start',
          onPress: () => {
            setStatus('IN_PROGRESS');
          },
        },
      ]
    );
  };

  const handleStopDispensing = () => {
    Alert.alert(
      'Stop Pumping',
      'Are you sure you want to stop pumping? You will be charged for the fuel already dispensed.',
      [
        { text: 'Continue Pumping', style: 'cancel' },
        {
          text: 'Stop',
          style: 'destructive',
          onPress: () => {
            stopDispensing();
            setStatus('STOPPED');
            setTimeout(() => handleDispenseComplete('USER_STOPPED'), 300);
          },
        },
      ]
    );
  };

  const handleEmergencyStop = () => {
    Alert.alert(
      '⚠️ EMERGENCY STOP',
      'This will immediately stop all dispensing. Use only in case of emergency!',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'EMERGENCY STOP',
          style: 'destructive',
          onPress: () => {
            stopDispensing();
            setStatus('EMERGENCY_STOPPED');
            setTimeout(() => handleDispenseComplete('EMERGENCY_STOP'), 300);
          },
        },
      ]
    );
  };

  const handleDispenseComplete = (reason: string = 'TARGET_REACHED') => {
    stopDispensing(); // Ensure timer is cleared
    setStatus('COMPLETED');
    
    // Store transaction securely
    const transactionId = transactionStore.storeTransaction({
      stationName: station?.name || 'Station',
      product: product,
      nozzle: nozzle,
      volumeDispensed: parseFloat(volumeDispensed.toFixed(2)),
      amountCharged: parseFloat(amountCharged.toFixed(2)),
      unitPrice: parseFloat(unitPrice.toFixed(2)),
      currency: getSelectedPrice()?.currency || 'MYR',
      holdAmount: holdAmount,
      elapsedTime: formatTime(elapsedTime),
      stopReason: reason,
    });
    
    // Navigate with only the transaction ID
    setTimeout(() => {
      router.push({
        pathname: './refueling-complete',
        params: {
          transactionId,
        },
      });
    }, 500);
  };

  const getSelectedPrice = () => {
    if (!station?.pricing || !product) return null;
    return station.pricing.find(p => p.product === product);
  };

  const calculateProgress = () => {
    const targetVolume = presetType === 'VOLUME' ? presetValue : presetValue / unitPrice;
    return Math.min((volumeDispensed / targetVolume) * 100, 100);
  };

  const formatTime = (milliseconds: number) => {
    const seconds = Math.floor(milliseconds / 1000);
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const getHoldRemaining = () => {
    const holdAmountValue = parseFloat(holdAmount.split(' ')[1] || '0');
    const remaining = holdAmountValue - amountCharged;
    return remaining > 0 ? remaining : 0;
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Initializing...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      {/* Status Header */}
      <View style={styles.statusHeader}>
        {status === 'IN_PROGRESS' && (
          <Animated.View style={{ transform: [{ scale: pulseAnim }] }}>
            <View style={styles.statusIconContainer}>
              <Text style={styles.statusIcon}>⛽</Text>
            </View>
          </Animated.View>
        )}
        {(status === 'COMPLETED' || status === 'STOPPED') && (
          <View style={[styles.statusIconContainer, styles.statusIconComplete]}>
            <Text style={styles.statusIcon}>✓</Text>
          </View>
        )}
        {status === 'EMERGENCY_STOPPED' && (
          <View style={[styles.statusIconContainer, styles.statusIconEmergency]}>
            <Text style={styles.statusIcon}>⚠️</Text>
          </View>
        )}
        
        <Text style={styles.statusTitle}>
          {status === 'IN_PROGRESS' && 'Pumping Fuel...'}
          {status === 'COMPLETED' && 'Complete'}
          {status === 'STOPPED' && 'Stopped'}
          {status === 'EMERGENCY_STOPPED' && 'Emergency Stop'}
        </Text>
        <Text style={styles.statusSubtitle}>
          Nozzle {nozzle} • {product}
        </Text>
        {status === 'IN_PROGRESS' && (
          <Text style={styles.instructionText}>
            Use the nozzle to pump fuel into your vehicle
          </Text>
        )}
      </View>

      {/* Main Display Card */}
      <View style={styles.displayCard}>
        <View style={styles.displayRow}>
          <View style={styles.displayItem}>
            <Text style={styles.displayLabel}>Volume Dispensed</Text>
            <Text style={styles.displayValue}>{volumeDispensed.toFixed(2)} L</Text>
          </View>
          <View style={styles.displayDivider} />
          <View style={styles.displayItem}>
            <Text style={styles.displayLabel}>Total Cost</Text>
            <Text style={styles.displayValueCost}>
              {getSelectedPrice()?.currency} {amountCharged.toFixed(2)}
            </Text>
          </View>
        </View>

        <View style={styles.displayRowSmall}>
          <Text style={styles.displaySmallLabel}>Elapsed Time</Text>
          <Text style={styles.displaySmallValue}>{formatTime(elapsedTime)}</Text>
        </View>

        <View style={styles.displayRowSmall}>
          <Text style={styles.displaySmallLabel}>Unit Price</Text>
          <Text style={styles.displaySmallValue}>
            {getSelectedPrice()?.currency} {unitPrice.toFixed(2)}/L
          </Text>
        </View>
      </View>

      {/* Progress Section */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>Progress</Text>
        
        <View style={styles.progressContainer}>
          <View style={styles.progressBar}>
            <View style={[styles.progressFill, { width: `${calculateProgress()}%` }]} />
          </View>
          <Text style={styles.progressPercentage}>{calculateProgress().toFixed(0)}%</Text>
        </View>

        <View style={styles.targetInfo}>
          <Text style={styles.targetLabel}>Target:</Text>
          <Text style={styles.targetValue}>
            {presetType === 'VOLUME' 
              ? `${presetValue.toFixed(2)} L` 
              : `${getSelectedPrice()?.currency} ${presetValue.toFixed(2)}`}
          </Text>
        </View>
      </View>

      {/* Wallet Hold Info */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>💳 Wallet Hold</Text>
        
        <View style={styles.holdRow}>
          <Text style={styles.holdLabel}>Hold Amount:</Text>
          <Text style={styles.holdValue}>{holdAmount}</Text>
        </View>
        
        <View style={styles.holdRow}>
          <Text style={styles.holdLabel}>Charged So Far:</Text>
          <Text style={styles.holdValueCharged}>
            {getSelectedPrice()?.currency} {amountCharged.toFixed(2)}
          </Text>
        </View>
        
        <View style={styles.holdRow}>
          <Text style={styles.holdLabel}>Remaining Hold:</Text>
          <Text style={styles.holdValueRemaining}>
            {getSelectedPrice()?.currency} {getHoldRemaining().toFixed(2)}
          </Text>
        </View>

        <Text style={styles.holdNote}>
          ⓘ Unused hold amount will be released after transaction
        </Text>
      </View>

      {/* Control Buttons */}
      <View style={styles.buttonContainer}>
        {status === 'IN_PROGRESS' && (
          <>
            <TouchableOpacity
              style={[globalStyles.primaryButton, styles.stopButton]}
              onPress={handleStopDispensing}
            >
              <Text style={globalStyles.primaryButtonText}>⏹ Stop Pumping</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[globalStyles.secondaryButton, styles.emergencyButton]}
              onPress={handleEmergencyStop}
            >
              <Text style={styles.emergencyButtonText}>🚨 EMERGENCY STOP</Text>
            </TouchableOpacity>
          </>
        )}

        {(status === 'COMPLETED' || status === 'STOPPED' || status === 'EMERGENCY_STOPPED') && (
          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={() => router.push('./home')}
          >
            <Text style={globalStyles.primaryButtonText}>Done</Text>
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
  statusHeader: {
    alignItems: 'center',
    marginBottom: 24,
  },
  statusIconContainer: {
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
  statusIconIdle: {
    backgroundColor: '#6B7280',
  },
  statusIconComplete: {
    backgroundColor: '#10B981',
  },
  statusIconEmergency: {
    backgroundColor: '#EF4444',
    shadowColor: '#EF4444',
  },
  statusIcon: {
    fontSize: 48,
    color: '#fff',
  },
  statusTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 8,
  },
  statusSubtitle: {
    fontSize: 16,
    color: '#6B7280',
  },
  instructionText: {
    fontSize: 14,
    color: '#10B981',
    marginTop: 8,
    fontStyle: 'italic',
  },
  displayCard: {
    backgroundColor: '#111827',
    borderRadius: 16,
    padding: 24,
    marginBottom: 16,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 12,
    elevation: 5,
  },
  displayRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  displayItem: {
    flex: 1,
    alignItems: 'center',
  },
  displayDivider: {
    width: 1,
    height: 60,
    backgroundColor: '#374151',
    marginHorizontal: 16,
  },
  displayLabel: {
    fontSize: 13,
    color: '#9CA3AF',
    marginBottom: 8,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  displayValue: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#10B981',
  },
  displayValueCost: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#3B82F6',
  },
  displayRowSmall: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: '#374151',
  },
  displaySmallLabel: {
    fontSize: 14,
    color: '#9CA3AF',
  },
  displaySmallValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#F3F4F6',
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
  progressContainer: {
    marginBottom: 16,
  },
  progressBar: {
    height: 24,
    backgroundColor: '#E5E7EB',
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 8,
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#10B981',
    borderRadius: 12,
  },
  progressPercentage: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#10B981',
    textAlign: 'center',
  },
  targetInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingTop: 12,
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
  },
  targetLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  targetValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  holdRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  holdLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  holdValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  holdValueCharged: {
    fontSize: 15,
    fontWeight: '600',
    color: '#3B82F6',
  },
  holdValueRemaining: {
    fontSize: 15,
    fontWeight: '600',
    color: '#10B981',
  },
  holdNote: {
    fontSize: 13,
    color: '#6B7280',
    textAlign: 'center',
    marginTop: 8,
    fontStyle: 'italic',
  },
  buttonContainer: {
    marginTop: 8,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  stopButton: {
    backgroundColor: '#F59E0B',
    marginBottom: 12,
  },
  emergencyButton: {
    backgroundColor: '#EF4444',
    borderWidth: 3,
    borderColor: '#DC2626',
  },
  emergencyButtonText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#fff',
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#6B7280',
  },
});
