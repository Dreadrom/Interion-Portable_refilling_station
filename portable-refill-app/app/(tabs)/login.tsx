import { router } from 'expo-router';
import { useState } from 'react';
import { Ionicons } from '@expo/vector-icons';
import {
  Alert,
  Image,
  KeyboardAvoidingView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';
import { useAuthStore } from '../../src/stores/useAuthStore';

type LoginTab = 'phone' | 'email';

export default function LoginScreen() {
  const [activeTab, setActiveTab] = useState<LoginTab>('phone');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, devLogin, createOfflineSession, isLoading } = useAuthStore();

  const TEST_EMAIL = 'tester@bluediesel.com.my';
  const TEST_PASSWORD = 'BlueDiesel@2026';

  const handleEmailLogin = async () => {
    if (!email || !password) {
      Alert.alert('Error', 'Please enter both email and password');
      return;
    }
    // Test account — works without a live backend
    if (email.toLowerCase().trim() === TEST_EMAIL && password === TEST_PASSWORD) {
      await devLogin();
      router.replace('/home');
      return;
    }
    try {
      await login({ email, password });
      router.replace('/home');
    } catch (error: any) {
      // Backend unreachable (network error or timeout) — create local session so the demo flow works
      if (error.code === 'SERVICE_UNAVAILABLE' || error.code === 'GATEWAY_TIMEOUT' || error.code === 'INTERNAL_SERVER_ERROR') {
        await createOfflineSession({ name: email.split('@')[0], email });
        router.replace('/home');
      } else {
        Alert.alert('Login Failed', error.message || 'An error occurred during login');
      }
    }
  };

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior="padding">
    <ScrollView contentContainerStyle={styles.scrollContent} keyboardShouldPersistTaps="handled">
      <Image
        source={require('../images/fuel_station_logo.jpg')}
        style={globalStyles.logo}
        resizeMode="contain"
      />

      <Text style={styles.title}>BlueDiesel</Text>

      {/* Quick Dispense — primary CTA for one-time users */}
      <TouchableOpacity
        style={styles.quickButton}
        onPress={() => router.push({ pathname: './phone-login', params: { mode: 'guest' } })}
      >
        <View style={styles.quickRow}>
          <View style={styles.quickIconBox}>
            <Ionicons name="flash" size={20} color="#10B981" />
          </View>
          <View style={styles.quickTextBlock}>
            <Text style={styles.quickButtonTitle}>Quick Dispense</Text>
            <Text style={styles.quickButtonSubtitle}>No account needed · Verify phone only</Text>
          </View>
          <Ionicons name="arrow-forward" size={20} color="rgba(255,255,255,0.5)" />
        </View>
      </TouchableOpacity>

      <View style={styles.dividerRow}>
        <View style={styles.dividerLine} />
        <Text style={styles.dividerText}>or sign in to your account</Text>
        <View style={styles.dividerLine} />
      </View>

      {/* Tab switcher */}
      <View style={styles.tabRow}>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'phone' && styles.tabActive]}
          onPress={() => setActiveTab('phone')}
        >
          <View style={styles.tabContent}>
            <Ionicons name="phone-portrait-outline" size={15} color={activeTab === 'phone' ? '#111827' : '#9CA3AF'} />
            <Text style={[styles.tabText, activeTab === 'phone' && styles.tabTextActive]}>Phone OTP</Text>
          </View>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.tab, activeTab === 'email' && styles.tabActive]}
          onPress={() => setActiveTab('email')}
        >
          <View style={styles.tabContent}>
            <Ionicons name="mail-outline" size={15} color={activeTab === 'email' ? '#111827' : '#9CA3AF'} />
            <Text style={[styles.tabText, activeTab === 'email' && styles.tabTextActive]}>Email</Text>
          </View>
        </TouchableOpacity>
      </View>

      {activeTab === 'phone' && (
        <>
          <TouchableOpacity
            style={styles.phoneCta}
            onPress={() => router.push({ pathname: './phone-login', params: { mode: 'login' } })}
          >
            <Text style={styles.phoneCtaText}>Continue with Phone Number</Text>
            <Ionicons name="arrow-forward" size={18} color="#10B981" />
          </TouchableOpacity>

          <TouchableOpacity
            style={[globalStyles.secondaryButton, { marginTop: 16 }]}
            onPress={() => router.push('/create-account')}
            disabled={isLoading}
          >
            <Text style={globalStyles.secondaryButtonText}>Create Account</Text>
          </TouchableOpacity>

          <View style={styles.signInRow}>
            <Text style={styles.signInHint}>Already have an account? </Text>
            <TouchableOpacity onPress={() => setActiveTab('email')} disabled={isLoading}>
              <Text style={styles.signInLink}>Sign in with email</Text>
            </TouchableOpacity>
          </View>
        </>
      )}

      {activeTab === 'email' && (
        <View style={styles.emailSection}>
          <TextInput
            placeholder="Email address"
            style={globalStyles.input}
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            autoCapitalize="none"
            editable={!isLoading}
          />
          <TextInput
            placeholder="Password"
            style={globalStyles.input}
            secureTextEntry
            value={password}
            onChangeText={setPassword}
            editable={!isLoading}
          />
          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={handleEmailLogin}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>
              {isLoading ? 'Logging in...' : 'Login'}
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            onPress={() => router.push('/forgot-password')}
            disabled={isLoading}
            style={{ alignItems: 'center', paddingVertical: 8 }}
          >
            <Text style={{ color: '#10B981', fontSize: 14 }}>Forgot password?</Text>
          </TouchableOpacity>
        </View>
      )}

      <TouchableOpacity
        onPress={() => router.push('/terms-and-conditions')}
        style={{ marginTop: 30, alignItems: 'center', paddingVertical: 12 }}
      >
        <Text style={{ fontSize: 13, color: '#64748B', textDecorationLine: 'underline' }}>
          Terms and Conditions
        </Text>
      </TouchableOpacity>

    </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  scrollContent: {
    flexGrow: 1,
    padding: 24,
    alignItems: 'center',
    backgroundColor: '#F8FAFC',
  },
  title: {
    fontSize: 22,
    fontWeight: '700',
    color: '#111827',
    marginBottom: 28,
    textAlign: 'center',
  },
  quickButton: {
    width: '100%',
    backgroundColor: '#111827',
    borderRadius: 16,
    paddingVertical: 18,
    paddingHorizontal: 20,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 12,
    elevation: 4,
  },
  quickRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  quickIconBox: {
    width: 40,
    height: 40,
    borderRadius: 10,
    backgroundColor: 'rgba(16,185,129,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 14,
  },
  quickTextBlock: { flex: 1 },
  quickButtonTitle: { color: '#fff', fontSize: 17, fontWeight: '700' },
  quickButtonSubtitle: { color: 'rgba(255,255,255,0.6)', fontSize: 12, marginTop: 3 },
  dividerRow: { flexDirection: 'row', alignItems: 'center', width: '100%', marginVertical: 20 },
  dividerLine: { flex: 1, height: 1, backgroundColor: '#E5E7EB' },
  dividerText: { marginHorizontal: 12, fontSize: 12, color: '#9CA3AF' },
  tabRow: {
    flexDirection: 'row',
    width: '100%',
    borderRadius: 12,
    backgroundColor: '#F3F4F6',
    padding: 4,
    marginBottom: 16,
  },
  tab: { flex: 1, paddingVertical: 10, alignItems: 'center', borderRadius: 9 },
  tabActive: {
    backgroundColor: '#fff',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  tabContent: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  tabText: { fontSize: 14, color: '#9CA3AF', fontWeight: '500' },
  tabTextActive: { color: '#111827', fontWeight: '600' },
  phoneCta: {
    width: '100%',
    backgroundColor: '#ECFDF5',
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: '#10B981',
    paddingVertical: 16,
    paddingHorizontal: 20,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 4,
  },
  phoneCtaText: { color: '#10B981', fontSize: 16, fontWeight: '600' },
  emailSection: { width: '100%' },
  signInRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 14,
    marginBottom: 8,
  },
  signInHint: { fontSize: 14, color: '#6B7280' },
  signInLink: { fontSize: 14, color: '#10B981', fontWeight: '600' },
});

