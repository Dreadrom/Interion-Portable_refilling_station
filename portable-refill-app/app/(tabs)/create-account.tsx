import { router } from 'expo-router';
import { useState } from 'react';
import { Text, TextInput, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';

export default function CreateAccountScreen() {
  const [step, setStep] = useState<1 | 2 | 3>(1);

  return (
    <View style={globalStyles.container}>
      <Text style={globalStyles.title}>Interion Portable Refill App</Text>
      <Text style={globalStyles.subtitle}>Create Account</Text>

      {step === 1 && (
        <>
          <TextInput
            placeholder="Enter your email"
            style={globalStyles.input}
          />

          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={() => setStep(2)}
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
          />

          <TouchableOpacity>
            <Text style={globalStyles.resend}>Resend code</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={globalStyles.primaryButton}
            onPress={() => setStep(3)}
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
          />

          <TextInput
            placeholder="Enter your password"
            style={globalStyles.input}
            secureTextEntry
          />

          <TextInput
            placeholder="Re-enter a password"
            style={globalStyles.input}
            secureTextEntry
          />

          <TouchableOpacity style={globalStyles.primaryButton}
            onPress={() => router.replace('/login')}>
            <Text style={globalStyles.primaryButtonText}>Continue</Text>
          </TouchableOpacity>
        </>
      )}

      <TouchableOpacity
        style={globalStyles.secondaryButton}
        onPress={() => router.back()}
      >
        <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
      </TouchableOpacity>
    </View>
  );
}
