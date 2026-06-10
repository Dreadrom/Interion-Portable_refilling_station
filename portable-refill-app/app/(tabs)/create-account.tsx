import { router } from 'expo-router';
import { useState } from 'react';
import { Alert, KeyboardAvoidingView, ScrollView, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';
import { useAuthStore } from '../../src/stores/useAuthStore';

export default function CreateAccountScreen() {
  const [step, setStep] = useState<1 | 2>(1);
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreedToTerms, setAgreedToTerms] = useState(false);
  const { register, createOfflineSession, isLoading } = useAuthStore();

  const handleContinue = () => {
    if (!email) {
      Alert.alert('Error', 'Please enter your email');
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      Alert.alert('Error', 'Please enter a valid email address');
      return;
    }
    setStep(2);
  };

  const handleRegister = async () => {
    if (!name || !password || !confirmPassword) {
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert('Error', 'Passwords do not match');
      return;
    }

    if (!agreedToTerms) {
      Alert.alert('Terms Required', 'Please agree to the Terms and Conditions to continue');
      return;
    }

    try {
      await register({ email, password, name });
      router.replace('/home');
    } catch (error: any) {
      // Backend unreachable (network error or timeout) — create local account so the demo flow works
      if (error.code === 'SERVICE_UNAVAILABLE' || error.code === 'GATEWAY_TIMEOUT' || error.code === 'INTERNAL_SERVER_ERROR') {
        await createOfflineSession({ name, email });
        router.replace('/home');
      } else {
        Alert.alert('Registration Failed', error.message || 'An error occurred during registration');
      }
    }
  };

  return (
    <KeyboardAvoidingView style={{ flex: 1, backgroundColor: '#F8FAFC' }} behavior="padding">
    <ScrollView contentContainerStyle={[globalStyles.container, { flexGrow: 1 }]} keyboardShouldPersistTaps="handled">
      <Text style={globalStyles.title}>BlueDiesel</Text>
      <Text style={globalStyles.subtitle}>Create Account</Text>

      {step === 1 && (
        <>
          <TextInput
            placeholder="Enter your email"
            style={globalStyles.input}
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            autoCapitalize="none"
            editable={!isLoading}
          />

          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={handleContinue}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>Continue</Text>
          </TouchableOpacity>
        </>
      )}

      {step === 2 && (
        <>
          <TextInput
            placeholder="Enter your name"
            style={globalStyles.input}
            value={name}
            onChangeText={setName}
            editable={!isLoading}
          />

          <TextInput
            placeholder="Enter your password"
            style={globalStyles.input}
            secureTextEntry
            value={password}
            onChangeText={setPassword}
            editable={!isLoading}
          />

          <TextInput
            placeholder="Re-enter a password"
            style={globalStyles.input}
            secureTextEntry
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            editable={!isLoading}
          />

          <View
            style={{
              width: '100%',
              maxWidth: 400,
              alignSelf: 'center',
              marginTop: 8,
              marginBottom: 4,
            }}
          >
            <TouchableOpacity
              style={{
                flexDirection: 'row',
                alignItems: 'center',
                paddingVertical: 12,
                paddingHorizontal: 4,
              }}
              onPress={() => setAgreedToTerms(!agreedToTerms)}
              disabled={isLoading}
            >
              <View
                style={{
                  width: 24,
                  height: 24,
                  borderRadius: 4,
                  borderWidth: 2,
                  borderColor: agreedToTerms ? '#10B981' : '#CBD5E1',
                  backgroundColor: agreedToTerms ? '#10B981' : 'white',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginRight: 12,
                  flexShrink: 0,
                }}
              >
                {agreedToTerms && (
                  <Text style={{ color: 'white', fontSize: 16, fontWeight: 'bold' }}>✓</Text>
                )}
              </View>
              <View style={{ flex: 1, flexDirection: 'row', flexWrap: 'wrap' }}>
                <Text style={{ fontSize: 14, color: '#475569' }}>I agree to the </Text>
                <Text
                  style={{ fontSize: 14, color: '#10B981', textDecorationLine: 'underline' }}
                  onPress={(e) => {
                    e.stopPropagation();
                    router.push('/terms-and-conditions');
                  }}
                >
                  Terms and Conditions
                </Text>
              </View>
            </TouchableOpacity>
          </View>

          <TouchableOpacity 
            style={globalStyles.primaryButton}
            onPress={handleRegister}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>
              {isLoading ? 'Creating Account...' : 'Create Account'}
            </Text>
          </TouchableOpacity>
        </>
      )}

      <TouchableOpacity
        style={globalStyles.secondaryButton}
        onPress={() => router.back()}
        disabled={isLoading}
      >
        <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
      </TouchableOpacity>
    </ScrollView>
    </KeyboardAvoidingView>
  );
}
