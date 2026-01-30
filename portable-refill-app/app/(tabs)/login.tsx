import { router } from 'expo-router';
import { useState } from 'react';

import { Alert, Image, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { login } from '../../src/api/auth';


export default function LoginScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async () => {
    if (!email || !password) {
      Alert.alert('Error', 'Please enter both email and password');
      return;
    }

    setIsLoading(true);
    try {
      const response = await login({ email, password });
      
      // Store token here (e.g., in AsyncStorage or SecureStore)
      // await SecureStore.setItemAsync('authToken', response.token);
      
      router.replace('/home');
    } catch (error: any) {
      Alert.alert('Login Failed', error.message || 'An error occurred during login');
    } finally {
      setIsLoading(false);
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
