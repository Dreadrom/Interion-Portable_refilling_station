import { router } from 'expo-router';
import { Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

import { globalStyles } from '../../src/styles/globalStyles';

import { useRef } from 'react';
import { Connect } from '../../src/services/PTSConnect';
import { useAuthStore } from '../../src/stores/useAuthStore';

export default function HomeScreen() {
  const { user, logout } = useAuthStore();
  const connectRef = useRef<Connect | null>(null);

  if (!connectRef.current) {
    connectRef.current = new Connect();
  }

  const onConnectPressed = () => {
    router.push('/qr-scanner');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Interion Portable Refill Station</Text>

      <View style={styles.walletBox}>
        <Text style={styles.username}>{user?.name ?? 'Guest'}</Text>
        <Text style={styles.balance}>$501.90</Text>
      </View>

      <Text style={globalStyles.subtitle}>What would you like to do today?</Text>

      <TouchableOpacity style={globalStyles.secondaryButton}
      onPress={onConnectPressed}>
        <Text>Connect to a station</Text>
      </TouchableOpacity>

      <TouchableOpacity style={globalStyles.secondaryButton}
      onPress={() => router.push('./top-up-wallet')}>
        <Text>Top-up your wallet</Text>
      </TouchableOpacity>

      <TouchableOpacity style={globalStyles.secondaryButton}
      onPress={() => router.push('./profile')}>
        <Text>View / Edit profile</Text>
      </TouchableOpacity>

      <TouchableOpacity style={globalStyles.secondaryButton}
      onPress={() => router.push('/settings')}>
        <Text>Settings</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.logoutButton}
        onPress={async () => { await logout(); router.replace('/login'); }}
      >
        <Text>Logout</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 20,
    fontWeight: '700',
    textAlign: 'center',
    marginBottom: 30,
  },
  walletBox: {
    borderWidth: 1,
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
    marginBottom: 30,
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
  },
  username: {
    fontSize: 14,
    color: '#666',
  },
  balance: {
    fontSize: 28,
    fontWeight: '700',
    marginTop: 5,
  },
  logoutButton: {
    marginTop: 'auto',
    borderWidth: 1,
    borderRadius: 10,
    padding: 14,
    alignItems: 'center',
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
  },
});
