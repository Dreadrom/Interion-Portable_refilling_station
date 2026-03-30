import { router } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import React, { useState, useEffect, useCallback } from 'react';
import {
  ActivityIndicator,
  FlatList,
  RefreshControl,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { transactionStore, TransactionData } from '../../src/utils/transactionStore';

function formatDate(timestamp: number): string {
  return new Date(timestamp).toLocaleDateString('en-MY', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function getStopReasonInfo(reason: string): { label: string; color: string } {
  switch (reason) {
    case 'TARGET_REACHED': return { label: 'Order Fulfilled', color: '#10B981' };
    case 'TANK_FULL':      return { label: 'Tank full', color: '#6366F1' };
    case 'USER_STOPPED':   return { label: 'Stopped by user', color: '#F59E0B' };
    case 'EMERGENCY_STOP': return { label: 'Emergency stop', color: '#EF4444' };
    case 'TIMEOUT':        return { label: 'Timed out', color: '#6B7280' };
    default:               return { label: reason, color: '#6B7280' };
  }
}

function TransactionItem({ item }: { item: TransactionData }) {
  const stopInfo = getStopReasonInfo(item.stopReason);
  // Use stored refundAmount if available; fall back to calculation for old records
  const refund = item.refundAmount != null
    ? item.refundAmount
    : Math.max(0, parseFloat(item.holdAmount.split(' ')[1] || '0') - item.amountCharged);

  return (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.stationName}>{item.stationName}</Text>
        <View style={[styles.reasonBadge, { backgroundColor: stopInfo.color + '20' }]}>
          <Text style={[styles.reasonText, { color: stopInfo.color }]}>{stopInfo.label}</Text>
        </View>
      </View>
      <View style={styles.cardBody}>
        <Text style={styles.detail}>{item.product} · Nozzle {item.nozzle}</Text>
        <Text style={styles.detail}>
          {item.volumeDispensed.toFixed(2)} L @ {item.currency} {item.unitPrice.toFixed(2)}/L · {item.elapsedTime}
        </Text>
      </View>
      <View style={styles.amountRow}>
        <Text style={styles.amount}>{item.currency} {item.amountCharged.toFixed(2)}</Text>
      </View>
      <Text style={styles.timestamp}>{formatDate(item.timestamp)}</Text>
      <Text style={styles.txnId}>{item.id}</Text>
    </View>
  );
}

export default function TransactionHistoryScreen() {
  const insets = useSafeAreaInsets();
  const [transactions, setTransactions] = useState<TransactionData[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadTransactions = useCallback(async () => {
    const list = await transactionStore.getAllTransactions();
    setTransactions(list);
    setLoading(false);
    setRefreshing(false);
  }, []);

  useEffect(() => {
    loadTransactions();
  }, [loadTransactions]);

  const onRefresh = () => {
    setRefreshing(true);
    loadTransactions();
  };

  const totalVolume = transactions.reduce((s, t) => s + t.volumeDispensed, 0);
  const totalSpent = transactions.reduce((s, t) => s + t.amountCharged, 0);
  const currency = transactions[0]?.currency || 'MYR';

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Loading transactions...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={[styles.header, { paddingTop: insets.top + 16 }]}>
        <View style={styles.headerTopRow}>
          <TouchableOpacity onPress={() => router.back()} style={styles.backButton}>
            <Ionicons name="chevron-back" size={22} color="#111827" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>Transaction History</Text>
          <View style={{ width: 44 }} />
        </View>
        {transactions.length > 0 && (
          <View style={styles.summaryBar}>
            <View style={styles.summaryItem}>
              <Text style={styles.summaryValue}>{transactions.length}</Text>
              <Text style={styles.summaryLabel}>Sessions</Text>
            </View>
            <View style={styles.summaryDivider} />
            <View style={styles.summaryItem}>
              <Text style={styles.summaryValue}>{totalVolume.toFixed(1)} L</Text>
              <Text style={styles.summaryLabel}>Total Volume</Text>
            </View>
            <View style={styles.summaryDivider} />
            <View style={styles.summaryItem}>
              <Text style={styles.summaryValue}>{currency} {totalSpent.toFixed(2)}</Text>
              <Text style={styles.summaryLabel}>Total Spent</Text>
            </View>
          </View>
        )}
      </View>

      {transactions.length === 0 ? (
        <View style={styles.centerContainer}>
          <Ionicons name="reader-outline" size={48} color="#9CA3AF" style={{ marginBottom: 12 }} />
          <Text style={styles.emptyTitle}>No Transactions Yet</Text>
          <Text style={styles.emptySubtitle}>Your refueling transactions will appear here.</Text>
          <TouchableOpacity style={styles.homeButton} onPress={() => router.push('/home')}>
            <Text style={styles.homeButtonText}>Go to Home</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <FlatList
          data={transactions}
          keyExtractor={(item) => item.id}
          renderItem={({ item }) => <TransactionItem item={item} />}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  header: {
    backgroundColor: '#fff',
    paddingHorizontal: 20,
    paddingBottom: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  headerTopRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  backButton: {
    marginRight: 8,
    padding: 4,
  },
  headerTitle: {
    flex: 1,
    fontSize: 22,
    fontWeight: '700',
    color: '#111827',
  },
  clearText: {
    fontSize: 14,
    color: '#EF4444',
    fontWeight: '500',
  },
  summaryBar: {
    flexDirection: 'row',
    backgroundColor: '#F3F4F6',
    borderRadius: 12,
    padding: 12,
  },
  summaryItem: {
    flex: 1,
    alignItems: 'center',
  },
  summaryDivider: {
    width: 1,
    backgroundColor: '#D1D5DB',
    marginHorizontal: 8,
  },
  summaryValue: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 2,
  },
  summaryLabel: {
    fontSize: 11,
    color: '#6B7280',
  },
  listContent: {
    padding: 16,
    paddingBottom: 40,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 14,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.07,
    shadowRadius: 4,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  stationName: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
    flex: 1,
    marginRight: 10,
  },
  reasonBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 20,
  },
  reasonText: {
    fontSize: 11,
    fontWeight: '600',
  },
  cardBody: {
    marginBottom: 10,
  },
  detail: {
    fontSize: 13,
    color: '#6B7280',
    marginBottom: 3,
  },
  amountRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 6,
  },
  amount: {
    fontSize: 20,
    fontWeight: '700',
    color: '#111827',
  },
  refundBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#ECFDF5',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 20,
    gap: 4,
  },
  refundText: {
    fontSize: 12,
    fontWeight: '600',
    color: '#059669',
  },
  timestamp: {
    fontSize: 12,
    color: '#9CA3AF',
    marginTop: 2,
  },
  txnId: {
    fontSize: 10,
    color: '#D1D5DB',
    marginTop: 2,
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  loadingText: {
    fontSize: 16,
    color: '#666',
  },
  emptyIcon: {
    fontSize: 48,
    marginBottom: 16,
  },
  emptyTitle: {
    fontSize: 20,
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: 8,
  },
  emptySubtitle: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 24,
  },
  homeButton: {
    backgroundColor: '#10B981',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  homeButtonText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 15,
  },
});
