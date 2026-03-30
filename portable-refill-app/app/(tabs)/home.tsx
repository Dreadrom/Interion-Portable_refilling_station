import { router, useFocusEffect } from 'expo-router';
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useRef, useState, useCallback } from 'react';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Connect } from '../../src/services/PTSConnect';
import { useAuthStore } from '../../src/stores/useAuthStore';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';

type GridItem = {
  icon: keyof typeof Ionicons.glyphMap;
  label: string;
  route: string;
};

const accountMenuItems: GridItem[] = [
  { icon: 'time-outline', label: 'History', route: './transaction-history' },
  { icon: 'person-outline', label: 'Profile', route: './profile' },
  { icon: 'card-outline', label: 'Bank Accounts', route: './bank-account' },
  { icon: 'settings-outline', label: 'Settings', route: '/settings' },
];

export default function HomeScreen() {
  const { user, logout, isGuest } = useAuthStore();
  const insets = useSafeAreaInsets();
  const connectRef = useRef<Connect | null>(null);
  if (!connectRef.current) connectRef.current = new Connect();
  const [recentTxns, setRecentTxns] = useState<TransactionData[]>([]);

  useFocusEffect(useCallback(() => {
    if (!isGuest) {
      transactionStore.getAllTransactions().then(list => setRecentTxns(list.slice(0, 3)));
    }
  }, [isGuest]));

  const initial = (user?.name ?? 'G').charAt(0).toUpperCase();
  const balance = (user?.walletBalance ?? 0).toFixed(2);

  const handleLogout = async () => {
    await logout();
    router.replace('/login');
  };

  return (
    <ScrollView style={styles.screen} contentContainerStyle={[styles.content, { paddingTop: insets.top + 20 }]}>
      {/* Header Row */}
      <View style={styles.topRow}>
        <View>
          <Text style={styles.greeting}>Good day,</Text>
          <Text style={styles.username}>{user?.name ?? 'Guest'}</Text>
        </View>
        {!isGuest && (
          <TouchableOpacity style={styles.avatar} onPress={() => router.push('./profile')}>
            <Text style={styles.avatarText}>{initial}</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Guest Banner */}
      {isGuest && (
        <View style={styles.guestBanner}>
          <Ionicons name="information-circle-outline" size={20} color="#92400E" />
          <View style={styles.guestBody}>
            <Text style={styles.guestTitle}>Quick Dispense Mode</Text>
            <Text style={styles.guestSub}>This session is temporary — history is not saved.</Text>
          </View>
          <TouchableOpacity onPress={() => router.push('/create-account')}>
            <Text style={styles.guestLink}>Sign Up</Text>
          </TouchableOpacity>
        </View>
      )}

      {/* Wallet Card */}
      <View style={styles.walletCard}>
        <Text style={styles.walletLabel}>Wallet Balance</Text>
        <Text style={styles.walletBalance}>MYR {balance}</Text>
        <View style={styles.walletActions}>
          <TouchableOpacity style={styles.walletBtn} onPress={() => router.push('./top-up-wallet')}>
            <Ionicons name="add" size={15} color="#059669" />
            <Text style={styles.walletBtnText}>Top Up</Text>
          </TouchableOpacity>
          {!isGuest && (
            <TouchableOpacity style={styles.walletBtnSecondary} onPress={() => router.push('./wallet-cashout')}>
              <Ionicons name="arrow-up-circle-outline" size={15} color="rgba(255,255,255,0.8)" />
              <Text style={styles.walletBtnSecondaryText}>Cash Out</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* Wallet Recent Activity */}
      {!isGuest && recentTxns.length > 0 && (
        <View style={styles.activityCard}>
          <View style={styles.activityHeader}>
            <Text style={styles.activityTitle}>Recent Activity</Text>
            <TouchableOpacity onPress={() => router.push('./transaction-history')}>
              <Text style={styles.activitySeeAll}>See all</Text>
            </TouchableOpacity>
          </View>
          {recentTxns.map(txn => (
            <View key={txn.id} style={styles.activityRow}>
              <View style={styles.activityIcon}>
                <Ionicons name="water-outline" size={16} color="#10B981" />
              </View>
              <View style={styles.activityBody}>
                <Text style={styles.activityLabel} numberOfLines={1}>{txn.stationName} · {txn.product}</Text>
                <Text style={styles.activityDate}>
                  {new Date(txn.timestamp).toLocaleDateString('en-MY', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })}
                </Text>
              </View>
              <View style={styles.activityAmounts}>
                <Text style={styles.activityDebit}>-{txn.currency} {txn.amountCharged.toFixed(2)}</Text>
              </View>
            </View>
          ))}
        </View>
      )}

      {/* Connect to Station */}
      <TouchableOpacity style={styles.connectCard} onPress={() => router.push('/qr-scanner')}>
        <View style={styles.connectIconBox}>
          <Ionicons name="qr-code-outline" size={26} color="#10B981" />
        </View>
        <View style={styles.connectBody}>
          <Text style={styles.connectTitle}>Connect to Station</Text>
          <Text style={styles.connectSub}>Scan the QR code to start refilling</Text>
        </View>
        <Ionicons name="chevron-forward" size={20} color="#D1D5DB" />
      </TouchableOpacity>

      {/* Account Grid */}
      {!isGuest && (
        <>
          <Text style={styles.sectionLabel}>Your Account</Text>
          <View style={styles.grid}>
            {accountMenuItems.map((item) => (
              <TouchableOpacity
                key={item.label}
                style={styles.gridCard}
                onPress={() => router.push(item.route as any)}
              >
                <View style={styles.gridIconBox}>
                  <Ionicons name={item.icon} size={22} color="#10B981" />
                </View>
                <Text style={styles.gridLabel}>{item.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </>
      )}

      {/* Sign Out */}
      <TouchableOpacity style={styles.signOutBtn} onPress={handleLogout}>
        <Ionicons name="log-out-outline" size={17} color="#9CA3AF" />
        <Text style={styles.signOutText}>
          {isGuest ? 'Exit Quick Dispense' : 'Sign Out'}
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#F8FAFC' },
  content: { paddingHorizontal: 20, paddingBottom: 40 },

  topRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 28,
  },
  greeting: { fontSize: 13, color: '#9CA3AF', marginBottom: 2 },
  username: { fontSize: 22, fontWeight: '700', color: '#111827' },
  avatar: {
    width: 42,
    height: 42,
    borderRadius: 21,
    backgroundColor: '#10B981',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarText: { color: '#fff', fontWeight: '700', fontSize: 17 },

  guestBanner: {
    backgroundColor: '#FEF3C7',
    borderRadius: 12,
    padding: 14,
    marginBottom: 20,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    borderWidth: 1,
    borderColor: '#FDE68A',
  },
  guestBody: { flex: 1 },
  guestTitle: { fontSize: 13, fontWeight: '600', color: '#92400E' },
  guestSub: { fontSize: 12, color: '#B45309', marginTop: 1 },
  guestLink: { fontSize: 13, color: '#10B981', fontWeight: '600' },

  walletCard: {
    backgroundColor: '#111827',
    borderRadius: 20,
    padding: 24,
    marginBottom: 16,
  },
  walletLabel: { fontSize: 13, color: 'rgba(255,255,255,0.5)', marginBottom: 6 },
  walletBalance: {
    fontSize: 36,
    fontWeight: '700',
    color: '#fff',
    marginBottom: 22,
    letterSpacing: -0.5,
  },
  walletActions: { flexDirection: 'row', gap: 10 },
  walletBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    backgroundColor: '#ECFDF5',
    borderRadius: 10,
    paddingVertical: 9,
    paddingHorizontal: 16,
  },
  walletBtnText: { color: '#059669', fontWeight: '600', fontSize: 14 },
  walletBtnSecondary: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    backgroundColor: 'rgba(255,255,255,0.12)',
    borderRadius: 10,
    paddingVertical: 9,
    paddingHorizontal: 16,
  },
  walletBtnSecondaryText: { color: 'rgba(255,255,255,0.8)', fontWeight: '600', fontSize: 14 },

  connectCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 18,
    marginBottom: 28,
    flexDirection: 'row',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06,
    shadowRadius: 8,
    elevation: 2,
  },
  connectIconBox: {
    width: 50,
    height: 50,
    borderRadius: 14,
    backgroundColor: '#ECFDF5',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 14,
  },
  connectBody: { flex: 1 },
  connectTitle: { fontSize: 16, fontWeight: '600', color: '#111827' },
  connectSub: { fontSize: 13, color: '#6B7280', marginTop: 2 },

  sectionLabel: {
    fontSize: 11,
    fontWeight: '600',
    color: '#9CA3AF',
    textTransform: 'uppercase',
    letterSpacing: 0.8,
    marginBottom: 12,
  },
  grid: { flexDirection: 'row', flexWrap: 'wrap', gap: 12, marginBottom: 28 },
  gridCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 18,
    width: '47%',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 6,
    elevation: 2,
  },
  gridIconBox: {
    width: 44,
    height: 44,
    borderRadius: 12,
    backgroundColor: '#ECFDF5',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  gridLabel: { fontSize: 14, fontWeight: '600', color: '#111827' },

  signOutBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 14,
    gap: 8,
  },
  signOutText: { fontSize: 15, color: '#9CA3AF', fontWeight: '500' },

  activityCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    paddingVertical: 14,
    paddingHorizontal: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.06,
    shadowRadius: 8,
    elevation: 2,
  },
  activityHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  activityTitle: { fontSize: 13, fontWeight: '700', color: '#111827' },
  activitySeeAll: { fontSize: 12, color: '#10B981', fontWeight: '600' },
  activityRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: '#F3F4F6',
    gap: 10,
  },
  activityIcon: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: '#ECFDF5',
    justifyContent: 'center',
    alignItems: 'center',
  },
  activityBody: { flex: 1 },
  activityLabel: { fontSize: 13, fontWeight: '600', color: '#111827' },
  activityDate: { fontSize: 11, color: '#9CA3AF', marginTop: 2 },
  activityAmounts: { alignItems: 'flex-end' },
  activityDebit: { fontSize: 13, fontWeight: '700', color: '#374151' },
  activityRefund: { fontSize: 11, fontWeight: '600', color: '#10B981', marginTop: 2 },
});
