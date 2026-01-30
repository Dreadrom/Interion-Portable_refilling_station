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
import { globalStyles } from '../styles/globalStyles';
import { User } from '../../src/types/user';

export default function ProfileScreen() {
  // Mock user data - in production this would come from user context/API
  const [user, setUser] = useState<User>({
    id: 'user_123456',
    email: 'john.doe@example.com',
    name: 'John Doe',
    phone: '+60 12-345 6789',
    role: 'DRIVER',
    createdAt: '2025-01-15T08:00:00Z',
  });
  
  const [name, setName] = useState(user.name);
  const [email, setEmail] = useState(user.email);
  const [phone, setPhone] = useState(user.phone || '');
  
  const [isEditing, setIsEditing] = useState(false);
  const [processing, setProcessing] = useState(false);

  const memberSince = new Date(user.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long' });
  const walletBalance = 501.90;
  const totalTransactions = 47;
  const currency = 'MYR';

  const handleSave = async () => {
    // Validation
    if (!name.trim()) {
      Alert.alert('Validation Error', 'Name is required.');
      return;
    }

    if (!email.trim() || !email.includes('@')) {
      Alert.alert('Validation Error', 'Please enter a valid email address.');
      return;
    }

    setProcessing(true);

    try {
      // Simulate API call with UpdateProfileRequest
      await new Promise(resolve => setTimeout(resolve, 1500));

      // Update user state
      setUser({ ...user, name, email, phone: phone || undefined });
      
      Alert.alert('Success', 'Your profile has been updated successfully.');
      setIsEditing(false);
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to update profile.');
    } finally {
      setProcessing(false);
    }
  };

  const handleCancel = () => {
    // Reset to original values
    setName(user.name);
    setEmail(user.email);
    setPhone(user.phone || '');
    setIsEditing(false);
  };

  const handleChangePassword = () => {
    Alert.alert(
      'Change Password',
      'You will be redirected to change your password.',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Continue', onPress: () => {
          // Navigate to change password screen
          Alert.alert('Info', 'Change password screen would be implemented here.');
        }},
      ]
    );
  };

  const handleDeleteAccount = () => {
    Alert.alert(
      '⚠️ Delete Account',
      'This action cannot be undone. All your data will be permanently deleted.',
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: () => {
            Alert.alert('Info', 'Account deletion process would be implemented here.');
          },
        },
      ]
    );
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.avatarContainer}>
          <Text style={styles.avatarText}>{user.name.charAt(0).toUpperCase()}</Text>
        </View>
        <Text style={styles.headerTitle}>{user.name}</Text>
        <Text style={styles.headerSubtitle}>Member since {memberSince}</Text>
      </View>

      {/* Stats Cards */}
      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{currency} {walletBalance.toFixed(2)}</Text>
          <Text style={styles.statLabel}>Wallet Balance</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{totalTransactions}</Text>
          <Text style={styles.statLabel}>Transactions</Text>
        </View>
      </View>

      {/* Personal Information */}
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Text style={styles.cardTitle}>Personal Information</Text>
          {!isEditing && (
            <View style={styles.headerButtons}>
              <TouchableOpacity onPress={handleChangePassword} style={{ marginRight: 16 }}>
                <Text style={styles.editButton}>Change Password</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => setIsEditing(true)}>
                <Text style={styles.editButton}>Edit</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>

        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>Name</Text>
          <TextInput
            style={[styles.fieldInput, !isEditing && styles.fieldInputDisabled]}
            value={name}
            onChangeText={setName}
            editable={isEditing}
            placeholder="Enter your name"
          />
        </View>

        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>Email Address</Text>
          <TextInput
            style={[styles.fieldInput, !isEditing && styles.fieldInputDisabled]}
            value={email}
            onChangeText={setEmail}
            editable={isEditing}
            keyboardType="email-address"
            autoCapitalize="none"
            placeholder="Enter your email"
          />
        </View>

        <View style={styles.fieldGroup}>
          <Text style={styles.fieldLabel}>Phone Number (Optional)</Text>
          <TextInput
            style={[styles.fieldInput, !isEditing && styles.fieldInputDisabled]}
            value={phone}
            onChangeText={setPhone}
            editable={isEditing}
            keyboardType="phone-pad"
            placeholder="Enter your phone number"
          />
        </View>

        {isEditing && (
          <View style={styles.editActions}>
            <TouchableOpacity
              style={[globalStyles.secondaryButton, { flex: 1, marginRight: 8 }]}
              onPress={handleCancel}
              disabled={processing}
            >
              <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[globalStyles.primaryButton, { flex: 1, marginLeft: 8 }]}
              onPress={handleSave}
              disabled={processing}
            >
              <Text style={globalStyles.primaryButtonText}>
                {processing ? 'Saving...' : 'Save Changes'}
              </Text>
            </TouchableOpacity>
          </View>
        )}
      </View>

      {/* Actions */}
      <View style={styles.card}>
        <TouchableOpacity
          style={globalStyles.secondaryButton}
          onPress={() => Alert.alert('Info', 'Transaction history would be displayed here.')}
        >
          <Text style={globalStyles.secondaryButtonText}>Transaction History</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[globalStyles.primaryButton, { marginTop: 12 }]}
          onPress={() => router.push('./home')}
        >
          <Text style={globalStyles.primaryButtonText}>Return to Home</Text>
        </TouchableOpacity>
      </View>

      <View style={{ height: 40 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  contentContainer: {
    paddingTop: 60,
    paddingHorizontal: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 24,
  },
  avatarContainer: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: '#10B981',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
    shadowColor: '#10B981',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 12,
    elevation: 8,
  },
  avatarText: {
    fontSize: 42,
    fontWeight: 'bold',
    color: '#fff',
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#6B7280',
  },
  statsContainer: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 24,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
  },
  statCard: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  statValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#10B981',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: '#6B7280',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    maxWidth: 600,
    alignSelf: 'center',
    width: '100%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  headerButtons: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 16,
  },
  editButton: {
    fontSize: 14,
    fontWeight: '600',
    color: '#3B82F6',
  },
  fieldGroup: {
    marginBottom: 16,
  },
  fieldLabel: {
    fontSize: 13,
    fontWeight: '500',
    color: '#6B7280',
    marginBottom: 6,
  },
  fieldInput: {
    borderWidth: 1,
    borderColor: '#D1D5DB',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 15,
    color: '#111827',
    backgroundColor: '#fff',
  },
  fieldInputDisabled: {
    backgroundColor: '#F3F4F6',
    color: '#6B7280',
  },
  editActions: {
    flexDirection: 'row',
    marginTop: 8,
  },
});
