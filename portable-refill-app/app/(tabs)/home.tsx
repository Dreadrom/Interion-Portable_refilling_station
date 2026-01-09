import { router } from 'expo-router';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Interion Portable Refill Station</Text>

      <View style={styles.walletBox}>
        <Text style={styles.username}>Username</Text>
        <Text style={styles.balance}>$501.90</Text>
      </View>

      <Text style={styles.question}>What would you like to do today?</Text>

      <TouchableOpacity style={styles.actionButton}>
        <Text>Connect to a station</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.actionButton}>
        <Text>Top-up your wallet</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.actionButton}>
        <Text>View / Edit profile</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.logoutButton}
        onPress={() => router.replace('/login')}
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
  question: {
    textAlign: 'center',
    marginBottom: 20,
  },
  actionButton: {
    borderWidth: 1,
    borderRadius: 10,
    padding: 16,
    marginBottom: 12,
    alignItems: 'center',
  },
  logoutButton: {
    marginTop: 'auto',
    borderWidth: 1,
    borderRadius: 10,
    padding: 14,
    alignItems: 'center',
  },
});
