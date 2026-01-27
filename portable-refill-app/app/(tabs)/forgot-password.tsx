import { router } from 'expo-router';
import { useState } from 'react';
import { Alert, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { forgotPassword, resetPassword } from '../../src/api/auth';

export default function ForgotPasswordScreen() {
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [email, setEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSendResetEmail = async () => {
    Alert.alert('Forgot Password Function Called', `Sending reset email to: ${email}`);
    
    if (!email) {
      Alert.alert('Error', 'Please enter your email');
      return;
    }

    setIsLoading(true);
    try {
      const response = await forgotPassword({ email });
      Alert.alert('Success', response.message);
      setStep(2);
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to send reset email');
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyCode = () => {
    Alert.alert('Verify Code Function Called', `Verifying code: ${verificationCode}`);
    
    if (!verificationCode) {
      Alert.alert('Error', 'Please enter the verification code');
      return;
    }
    
    // Mock: In real implementation, this would verify the code with backend
    Alert.alert('Success', 'Code verified successfully');
    setStep(3);
  };

  const handleResendCode = async () => {
    Alert.alert('Resend Code Function Called', `Resending reset email to: ${email}`);
    
    setIsLoading(true);
    try {
      const response = await forgotPassword({ email });
      Alert.alert('Success', 'Reset email resent');
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to resend email');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetPassword = async () => {
    Alert.alert('Reset Password Function Called', `Resetting password with token: ${verificationCode}`);
    
    if (!newPassword || !confirmPassword) {
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }

    if (newPassword !== confirmPassword) {
      Alert.alert('Error', 'Passwords do not match');
      return;
    }

    setIsLoading(true);
    try {
      const response = await resetPassword({ 
        token: verificationCode, 
        newPassword: newPassword 
      });
      
      Alert.alert('Success', response.message);
      router.replace('/login');
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to reset password');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={globalStyles.container}>
      <Text style={globalStyles.title}>Interion Portable Refill App</Text>
      <Text style={globalStyles.subtitle}>Forgot Password</Text>

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
            onPress={handleSendResetEmail}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>
              {isLoading ? 'Sending...' : 'Continue'}
            </Text>
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
            placeholder="Enter new password"
            style={globalStyles.input}
            secureTextEntry
            value={newPassword}
            onChangeText={setNewPassword}
            editable={!isLoading}
          />

          <TextInput
            placeholder="Re-enter new password"
            style={globalStyles.input}
            secureTextEntry
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            editable={!isLoading}
          />

          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={handleResetPassword}
            disabled={isLoading}
          >
            <Text style={globalStyles.primaryButtonText}>
              {isLoading ? 'Resetting...' : 'Continue'}
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
