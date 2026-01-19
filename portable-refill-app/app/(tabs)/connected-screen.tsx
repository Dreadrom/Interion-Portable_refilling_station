// app/debug.tsx
import { router } from 'expo-router';
import { useRef } from 'react';
import { Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { globalStyles } from '../styles/globalStyles';
import { Connect } from './test_home';

export default function ConnectedScreen() {
  const connectRef = useRef<Connect | null>(null);

  if (!connectRef.current) {
    connectRef.current = new Connect();
  }

  /** Get Controller Type */
  const onGetControllerTypePressed = async () => {
    try {
      const response = await connectRef.current!.getControllerType();
      const controllerType = response.Packets?.[0]?.Data?.Type ?? 'Unknown';
      Alert.alert('Controller Type', controllerType);
      console.log('[GetControllerType] Response:', response);
    } catch (err) {
      console.error('[GetControllerType] Error:', err);
      Alert.alert('Error', (err as Error).message);
    }
  };

  const onGetDateTimePressed = async () => {
    try {
      const response = await connectRef.current!.getDateTimePressed();
      const controllerType = response.Packets?.[0]?.Data?.Type ?? 'Unknown';
      Alert.alert('Date Time', controllerType);
      console.log('[GetDateTime] Response:', response);
    } catch (err) {
      console.error('[GetDateTime] Error:', err);
      Alert.alert('Error', (err as Error).message);
    }
  };

  

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Debug Panel</Text>

      <TouchableOpacity style={globalStyles.primaryButton} onPress={onGetControllerTypePressed}>
        <Text style={globalStyles.primaryButtonText}>Get Controller Type</Text>
      </TouchableOpacity>

      <TouchableOpacity style={[globalStyles.primaryButton, { marginTop: 16 }]} onPress={onGetDateTimePressed}>
        <Text style={globalStyles.primaryButtonText}>Get DateTime</Text>
      </TouchableOpacity>

      <TouchableOpacity style={[globalStyles.secondaryButton, { marginTop: 30 }]} onPress={() => router.replace('/home')}>
        <Text>Back to Home</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    fontSize: 22,
    fontWeight: '700',
    marginBottom: 40,
  },
});
