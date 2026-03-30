import { router } from 'expo-router';
import React, { useState } from 'react';
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { globalStyles } from '../../src/styles/globalStyles';
import { changePassword } from '../../src/api/auth';

export default function ChangePasswordScreen() {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [processing, setProcessing] = useState(false);

  const handleChangePassword = async () => {
    if (!currentPassword || !newPassword || !confirmPassword) {
      Alert.alert('Validation Error', 'Please fill in all fields.');
      return;
    }

    if (newPassword.length < 6) {
      Alert.alert('Validation Error', 'New password must be at least 6 characters.');
      return;
    }

    if (newPassword !== confirmPassword) {
      Alert.alert('Validation Error', 'New passwords do not match.');
      return;
    }

    setProcessing(true);

    try {
      await changePassword({ currentPassword, newPassword });
      Alert.alert('Success', 'Your password has been changed successfully.', [
        { text: 'OK', onPress: () => router.back() },
      ]);
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to change password.');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Change Password</Text>
        <Text style={styles.headerSubtitle}>Enter your current and new password</Text>
      </View>

      <View style={styles.card}>
        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>Current Password</Text>
          <TextInput
            style={styles.fieldInput}
            value={currentPassword}
            onChangeText={setCurrentPassword}
            secureTextEntry
            placeholder="Enter current password"
            editable={!processing}
          />
        </View>

        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>New Password</Text>
          <TextInput
            style={styles.fieldInput}
            value={newPassword}
            onChangeText={setNewPassword}
            secureTextEntry
            placeholder="Enter new password (min. 6 characters)"
            editable={!processing}
          />
        </View>

        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>Confirm New Password</Text>
          <TextInput
            style={styles.fieldInput}
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            secureTextEntry
            placeholder="Re-enter new password"
            editable={!processing}
          />
        </View>

        <TouchableOpacity
          style={[globalStyles.primaryButton, { marginTop: 16 }]}
          onPress={handleChangePassword}
          disabled={processing}
        >
          <Text style={globalStyles.primaryButtonText}>
            {processing ? 'Changing Password...' : 'Change Password'}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.secondaryButton, { marginTop: 8 }]}
          onPress={() => router.back()}
          disabled={processing}
        >
          <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  contentContainer: {
    padding: 16,
    paddingBottom: 40,
  },
  header: {
    marginBottom: 20,
    paddingVertical: 16,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: 4,
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#666',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  fieldGroup: {
    marginBottom: 16,
  },
  fieldLabel: {
    fontSize: 12,
    fontWeight: '600',
    color: '#666',
    marginBottom: 6,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  fieldInput: {
    borderWidth: 1,
    borderColor: '#e0e0e0',
    borderRadius: 8,
    padding: 12,
    fontSize: 16,
    color: '#1a1a2e',
    backgroundColor: '#fff',
  },
});
