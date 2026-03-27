import { router } from 'expo-router';
import { useState } from 'react';

import { Alert, Image, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';
import { useAuthStore } from '../../src/stores/useAuthStore';


export default function LoginScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login, isLoading } = useAuthStore();

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Error', 'Please enter both email and password');
      return;
    }

    try {
      await login({ email, password });
      router.replace('/home');
    } catch (error: any) {
      Alert.alert('Login Failed', error.message || 'An error occurred during login');
    }
  };

  return (
    <View style={globalStyles.container}>
      <Image
        source={require('../images/fuel_station_logo.jpg')}
        style={globalStyles.logo}
        resizeMode="contain"
      />

      <Text style={globalStyles.title}>Interion Portable Refill App</Text>
      <Text style={globalStyles.subtitle}>Login Page</Text>

      <TextInput
        placeholder="Enter your email"
        style={globalStyles.input}
        value={email}
        onChangeText={setEmail}
        keyboardType="email-address"
        autoCapitalize="none"
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

      <TouchableOpacity
        style={globalStyles.primaryButton}
        onPress={handleLogin}
        disabled={isLoading}
      >
        <Text style={globalStyles.primaryButtonText}>
          {isLoading ? 'Logging in...' : 'Continue'}
        </Text>
      </TouchableOpacity>

      <Text style={globalStyles.or}>or</Text>

      <TouchableOpacity
        style={globalStyles.secondaryButton}
        onPress={() => router.push('/create-account')}
        disabled={isLoading}
      >
        <Text style={globalStyles.secondaryButtonText}>Create an account</Text>
      </TouchableOpacity>

      <TouchableOpacity 
      style={globalStyles.secondaryButton}
      onPress={() => router.push('/forgot-password')}
      disabled={isLoading}>
        <Text style={globalStyles.secondaryButtonText}>Forgot Password?</Text>
      </TouchableOpacity>
    </View>
  );
}
