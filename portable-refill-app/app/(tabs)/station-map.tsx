import { router } from 'expo-router';
import React, { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Linking,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import * as Location from 'expo-location';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { DEMO_STATIONS } from '../../src/data/demoStations';
import { env, isDevelopment } from '../../src/config/env';
import { getNearbyStations } from '../../src/api/stations';

type StationItem = {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  status: string;
  address: string;
  distanceKm?: number;
};

const STATUS_COLOR: Record<string, string> = {
  IDLE: '#10B981',
  DISPENSING: '#3B82F6',
  ALARM: '#F59E0B',
  OFFLINE: '#6B7280',
  MAINTENANCE: '#EF4444',
};

const STATUS_LABEL: Record<string, string> = {
  IDLE: 'Available',
  DISPENSING: 'In Use',
  ALARM: 'Alert',
  OFFLINE: 'Offline',
  MAINTENANCE: 'Maintenance',
};

const STATUS_ICON: Record<string, string> = {
  IDLE: 'checkmark-circle',
  DISPENSING: 'water',
  ALARM: 'warning',
  OFFLINE: 'close-circle',
  MAINTENANCE: 'construct',
};

function haversineKm(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371;
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

export default function StationMapScreen() {
  const insets = useSafeAreaInsets();
  const [stations, setStations] = useState<StationItem[]>([]);
  const [userLocation, setUserLocation] = useState<{ latitude: number; longitude: number } | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => { load(); }, []);

  const useDemoStations = env.enableMockApi || isDevelopment;

  const load = async (isRefresh = false) => {
    if (!isRefresh) setLoading(true);
    const [userLoc] = await Promise.all([getUserLocation(), fetchStations(isRefresh)]);
    if (!isRefresh) setLoading(false);
  };

  const getUserLocation = async () => {
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') return null;
      const loc = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
      const coords = { latitude: loc.coords.latitude, longitude: loc.coords.longitude };
      setUserLocation(coords);
      return coords;
    } catch {
      return null;
    }
  };

  const fetchStations = async (isRefresh = false) => {
    if (useDemoStations) {
      const mapped: StationItem[] = DEMO_STATIONS.map(s => ({
        id: s.id,
        name: s.name,
        latitude: s.location.latitude,
        longitude: s.location.longitude,
        status: s.status,
        address: s.location.address ?? '',
        distanceKm: s.distanceKm,
      }));
      setStations(mapped);
      setRefreshing(false);
      return;
    }

    try {
      const data = await getNearbyStations();
      const mapped: StationItem[] = (data as any[]).map((s: any) => ({
        id: s.id,
        name: s.name,
        latitude: s.location?.latitude ?? 0,
        longitude: s.location?.longitude ?? 0,
        status: s.status ?? 'IDLE',
        address: s.location?.address ?? '',
        distanceKm: s.distanceKm,
      })).filter(s => s.latitude !== 0);
      setStations(mapped);
    } catch {
      const mapped: StationItem[] = DEMO_STATIONS.map(s => ({
        id: s.id,
        name: s.name,
        latitude: s.location.latitude,
        longitude: s.location.longitude,
        status: s.status,
        address: s.location.address ?? '',
        distanceKm: s.distanceKm,
      }));
      setStations(mapped);
    } finally {
      setRefreshing(false);
    }
  };

  // Recalculate distances once we have user location
  const stationsWithDistance: StationItem[] = userLocation
    ? stations.map(s => ({
        ...s,
        distanceKm: haversineKm(userLocation.latitude, userLocation.longitude, s.latitude, s.longitude),
      })).sort((a, b) => (a.distanceKm ?? Infinity) - (b.distanceKm ?? Infinity))
    : stations;

  const openInMaps = (station: StationItem) => {
    const label = encodeURIComponent(station.name);
    const url = `https://www.google.com/maps/dir/?api=1&destination=${station.latitude},${station.longitude}&destination_place_id=${label}`;
    Linking.openURL(url).catch(() => {
      // Fallback to Apple/default maps
      Linking.openURL(`maps://app?daddr=${station.latitude},${station.longitude}`);
    });
  };

  const formatDistance = (km?: number) => {
    if (km === undefined) return null;
    return km < 1 ? `${Math.round(km * 1000)} m` : `${km.toFixed(1)} km`;
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backBtn}>
          <Ionicons name="arrow-back" size={22} color="#111827" />
        </TouchableOpacity>
        <View style={styles.headerCenter}>
          <Text style={styles.title}>Nearby Stations</Text>
          {userLocation && (
            <Text style={styles.subtitle}>Sorted by distance from you</Text>
          )}
        </View>
        <TouchableOpacity
          style={styles.refreshBtn}
          onPress={() => { setRefreshing(true); fetchStations(true); }}
        >
          <Ionicons name="refresh" size={20} color="#6B7280" />
        </TouchableOpacity>
      </View>

      {/* Legend strip */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.legendStrip}
        contentContainerStyle={styles.legendStripContent}
      >
        {Object.entries(STATUS_LABEL).map(([status, label]) => (
          <View key={status} style={[styles.legendChip, { borderColor: STATUS_COLOR[status] + '44', backgroundColor: STATUS_COLOR[status] + '12' }]}>
            <View style={[styles.legendDot, { backgroundColor: STATUS_COLOR[status] }]} />
            <Text style={[styles.legendLabel, { color: STATUS_COLOR[status] }]}>{label}</Text>
          </View>
        ))}
      </ScrollView>

      {/* Station list */}
      {loading ? (
        <View style={styles.loadingBox}>
          <ActivityIndicator size="large" color="#10B981" />
          <Text style={styles.loadingText}>Finding stationsâ€¦</Text>
        </View>
      ) : (
        <ScrollView
          style={styles.list}
          contentContainerStyle={[styles.listContent, { paddingBottom: insets.bottom + 24 }]}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={() => { setRefreshing(true); fetchStations(true); }}
              tintColor="#10B981"
            />
          }
        >
          {stationsWithDistance.length === 0 && (
            <View style={styles.emptyBox}>
              <Ionicons name="location-outline" size={48} color="#D1D5DB" />
              <Text style={styles.emptyText}>No stations found</Text>
            </View>
          )}

          {stationsWithDistance.map(station => {
            const color = STATUS_COLOR[station.status] ?? '#6B7280';
            const label = STATUS_LABEL[station.status] ?? station.status;
            const icon = STATUS_ICON[station.status] ?? 'help-circle';
            const dist = formatDistance(station.distanceKm);

            return (
              <View key={station.id} style={styles.card}>
                {/* Left color bar */}
                <View style={[styles.cardBar, { backgroundColor: color }]} />

                <View style={styles.cardBody}>
                  <View style={styles.cardTop}>
                    <Text style={styles.cardName} numberOfLines={2}>{station.name}</Text>
                    <View style={[styles.statusBadge, { backgroundColor: color + '18' }]}>
                      <Ionicons name={icon as any} size={12} color={color} />
                      <Text style={[styles.statusText, { color }]}>{label}</Text>
                    </View>
                  </View>

                  <View style={styles.cardAddressRow}>
                    <Ionicons name="location-outline" size={13} color="#9CA3AF" />
                    <Text style={styles.cardAddress} numberOfLines={2}>{station.address}</Text>
                  </View>

                  {dist && (
                    <View style={styles.cardDistRow}>
                      <Ionicons name="navigate-outline" size={13} color="#9CA3AF" />
                      <Text style={styles.cardDist}>{dist} away</Text>
                    </View>
                  )}

                  <View style={styles.cardActions}>
                    <TouchableOpacity
                      style={styles.actionBtn}
                      onPress={() => router.push({ pathname: './station-info', params: { stationId: station.id } } as any)}
                    >
                      <Ionicons name="information-circle-outline" size={15} color="#10B981" />
                      <Text style={styles.actionBtnText}>View Details</Text>
                    </TouchableOpacity>

                    <TouchableOpacity
                      style={[styles.actionBtn, styles.actionBtnSecondary]}
                      onPress={() => openInMaps(station)}
                    >
                      <Ionicons name="map-outline" size={15} color="#3B82F6" />
                      <Text style={[styles.actionBtnText, { color: '#3B82F6' }]}>Directions</Text>
                    </TouchableOpacity>
                  </View>
                </View>
              </View>
            );
          })}
        </ScrollView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F8FAFC' },

  header: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
  },
  backBtn: {
    width: 36,
    height: 36,
    borderRadius: 10,
    backgroundColor: '#F3F4F6',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  headerCenter: { flex: 1 },
  title: { fontSize: 17, fontWeight: '700', color: '#111827' },
  subtitle: { fontSize: 12, color: '#9CA3AF', marginTop: 1 },
  refreshBtn: {
    width: 36,
    height: 36,
    borderRadius: 10,
    backgroundColor: '#F3F4F6',
    justifyContent: 'center',
    alignItems: 'center',
    marginLeft: 12,
  },

  legendStrip: { maxHeight: 42, backgroundColor: '#fff', borderBottomWidth: 1, borderBottomColor: '#F3F4F6' },
  legendStripContent: { paddingHorizontal: 16, paddingVertical: 8, gap: 8, flexDirection: 'row' },
  legendChip: { flexDirection: 'row', alignItems: 'center', gap: 5, borderWidth: 1, borderRadius: 20, paddingHorizontal: 10, paddingVertical: 4 },
  legendDot: { width: 7, height: 7, borderRadius: 4 },
  legendLabel: { fontSize: 11, fontWeight: '600' },

  loadingBox: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 12 },
  loadingText: { fontSize: 14, color: '#6B7280' },

  list: { flex: 1 },
  listContent: { paddingHorizontal: 16, paddingTop: 16, gap: 12 },

  emptyBox: { alignItems: 'center', paddingVertical: 48, gap: 12 },
  emptyText: { fontSize: 15, color: '#9CA3AF' },

  card: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderRadius: 16,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.07,
    shadowRadius: 6,
    elevation: 2,
  },
  cardBar: { width: 5 },
  cardBody: { flex: 1, padding: 16 },
  cardTop: { flexDirection: 'row', alignItems: 'flex-start', justifyContent: 'space-between', gap: 8, marginBottom: 8 },
  cardName: { flex: 1, fontSize: 15, fontWeight: '700', color: '#111827' },

  statusBadge: { flexDirection: 'row', alignItems: 'center', gap: 4, borderRadius: 8, paddingHorizontal: 8, paddingVertical: 4 },
  statusText: { fontSize: 11, fontWeight: '700', textTransform: 'uppercase', letterSpacing: 0.4 },

  cardAddressRow: { flexDirection: 'row', alignItems: 'flex-start', gap: 5, marginBottom: 4 },
  cardAddress: { flex: 1, fontSize: 12, color: '#6B7280', lineHeight: 17 },

  cardDistRow: { flexDirection: 'row', alignItems: 'center', gap: 5, marginBottom: 12 },
  cardDist: { fontSize: 12, color: '#9CA3AF', fontWeight: '500' },

  cardActions: { flexDirection: 'row', gap: 10 },
  actionBtn: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 5,
    paddingVertical: 9,
    borderRadius: 10,
    borderWidth: 1.5,
    borderColor: '#10B981',
    backgroundColor: '#ECFDF5',
  },
  actionBtnSecondary: {
    borderColor: '#3B82F6',
    backgroundColor: '#EFF6FF',
  },
  actionBtnText: { fontSize: 13, fontWeight: '600', color: '#10B981' },
});

