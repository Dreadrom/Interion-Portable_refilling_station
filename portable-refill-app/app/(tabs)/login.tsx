import { router } from 'expo-router';
import { useState } from 'react';

import { Image, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';


export default function LoginScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

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
      />

      <TextInput
        placeholder="Enter your password"
        style={globalStyles.input}
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      <TouchableOpacity
        style={globalStyles.primaryButton}
        onPress={() => {
          // Toast.show({
          //   type: 'success',
          //   text1: 'Logging into account...',
          //   text2: 'Welcome back 😊',
          //   position: 'bottom',
          // });
          router.replace('/home')
        }}
      >
        <Text style={globalStyles.primaryButtonText}>Continue</Text>
      </TouchableOpacity>

      <Text style={globalStyles.or}>or</Text>

      <TouchableOpacity
        style={globalStyles.secondaryButton}
        onPress={() => router.push('/create-account')}
      >
        <Text style={globalStyles.secondaryButtonText}>Create an account</Text>
      </TouchableOpacity>

      <TouchableOpacity 
      style={globalStyles.secondaryButton}
      onPress={() => router.push('/forgot-password')}>
        <Text style={globalStyles.secondaryButtonText}>Forgot Password?</Text>
      </TouchableOpacity>
    </View>
  );
}
