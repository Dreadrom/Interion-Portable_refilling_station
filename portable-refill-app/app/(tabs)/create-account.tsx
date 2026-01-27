import { router } from 'expo-router';
import { useState } from 'react';
import { Alert, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { register } from '../../src/api/auth';

export default function CreateAccountScreen() {
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [name, setName] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSendVerification = () => {
    // SEND VERIFICATION CODE TO 'email'
    if (!email) {
      Alert.alert('Error', 'Please enter your email');
      return;
    }
    
    setStep(2);
  };

  const handleVerifyCode = () => {
    if (!verificationCode) {
      Alert.alert('Error', 'Please enter the verification code');
      return;
    }
    
    // check verification code correct
    setStep(3);
  };

  const handleResendCode = () => {
    Alert.alert('Resend Code', `Resending code to: ${email}`);
    // resend verification code to 'email'
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

    setIsLoading(true);
    try {
      const response = await register({ 
        email, 
        password, 
        name 
      });
      
      Alert.alert('Registration Success', `Account created! Token: ${response.token?.substring(0, 20)}...`);
      
      // Store token here (e.g., in AsyncStorage or SecureStore)
      // await SecureStore.setItemAsync('authToken', response.token);
      
      router.replace('/home');
    } catch (error: any) {
      Alert.alert('Registration Failed', error.message || 'An error occurred during registration');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={globalStyles.container}>
      <Text style={globalStyles.title}>Interion Portable Refill App</Text>
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
            onPress={handleSendVerification}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>Continue</Text>
          </TouchableOpacity>
        </>
      )}

      {step === 2 && (
        <>
          <TextInput
            placeholder="Enter verification code"
            style={globalStyles.input}
            value={verificationCode}
            onChangeText={setVerificationCode}
            keyboardType="number-pad"
            editable={!isLoading}
          />

          <TouchableOpacity onPress={handleResendCode} disabled={isLoading}>
            <Text style={globalStyles.resend}>Resend code</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={handleVerifyCode}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>Continue</Text>
          </TouchableOpacity>
        </>
      )}

      {step === 3 && (
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
              {isLoading ? 'Creating Account...' : 'Continue'}
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
