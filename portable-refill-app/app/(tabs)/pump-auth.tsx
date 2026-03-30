import { router, useLocalSearchParams } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useState, useRef } from 'react';
import {
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { globalStyles } from '../../src/styles/globalStyles';

export default function PumpAuthScreen() {
  const insets = useSafeAreaInsets();
  const params = useLocalSearchParams();

  const stationId  = params.stationId as string;
  const product    = params.product as string;
  const presetType = params.presetType as string;
  const presetValue = params.presetValue as string;
  const holdAmount  = params.holdAmount as string;
  const holdAmountRaw = params.holdAmountRaw as string;
  const nozzle     = params.nozzle as string;
  const stationName = params.stationName as string;

  // Demo mode: any valid 4-digit code is accepted.
  // In production, the hardware issues the code and the backend verifies it.
  const [entered, setEntered] = useState('');
  const [hasError, setHasError] = useState(false);
  const inputRef = useRef<TextInput>(null);

  const verify = (code: string) => {
    if (code.length === 4) {
      router.push({
        pathname: './live-dispensing',
        params: { stationId, product, presetType, presetValue, holdAmount, holdAmountRaw, nozzle },
      });
    }
  };

  const handleChange = (text: string) => {
    const digits = text.replace(/\D/g, '').slice(0, 4);
    setEntered(digits);
    setHasError(false);
    if (digits.length === 4) verify(digits);
  };

  return (
    <View style={[styles.screen, { paddingTop: insets.top }]}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={22} color="#111827" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Pump Verification</Text>
        <View style={{ width: 38 }} />
      </View>

      <View style={styles.content}>
        {/* Station & pump info */}
        <View style={styles.stationCard}>
          <View style={styles.stationIconBox}>
            <Ionicons name="location-outline" size={20} color="#10B981" />
          </View>
          <View style={{ flex: 1 }}>
            <Text style={styles.stationName}>{stationName || 'Station'}</Text>
            <Text style={styles.nozzleLabel}>Pump #{nozzle}</Text>
          </View>
          <View style={styles.pumpBadge}>
            <Ionicons name="checkmark-circle" size={14} color="#10B981" />
            <Text style={styles.pumpBadgeText}>Reserved</Text>
          </View>
        </View>

        {/* Hardware prompt */}
        <View style={styles.promptCard}>
          <Ionicons name="hardware-chip-outline" size={28} color="#10B981" style={{ marginBottom: 12 }} />
          <Text style={styles.promptTitle}>Check the Pump Display</Text>
          <Text style={styles.instructions}>
            Look at the screen on pump #{nozzle} and enter the 4-digit code it shows to confirm you are physically present.
          </Text>
        </View>

        {/* PIN digit boxes */}
        <Pressable style={styles.pinRow} onPress={() => inputRef.current?.focus()}>
          {[0, 1, 2, 3].map(i => (
            <View
              key={i}
              style={[
                styles.pinBox,
                entered.length === i && styles.pinBoxActive,
                hasError && styles.pinBoxError,
              ]}
            >
              <Text style={[styles.pinDigit, hasError && styles.pinDigitError]}>
                {entered[i] ?? ''}
              </Text>
            </View>
          ))}
        </Pressable>

        {/* Hidden input that captures keyboard */}
        <TextInput
          ref={inputRef}
          value={entered}
          onChangeText={handleChange}
          keyboardType="number-pad"
          maxLength={4}
          style={styles.hiddenInput}
          autoFocus
          caretHidden
        />

        {hasError && (
          <Text style={styles.errorText}>Incorrect code — please check and try again</Text>
        )}

        <TouchableOpacity
          style={[
            globalStyles.primaryButton,
            entered.length < 4 && styles.btnDisabled,
            { marginTop: 28 },
          ]}
          onPress={() => verify(entered)}
          disabled={entered.length < 4}
        >
          <Text style={globalStyles.primaryButtonText}>Verify &amp; Start Dispensing</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={() => router.back()}
        >
          <Text style={globalStyles.secondaryButtonText}>Go Back</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#F8FAFC' },

  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#F3F4F6',
    backgroundColor: '#fff',
  },
  backBtn: {
    width: 38,
    height: 38,
    borderRadius: 10,
    backgroundColor: '#F3F4F6',
    justifyContent: 'center',
    alignItems: 'center',
  },
  headerTitle: { fontSize: 17, fontWeight: '700', color: '#111827' },

  content: { flex: 1, paddingHorizontal: 24, paddingTop: 28 },

  stationCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 16,
    marginBottom: 24,
    gap: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06,
    shadowRadius: 6,
    elevation: 2,
  },
  stationIconBox: {
    width: 40,
    height: 40,
    borderRadius: 10,
    backgroundColor: '#ECFDF5',
    justifyContent: 'center',
    alignItems: 'center',
  },
  stationName: { fontSize: 15, fontWeight: '700', color: '#111827' },
  nozzleLabel: { fontSize: 13, color: '#6B7280', marginTop: 2 },
  pumpBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    backgroundColor: '#ECFDF5',
    borderRadius: 8,
    paddingHorizontal: 8,
    paddingVertical: 4,
  },
  pumpBadgeText: { fontSize: 11, fontWeight: '600', color: '#059669' },

  promptCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 24,
    marginBottom: 24,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06,
    shadowRadius: 6,
    elevation: 2,
  },
  promptTitle: {
    fontSize: 17,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 8,
  },

  instructions: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    lineHeight: 20,
  },

  // PIN boxes
  pinRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 14,
    marginBottom: 8,
  },
  pinBox: {
    width: 60,
    height: 70,
    borderRadius: 14,
    borderWidth: 2,
    borderColor: '#D1D5DB',
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 1,
  },
  pinBoxActive: {
    borderColor: '#10B981',
    shadowColor: '#10B981',
    shadowOpacity: 0.25,
  },
  pinBoxError: {
    borderColor: '#EF4444',
    backgroundColor: '#FEF2F2',
  },
  pinDigit: {
    fontSize: 28,
    fontWeight: '700',
    color: '#111827',
    fontVariant: ['tabular-nums'],
  },
  pinDigitError: { color: '#EF4444' },

  hiddenInput: {
    position: 'absolute',
    opacity: 0,
    width: 0,
    height: 0,
  },

  errorText: {
    textAlign: 'center',
    color: '#EF4444',
    fontSize: 13,
    fontWeight: '500',
    marginBottom: 4,
  },

  btnDisabled: { opacity: 0.45 },
});
