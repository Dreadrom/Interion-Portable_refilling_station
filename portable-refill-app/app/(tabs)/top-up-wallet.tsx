import { router } from 'expo-router';
import React, { useState } from 'react';
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { globalStyles } from '../styles/globalStyles';

const PRESET_AMOUNTS = [10, 20, 50, 100, 200, 500];

export default function TopUpWalletScreen() {
  const [customAmount, setCustomAmount] = useState('');
  const [selectedPreset, setSelectedPreset] = useState<number | null>(null);
  const [processing, setProcessing] = useState(false);

  const currentBalance = 501.90; // This would come from user context/state in production
  const currency = 'MYR';

  const handlePresetSelect = (amount: number) => {
    setSelectedPreset(amount);
    setCustomAmount('');
  };

  const handleCustomAmountChange = (text: string) => {
    setCustomAmount(text);
    setSelectedPreset(null);
  };

  const getSelectedAmount = (): number | null => {
    if (selectedPreset !== null) return selectedPreset;
    const parsed = parseFloat(customAmount);
    return isNaN(parsed) ? null : parsed;
  };

  const handleTopUp = async () => {
    const amount = getSelectedAmount();

    if (!amount) {
      Alert.alert('Invalid Amount', 'Please select or enter an amount to top up.');
      return;
    }

    if (amount < 1) {
      Alert.alert('Invalid Amount', 'Minimum top-up amount is ' + currency + ' 1.00');
      return;
    }

    if (amount > 5000) {
      Alert.alert('Invalid Amount', 'Maximum top-up amount is ' + currency + ' 5,000.00');
      return;
    }

    setProcessing(true);

    try {
      // Simulate payment processing
      await new Promise(resolve => setTimeout(resolve, 2000));

      const newBalance = currentBalance + amount;

      Alert.alert(
        'Top-Up Successful!',
        `${currency} ${amount.toFixed(2)} has been added to your wallet.\n\nNew Balance: ${currency} ${newBalance.toFixed(2)}`,
        [
          {
            text: 'Done',
            onPress: () => router.back(),
          },
        ]
      );
    } catch (error: any) {
      Alert.alert('Top-Up Failed', error.message || 'An error occurred during top-up.');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Top-Up Wallet</Text>
        <Text style={styles.headerSubtitle}>Add funds to your wallet</Text>
      </View>

      {/* Current Balance */}
      <View style={styles.balanceCard}>
        <Text style={styles.balanceLabel}>Current Balance</Text>
        <Text style={styles.balanceAmount}>{currency} {currentBalance.toFixed(2)}</Text>
      </View>

      {/* Preset Amounts */}
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Quick Select</Text>
        <View style={styles.presetsGrid}>
          {PRESET_AMOUNTS.map((amount) => (
            <TouchableOpacity
              key={amount}
              style={[
                styles.presetButton,
                selectedPreset === amount && styles.presetButtonSelected,
              ]}
              onPress={() => handlePresetSelect(amount)}
              disabled={processing}
            >
              <Text
                style={[
                  styles.presetButtonText,
                  selectedPreset === amount && styles.presetButtonTextSelected,
                ]}
              >
                {currency} {amount}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Custom Amount */}
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>Or Enter Custom Amount</Text>
        <View style={styles.inputContainer}>
          <Text style={styles.currencyPrefix}>{currency}</Text>
          <TextInput
            style={styles.input}
            placeholder="0.00"
            keyboardType="decimal-pad"
            value={customAmount}
            onChangeText={handleCustomAmountChange}
            editable={!processing}
          />
        </View>
        <Text style={styles.helperText}>Min: {currency} 1 • Max: {currency} 5,000</Text>
      </View>

      {/* Summary */}
      {getSelectedAmount() !== null && (
        <View style={[styles.card, styles.summaryCard]}>
          <Text style={styles.summaryTitle}>Top-Up Summary</Text>
          
          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Top-Up Amount:</Text>
            <Text style={styles.summaryValue}>{currency} {getSelectedAmount()!.toFixed(2)}</Text>
          </View>

          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabel}>Current Balance:</Text>
            <Text style={styles.summaryValue}>{currency} {currentBalance.toFixed(2)}</Text>
          </View>

          <View style={styles.separator} />

          <View style={styles.summaryRow}>
            <Text style={styles.summaryLabelTotal}>New Balance:</Text>
            <Text style={styles.summaryValueTotal}>
              {currency} {(currentBalance + getSelectedAmount()!).toFixed(2)}
            </Text>
          </View>
        </View>
      )}

      {/* Payment Methods Info */}
      <View style={styles.infoCard}>
        <Text style={styles.infoIcon}>💳</Text>
        <View style={styles.infoTextContainer}>
          <Text style={styles.infoTitle}>Payment Methods</Text>
          <Text style={styles.infoText}>
            • Credit/Debit Card{'\n'}
            • Online Banking{'\n'}
            • Digital Wallets (Touch 'n Go, GrabPay)
          </Text>
        </View>
      </View>

      {/* Action Buttons */}
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={[
            globalStyles.primaryButton,
            (!getSelectedAmount() || processing) && styles.buttonDisabled,
          ]}
          onPress={handleTopUp}
          disabled={!getSelectedAmount() || processing}
        >
          <Text style={globalStyles.primaryButtonText}>
            {processing
              ? 'Processing...'
              : `Top-Up ${getSelectedAmount() ? `${currency} ${getSelectedAmount()!.toFixed(2)}` : ''}`}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 12 }]}
          onPress={() => router.back()}
          disabled={processing}
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
  header: {
    alignItems: 'center',
    marginBottom: 24,
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 8,
  },
  headerSubtitle: {
    fontSize: 16,
    color: '#6B7280',
  },
  balanceCard: {
    backgroundColor: '#10B981',
    borderRadius: 16,
    padding: 24,
    marginBottom: 24,
    alignItems: 'center',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
    shadowColor: '#10B981',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
  },
  balanceLabel: {
    fontSize: 14,
    color: '#D1FAE5',
    marginBottom: 8,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  balanceAmount: {
    fontSize: 36,
    fontWeight: 'bold',
    color: '#fff',
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
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 16,
  },
  presetsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
  },
  presetButton: {
    flex: 1,
    minWidth: '30%',
    backgroundColor: '#F3F4F6',
    borderRadius: 12,
    paddingVertical: 16,
    paddingHorizontal: 12,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: 'transparent',
  },
  presetButtonSelected: {
    backgroundColor: '#ECFDF5',
    borderColor: '#10B981',
  },
  presetButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: '#6B7280',
  },
  presetButtonTextSelected: {
    color: '#10B981',
  },
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#E5E7EB',
    borderRadius: 12,
    paddingHorizontal: 16,
    backgroundColor: '#F9FAFB',
  },
  currencyPrefix: {
    fontSize: 18,
    fontWeight: '600',
    color: '#6B7280',
    marginRight: 8,
  },
  input: {
    flex: 1,
    fontSize: 24,
    fontWeight: '600',
    color: '#111827',
    paddingVertical: 16,
  },
  helperText: {
    fontSize: 12,
    color: '#6B7280',
    marginTop: 8,
  },
  summaryCard: {
    backgroundColor: '#F0FDF4',
    borderWidth: 2,
    borderColor: '#10B981',
  },
  summaryTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6B7280',
    textTransform: 'uppercase',
    marginBottom: 16,
    letterSpacing: 0.5,
  },
  summaryRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 12,
  },
  summaryLabel: {
    fontSize: 15,
    color: '#6B7280',
  },
  summaryValue: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  summaryLabelTotal: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
  },
  summaryValueTotal: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#10B981',
  },
  separator: {
    height: 1,
    backgroundColor: '#10B981',
    marginVertical: 12,
    opacity: 0.3,
  },
  infoCard: {
    backgroundColor: '#EFF6FF',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
    flexDirection: 'row',
    alignItems: 'flex-start',
    borderWidth: 1,
    borderColor: '#3B82F6',
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  infoIcon: {
    fontSize: 24,
    marginRight: 12,
  },
  infoTextContainer: {
    flex: 1,
  },
  infoTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1E40AF',
    marginBottom: 4,
  },
  infoText: {
    fontSize: 13,
    color: '#1E40AF',
    lineHeight: 20,
  },
  buttonContainer: {
    marginTop: 8,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  buttonDisabled: {
    opacity: 0.5,
  },
});
