import { router } from 'expo-router';
import React, { useState, useEffect } from 'react';
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { globalStyles } from '../../src/styles/globalStyles';
import { useAuthStore } from '../../src/stores/useAuthStore';
import { updateProfile } from '../../src/api/auth';
import { transactionStore } from '../../src/utils/transactionStore';

export default function ProfileScreen() {
  const { user, logout, updateUser } = useAuthStore();
  const insets = useSafeAreaInsets();

  const [name, setName] = useState(user?.name ?? '');
  const [email, setEmail] = useState(user?.email ?? '');
  const [phone, setPhone] = useState(user?.phone ?? '');
  
  const [isEditing, setIsEditing] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [txnCount, setTxnCount] = useState(0);

  useEffect(() => {
    transactionStore.getAllTransactions().then((list) => setTxnCount(list.length));
  }, []);

  const memberSince = user ? new Date(user.createdAt).toLocaleDateString('en-US', { year: 'numeric', month: 'long' }) : '';
  const walletBalance = user?.walletBalance ?? 0;
  const currency = 'MYR';

  const handleSave = async () => {
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
      const updatedData = await updateProfile({ name, email, phone: phone || undefined });
      if (user) {
        await updateUser({ ...user, name, phone: phone || undefined });
      }
      Alert.alert('Success', 'Your profile has been updated successfully.');
      setIsEditing(false);
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to update profile.');
    } finally {
      setProcessing(false);
    }
  };

  const handleCancel = () => {
    setName(user?.name ?? '');
    setEmail(user?.email ?? '');
    setPhone(user?.phone ?? '');
    setIsEditing(false);
  };

  const handleChangePassword = () => {
    router.push('./change-password');
  };

  const handleDeleteAccount = () => {
    Alert.alert(
      'Delete Account',
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
    <ScrollView style={styles.container} contentContainerStyle={[styles.contentContainer, { paddingTop: insets.top + 24 }]}>
      {/* Header */}
      <View style={styles.header}>
        <View style={styles.avatarContainer}>
          <Text style={styles.avatarText}>{(user?.name ?? 'U').charAt(0).toUpperCase()}</Text>
        </View>
        <Text style={styles.headerTitle}>{user?.name ?? ''}</Text>
        <Text style={styles.headerSubtitle}>Member since {memberSince}</Text>
      </View>

      {/* Stats Cards */}
      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{currency} {walletBalance.toFixed(2)}</Text>
          <Text style={styles.statLabel}>Wallet Balance</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statValue}>{txnCount}</Text>
          <Text style={styles.statLabel}>Transactions</Text>
        </View>
      </View>

      {/* Personal Information */}
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Text style={[styles.cardTitle, { marginBottom: 0, flex: 1 }]}>Personal Information</Text>
          {!isEditing && (
            <View style={styles.headerButtons}>
              <TouchableOpacity onPress={handleChangePassword} style={{ marginRight: 14 }}>
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
        <Text style={styles.cardTitle}>Account</Text>

        <TouchableOpacity style={styles.actionRow} onPress={() => router.push('./transaction-history')}>
          <View style={styles.actionIconBox}>
            <Ionicons name="time-outline" size={20} color="#10B981" />
          </View>
          <Text style={styles.actionLabel}>Transaction History</Text>
          <Ionicons name="chevron-forward" size={18} color="#D1D5DB" />
        </TouchableOpacity>

        <View style={styles.actionDivider} />

        <TouchableOpacity style={styles.actionRow} onPress={() => router.push('./bank-account')}>
          <View style={styles.actionIconBox}>
            <Ionicons name="card-outline" size={20} color="#10B981" />
          </View>
          <Text style={styles.actionLabel}>Bank Accounts &amp; DuitNow</Text>
          <Ionicons name="chevron-forward" size={18} color="#D1D5DB" />
        </TouchableOpacity>

        <View style={styles.actionDivider} />

        <TouchableOpacity style={styles.actionRow} onPress={() => router.push('./wallet-cashout')}>
          <View style={styles.actionIconBox}>
            <Ionicons name="arrow-up-circle-outline" size={20} color="#10B981" />
          </View>
          <Text style={styles.actionLabel}>Cash Out Wallet</Text>
          <Ionicons name="chevron-forward" size={18} color="#D1D5DB" />
        </TouchableOpacity>

        <View style={styles.actionDivider} />

        <TouchableOpacity style={styles.actionRow} onPress={() => router.push('./home')}>
          <View style={styles.actionIconBox}>
            <Ionicons name="home-outline" size={20} color="#10B981" />
          </View>
          <Text style={styles.actionLabel}>Return to Home</Text>
          <Ionicons name="chevron-forward" size={18} color="#D1D5DB" />
        </TouchableOpacity>
      </View>

      {/* Danger Zone */}
      <View style={[styles.card, styles.dangerCard]}>
        <TouchableOpacity style={styles.dangerRow} onPress={handleDeleteAccount}>
          <Ionicons name="trash-outline" size={18} color="#EF4444" />
          <Text style={styles.dangerLabel}>Delete Account</Text>
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
    paddingHorizontal: 20,
    paddingBottom: 40,
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
    fontSize: 22,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  headerSubtitle: {
    fontSize: 13,
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
    fontSize: 16,
    fontWeight: '700',
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
    gap: 12,
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
    fontSize: 11,
    fontWeight: '500',
    color: '#3B82F6',
  },
  actionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 14,
  },
  actionIconBox: {
    width: 36,
    height: 36,
    borderRadius: 10,
    backgroundColor: '#ECFDF5',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 14,
  },
  actionLabel: {
    flex: 1,
    fontSize: 15,
    color: '#111827',
    fontWeight: '500',
  },
  actionDivider: {
    height: 1,
    backgroundColor: '#F3F4F6',
    marginLeft: 50,
  },
  dangerCard: {
    backgroundColor: '#FEF2F2',
    borderWidth: 1,
    borderColor: '#FECACA',
  },
  dangerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingVertical: 4,
  },
  dangerLabel: {
    fontSize: 15,
    color: '#EF4444',
    fontWeight: '500',
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
