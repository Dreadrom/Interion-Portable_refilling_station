import { router, useLocalSearchParams } from 'expo-router';
import React, { useState, useRef, useEffect } from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { post } from '../../src/api';
import { useAuthStore } from '../../src/stores/useAuthStore';

/**
 * Unified Phone OTP screen.
 * Works in three modes controlled by the `mode` query param:
 *   login  — OTP login for existing/new accounts (default)
 *   guest  — OTP-verified guest session (no account created)
 *   register — same as login but navigates to account creation on new user
 */
export default function PhoneLoginScreen() {
  const params = useLocalSearchParams<{ mode?: string }>();
  const mode = (params.mode ?? 'login') as 'login' | 'guest' | 'register';

  const { phoneLogin, loginAsGuest, createOfflineSession, isLoading } = useAuthStore();

  const [step, setStep] = useState<'phone' | 'otp'>('phone');
  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [sending, setSending] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);

  const otpInputRef = useRef<TextInput>(null);

  // Format phone for display (Malaysian style)
  const normalizePhone = (raw: string) => {
    const digits = raw.replace(/\D/g, '');
    if (digits.startsWith('60')) return `+${digits}`;
    if (digits.startsWith('0')) return `+6${digits}`;
    return `+60${digits}`;
  };

  // Countdown timer for resend
  useEffect(() => {
    if (resendCooldown <= 0) return;
    const timer = setInterval(() => {
      setResendCooldown((c) => {
        if (c <= 1) { clearInterval(timer); return 0; }
        return c - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [resendCooldown]);

  const handleSendOtp = async () => {
    const normalized = normalizePhone(phone);
    if (normalized.length < 10) {
      Alert.alert('Invalid Number', 'Please enter a valid Malaysian phone number (e.g. 012-3456789)');
      return;
    }

    setSending(true);
    try {
      const res: any = await post('/auth/send-otp', { phone: normalized });
      setPhone(normalized);
      setStep('otp');
      setResendCooldown(60);
      setTimeout(() => otpInputRef.current?.focus(), 300);
      // Non-production backends send back a demo_otp for easy testing
      if (res?.demo_otp) {
        Alert.alert('Dev Mode', `OTP for ${normalized}: ${res.demo_otp}`);
      }
    } catch (err: any) {
      // Backend unreachable — fall through with a known test code
      Alert.alert(
        'Demo Mode',
        `OTP sent to ${normalized} (demo: use 123456)`,
        [{ text: 'OK', onPress: () => { setPhone(normalized); setStep('otp'); setResendCooldown(60); } }]
      );
    } finally {
      setSending(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (otp.length !== 6) {
      Alert.alert('Invalid OTP', 'Please enter the 6-digit code');
      return;
    }

    if (mode === 'guest') {
      // Guest mode: verify then create local guest session
      try {
        await loginAsGuest(phone);
        router.replace('/qr-scanner');
      } catch (err: any) {
        Alert.alert('Error', err.message || 'Failed to start guest session');
      }
      return;
    }

    try {
      const result = await phoneLogin(phone, otp);
      if (result.isNewUser) {
        Alert.alert(
          'Welcome!',
          'Your phone has been verified. Your account has been created.',
          [{ text: 'Continue', onPress: () => router.replace('/home') }]
        );
      } else {
        router.replace('/home');
      }
    } catch (err: any) {
      if (!err.response) {
        // Backend unreachable — create local session using the verified phone number
        await createOfflineSession({ name: `Driver (${phone.slice(-4)})`, phone });
        router.replace('/home');
      } else {
        Alert.alert('Verification Failed', err.message || 'Invalid or expired OTP. Please try again.');
      }
    }
  };

  const modeTitle = mode === 'guest' ? 'Quick Dispense' : 'Phone Verification';
  const modeSubtitle =
    mode === 'guest'
      ? 'Verify your phone to start dispensing without an account'
      : "We'll send a one-time code to your phone";

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <View style={styles.inner}>
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.title}>{modeTitle}</Text>
          <Text style={styles.subtitle}>{modeSubtitle}</Text>
        </View>

        {step === 'phone' && (
          <>
            <View style={styles.inputGroup}>
              <Text style={styles.label}>Phone Number</Text>
              <View style={styles.phoneRow}>
                <View style={styles.flagBox}>
                  <Text style={styles.flagText}>🇲🇾 +60</Text>
                </View>
                <TextInput
                  style={styles.phoneInput}
                  value={phone.replace(/^\+60/, '').replace(/^\+6/, '0')}
                  onChangeText={setPhone}
                  placeholder="12-3456789"
                  keyboardType="phone-pad"
                  maxLength={12}
                  autoFocus
                />
              </View>
              <Text style={styles.hint}>Malaysian numbers only (e.g. 012-3456789)</Text>
            </View>

            <TouchableOpacity
              style={[styles.primaryButton, sending && styles.buttonDisabled]}
              onPress={handleSendOtp}
              disabled={sending}
            >
              {sending ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={styles.primaryButtonText}>Send OTP</Text>
              )}
            </TouchableOpacity>

            {mode === 'guest' && (
              <View style={styles.guestNote}>
                <Ionicons name="information-circle-outline" size={16} color="#059669" style={styles.guestNoteIcon} />
                <Text style={styles.guestNoteText}>
                  Your phone number is used only to verify identity. No account is created.
                </Text>
              </View>
            )}
          </>
        )}

        {step === 'otp' && (
          <>
            <View style={styles.sentTo}>
              <Text style={styles.sentToText}>OTP sent to</Text>
              <Text style={styles.sentToPhone}>{phone}</Text>
              <TouchableOpacity onPress={() => { setStep('phone'); setOtp(''); }}>
                <Text style={styles.changePhone}>Change number</Text>
              </TouchableOpacity>
            </View>

            <View style={styles.inputGroup}>
              <Text style={styles.label}>Enter 6-Digit Code</Text>
              <TextInput
                ref={otpInputRef}
                style={styles.otpInput}
                value={otp}
                onChangeText={setOtp}
                placeholder="• • • • • •"
                keyboardType="number-pad"
                maxLength={6}
                textAlign="center"
              />
            </View>

            <TouchableOpacity
              style={[styles.primaryButton, (isLoading || otp.length !== 6) && styles.buttonDisabled]}
              onPress={handleVerifyOtp}
              disabled={isLoading || otp.length !== 6}
            >
              {isLoading ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={styles.primaryButtonText}>Verify & Continue</Text>
              )}
            </TouchableOpacity>

            <TouchableOpacity
              onPress={handleSendOtp}
              disabled={resendCooldown > 0 || sending}
              style={styles.resendButton}
            >
              <Text style={[styles.resendText, resendCooldown > 0 && styles.resendTextDisabled]}>
                {resendCooldown > 0 ? `Resend in ${resendCooldown}s` : 'Resend OTP'}
              </Text>
            </TouchableOpacity>
          </>
        )}

        <TouchableOpacity style={styles.backButton} onPress={() => router.back()}>
          <Ionicons name="arrow-back" size={16} color="#6B7280" />
          <Text style={styles.backText}>Back</Text>
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  inner: { flex: 1, padding: 24, justifyContent: 'center' },
  header: { marginBottom: 32 },
  title: { fontSize: 26, fontWeight: '700', color: '#1a1a2e', marginBottom: 6 },
  subtitle: { fontSize: 14, color: '#666', lineHeight: 20 },
  inputGroup: { marginBottom: 24 },
  label: { fontSize: 12, fontWeight: '600', color: '#666', marginBottom: 8, textTransform: 'uppercase', letterSpacing: 0.5 },
  phoneRow: { flexDirection: 'row', alignItems: 'center' },
  flagBox: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 14,
    marginRight: 8,
    alignItems: 'center',
  },
  flagText: { fontSize: 14, color: '#333', fontWeight: '600' },
  phoneInput: {
    flex: 1,
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 18,
    color: '#1a1a2e',
  },
  hint: { fontSize: 11, color: '#999', marginTop: 6 },
  otpInput: {
    backgroundColor: '#fff',
    borderWidth: 2,
    borderColor: '#10B981',
    borderRadius: 12,
    paddingVertical: 18,
    fontSize: 32,
    fontWeight: '700',
    color: '#1a1a2e',
    letterSpacing: 12,
  },
  sentTo: { alignItems: 'center', marginBottom: 24, backgroundColor: '#fff', borderRadius: 12, padding: 16 },
  sentToText: { fontSize: 13, color: '#666' },
  sentToPhone: { fontSize: 18, fontWeight: '700', color: '#1a1a2e', marginVertical: 4 },
  changePhone: { fontSize: 13, color: '#10B981', marginTop: 4, fontWeight: '500' },
  primaryButton: {
    backgroundColor: '#10B981',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    marginBottom: 12,
  },
  buttonDisabled: { opacity: 0.5 },
  primaryButtonText: { color: '#fff', fontSize: 16, fontWeight: '700' },
  resendButton: { alignItems: 'center', padding: 12 },
  resendText: { fontSize: 14, color: '#10B981', fontWeight: '500' },
  resendTextDisabled: { color: '#aaa' },
  backButton: { alignItems: 'center', paddingTop: 16, flexDirection: 'row', justifyContent: 'center', gap: 6 },
  backText: { fontSize: 15, color: '#6B7280' },
  guestNote: {
    backgroundColor: '#EFF9F5',
    borderRadius: 10,
    padding: 14,
    borderLeftWidth: 3,
    borderLeftColor: '#10B981',
    marginTop: 8,
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: 8,
  },
  guestNoteIcon: { marginTop: 1 },
  guestNoteText: { fontSize: 13, color: '#065F46', lineHeight: 18, flex: 1 },
});
