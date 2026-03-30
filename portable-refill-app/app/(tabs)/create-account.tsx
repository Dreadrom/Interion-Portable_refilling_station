import { router } from 'expo-router';
import { useState } from 'react';
import { Alert, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';
import { useAuthStore } from '../../src/stores/useAuthStore';

export default function CreateAccountScreen() {
  const [step, setStep] = useState<1 | 2>(1);
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const { register, isLoading } = useAuthStore();

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

    try {
      await register({ email, password, name });
      router.replace('/home');
    } catch (error: any) {
      Alert.alert('Registration Failed', error.message || 'An error occurred during registration');
    }
  };

  return (
    <View style={globalStyles.container}>
      <Text style={globalStyles.title}>AceRev Refill Kiosk</Text>
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
    </View>
  );
}
