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
  Animated,
} from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { getStationById } from '../../src/api/stations';
import { StationDetail, ProductType } from '../../src/types';

export default function PumpUnlockedScreen() {
  const params = useLocalSearchParams();
  const [station, setStation] = useState<StationDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [timeRemaining, setTimeRemaining] = useState(300); // 5 minutes in seconds
  const [pulseAnim] = useState(new Animated.Value(1));

  const stationId = params.stationId as string;
  const product = params.product as ProductType;
  const presetType = params.presetType as string;
  const presetValue = params.presetValue as string;
  const holdAmount = params.holdAmount as string;
  const nozzle = params.nozzle || '1'; // Default nozzle 1

  useEffect(() => {
    loadStationInfo();
    startPulseAnimation();
    startTimer();
  }, []);

  const loadStationInfo = async () => {
    try {
      const data = await getStationById(stationId);
      setStation(data);
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
          toValue: 1.1,
          duration: 1000,
          useNativeDriver: true,
        }),
        Animated.timing(pulseAnim, {
          toValue: 1,
          duration: 1000,
          useNativeDriver: true,
        }),
      ])
    ).start();
  };

  const startTimer = () => {
    const interval = setInterval(() => {
      setTimeRemaining((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          handleTimeout();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  };

  const handleTimeout = () => {
    Alert.alert(
      'Reservation Expired',
      'Your pump reservation has expired. Please start a new refill request.',
      [
        {
          text: 'OK',
          onPress: () => router.push('./qr-scanner'),
        },
      ]
    );
  };

  const handleProceedToDispense = () => {
    // Navigate to live dispensing screen
    router.push({
      pathname: './live-dispensing',
      params: {
        stationId: stationId,
        product: product,
        presetType: presetType,
        presetValue: presetValue,
        holdAmount: holdAmount,
        nozzle: nozzle,
      },
    });
  };

  const handleCancel = () => {
    Alert.alert(
      'Cancel Reservation',
      'Are you sure you want to cancel this pump reservation? Your wallet hold will be released.',
      [
        { text: 'No', style: 'cancel' },
        {
          text: 'Yes, Cancel',
          style: 'destructive',
          onPress: async () => {
            // Simulate releasing the wallet hold
            try {
              // In real implementation, call API to release hold
              await new Promise(resolve => setTimeout(resolve, 1000));
              
              Alert.alert(
                'Reservation Cancelled', 
                `Your wallet hold of ${holdAmount} has been released successfully.`, 
                [
                  { 
                    text: 'OK', 
                    onPress: () => router.replace('./qr-scanner') 
                  }
                ]
              );
            } catch (err: any) {
              Alert.alert('Error', 'Failed to release hold. Please contact support.');
            }
          },
        },
      ]
    );
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Loading...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      {/* Success Header */}
      <View style={styles.successHeader}>
        <Animated.View style={[styles.successIconContainer, { transform: [{ scale: pulseAnim }] }]}>
          <Text style={styles.successIcon}>✓</Text>
        </Animated.View>
        <Text style={styles.successTitle}>Pump Unlocked!</Text>
        <Text style={styles.successSubtitle}>Your pump is ready and waiting</Text>
      </View>

      {/* Timer */}
      <View style={[styles.timerCard, timeRemaining < 60 && styles.timerCardWarning]}>
        <Text style={styles.timerLabel}>Time Remaining</Text>
        <Text style={[styles.timerValue, timeRemaining < 60 && styles.timerValueWarning]}>
          {formatTime(timeRemaining)}
        </Text>
        <Text style={styles.timerSubtext}>
          {timeRemaining < 60 ? 'Hurry! Reservation expiring soon' : 'Reservation will expire after this time'}
        </Text>
      </View>

      {/* Station & Pump Info */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>📍 Station Information</Text>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Station:</Text>
          <Text style={styles.infoValue}>{station?.name}</Text>
        </View>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Address:</Text>
          <Text style={styles.infoValueSmall}>{station?.location?.address}</Text>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>⛽ Pump Assignment</Text>
        <View style={styles.pumpContainer}>
          <Text style={styles.pumpLabel}>Nozzle Number</Text>
          <Text style={styles.pumpNumber}>{nozzle}</Text>
          <Text style={styles.pumpProduct}>{product}</Text>
        </View>
      </View>

      {/* Refill Details */}
      <View style={styles.card}>
        <Text style={styles.cardTitle}>💧 Refill Details</Text>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Product:</Text>
          <Text style={styles.infoValue}>{product}</Text>
        </View>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Preset:</Text>
          <Text style={styles.infoValue}>
            {presetType === 'VOLUME' ? `${presetValue} L` : `${holdAmount?.split(' ')[0] || 'MYR'} ${presetValue}`}
          </Text>
        </View>
        <View style={styles.infoRow}>
          <Text style={styles.infoLabel}>Hold Amount:</Text>
          <Text style={styles.infoValue}>{holdAmount}</Text>
        </View>
      </View>

      {/* Instructions */}
      <View style={styles.instructionsCard}>
        <Text style={styles.instructionsTitle}>📋 Instructions</Text>
        <View style={styles.instructionsList}>
          <View style={styles.instructionItem}>
            <Text style={styles.stepNumber}>1</Text>
            <Text style={styles.stepText}>Proceed to the station and locate <Text style={styles.stepBold}>Nozzle {nozzle}</Text></Text>
          </View>
          <View style={styles.instructionItem}>
            <Text style={styles.stepNumber}>2</Text>
            <Text style={styles.stepText}>Remove the nozzle from the holster</Text>
          </View>
          <View style={styles.instructionItem}>
            <Text style={styles.stepNumber}>3</Text>
            <Text style={styles.stepText}>Insert the nozzle into your vehicle's fuel tank</Text>
          </View>
          <View style={styles.instructionItem}>
            <Text style={styles.stepNumber}>4</Text>
            <Text style={styles.stepText}>Tap "Start Dispensing" below to begin refilling</Text>
          </View>
          <View style={styles.instructionItem}>
            <Text style={styles.stepNumber}>5</Text>
            <Text style={styles.stepText}>The pump will automatically stop when complete</Text>
          </View>
        </View>
      </View>

      {/* Warning */}
      <View style={styles.warningCard}>
        <Text style={styles.warningIcon}>⚠️</Text>
        <Text style={styles.warningText}>
          Do not share your nozzle number with others. Only you have access to this pump during your reserved time.
        </Text>
      </View>

      {/* Action Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={globalStyles.primaryButton}
          onPress={handleProceedToDispense}
        >
          <Text style={globalStyles.primaryButtonText}>Start Dispensing</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={handleCancel}
        >
          <Text style={globalStyles.secondaryButtonText}>Cancel Reservation</Text>
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
  timerCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
    marginBottom: 16,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#10B981',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  timerCardWarning: {
    borderColor: '#F59E0B',
    backgroundColor: '#FEF3C7',
  },
  timerLabel: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 8,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  timerValue: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#10B981',
    marginBottom: 8,
  },
  timerValueWarning: {
    color: '#F59E0B',
  },
  timerSubtext: {
    fontSize: 13,
    color: '#6B7280',
    textAlign: 'center',
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
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
    alignItems: 'flex-start',
  },
  infoLabel: {
    fontSize: 15,
    color: '#6B7280',
    flex: 1,
  },
  infoValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
    flex: 2,
    textAlign: 'right',
  },
  infoValueSmall: {
    fontSize: 13,
    fontWeight: '500',
    color: '#111827',
    flex: 2,
    textAlign: 'right',
  },
  pumpContainer: {
    alignItems: 'center',
    paddingVertical: 16,
    backgroundColor: '#F0FDF4',
    borderRadius: 12,
  },
  pumpLabel: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 8,
  },
  pumpNumber: {
    fontSize: 64,
    fontWeight: 'bold',
    color: '#10B981',
    marginBottom: 8,
  },
  pumpProduct: {
    fontSize: 18,
    fontWeight: '600',
    color: '#059669',
  },
  instructionsCard: {
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
  instructionsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#1E40AF',
    marginBottom: 16,
  },
  instructionsList: {
    gap: 16,
  },
  instructionItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  stepNumber: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: '#3B82F6',
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
    textAlign: 'center',
    lineHeight: 28,
    marginRight: 12,
  },
  stepText: {
    flex: 1,
    fontSize: 15,
    color: '#1E3A8A',
    lineHeight: 22,
  },
  stepBold: {
    fontWeight: 'bold',
    color: '#1E40AF',
  },
  warningCard: {
    backgroundColor: '#FEF3C7',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
    flexDirection: 'row',
    alignItems: 'flex-start',
    borderWidth: 1,
    borderColor: '#F59E0B',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  warningIcon: {
    fontSize: 20,
    marginRight: 12,
  },
  warningText: {
    flex: 1,
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
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#6B7280',
  },
});
