import { router } from 'expo-router';
import React, { useState, useEffect } from 'react';
import { 
  Alert, 
  ScrollView, 
  StyleSheet, 
  Text, 
  TouchableOpacity, 
  View, 
  ActivityIndicator,
  RefreshControl 
} from 'react-native';
import * as Location from 'expo-location';
import { globalStyles } from '../styles/globalStyles';
import { getStations, getStationById } from '../../src/api/stations';
import { StationDetail, TankStatus } from '../../src/types';

const DEFAULT_RADIUS_KM = 10; // 10km radius

export default function NearbyStationsScreen() {
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [location, setLocation] = useState<Location.LocationObject | null>(null);
  const [stations, setStations] = useState<StationDetail[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getLocationPermission();
  }, []);

  const getLocationPermission = async () => {
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      setHasPermission(status === 'granted');
      
      if (status === 'granted') {
        fetchNearbyStations();
      }
    } catch (err) {
      setError('Failed to request location permission');
      console.error('Permission error:', err);
    }
  };

  const fetchNearbyStations = async () => {
    try {
      setLoading(true);
      setError(null);

      // Get current location
      const currentLocation = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.Balanced,
      });
      setLocation(currentLocation);

      // Fetch stations within radius
      const stationsList = await getStations({
        latitude: currentLocation.coords.latitude,
        longitude: currentLocation.coords.longitude,
        radiusKm: DEFAULT_RADIUS_KM,
      });

      // Fetch detailed information for each station (including tank status)
      const stationsWithDetails = await Promise.all(
        stationsList.map(async (station) => {
          try {
            const detail = await getStationById(station.id);
            // Calculate distance
            const distance = calculateDistance(
              currentLocation.coords.latitude,
              currentLocation.coords.longitude,
              station.location.latitude,
              station.location.longitude
            );
            return { ...detail, distanceKm: distance };
          } catch (err) {
            console.error(`Failed to fetch details for station ${station.id}:`, err);
            return null;
          }
        })
      );

      const validStations = stationsWithDetails.filter((s): s is StationDetail & { distanceKm: number } => s !== null);
      // Sort by distance
      validStations.sort((a, b) => a.distanceKm - b.distanceKm);
      
      setStations(validStations);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch nearby stations');
      console.error('Fetch stations error:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const onRefresh = async () => {
    setRefreshing(true);
    await fetchNearbyStations();
  };

  const calculateDistance = (lat1: number, lon1: number, lat2: number, lon2: number): number => {
    const R = 6371; // Radius of Earth in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  };

  const handleStationPress = (stationId: string) => {
    router.push({
      pathname: './station-info',
      params: { stationId },
    });
  };

  if (hasPermission === null) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Requesting location permission...</Text>
      </View>
    );
  }

  if (hasPermission === false) {
    return (
      <View style={styles.centerContainer}>
        <Text style={styles.errorText}>Location permission denied</Text>
        <Text style={styles.subtitle}>Please enable location access to find nearby stations</Text>
        <TouchableOpacity 
          style={[globalStyles.primaryButton, { marginTop: 20 }]} 
          onPress={getLocationPermission}
        >
          <Text style={globalStyles.primaryButtonText}>Request Permission</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (loading && !refreshing) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#10B981" />
        <Text style={styles.loadingText}>Finding nearby stations...</Text>
      </View>
    );
  }

  return (
    <ScrollView 
      style={styles.container} 
      contentContainerStyle={styles.contentContainer}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} colors={['#10B981']} />
      }
    >
      <Text style={styles.title}>Nearby Stations</Text>
      <Text style={styles.subtitle}>
        {location 
          ? `Showing stations within ${DEFAULT_RADIUS_KM}km of your location`
          : 'Getting your location...'}
      </Text>

      {error && (
        <View style={styles.errorContainer}>
          <Text style={styles.errorText}>{error}</Text>
          <TouchableOpacity 
            style={[globalStyles.primaryButton, { marginTop: 12 }]} 
            onPress={fetchNearbyStations}
          >
            <Text style={globalStyles.primaryButtonText}>Retry</Text>
          </TouchableOpacity>
        </View>
      )}

      {!error && stations.length === 0 && (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>No stations found nearby</Text>
          <Text style={styles.subtitle}>Try increasing your search radius or check back later</Text>
        </View>
      )}

      {stations.map((station) => (
        <TouchableOpacity
          key={station.id}
          style={styles.stationCard}
          onPress={() => handleStationPress(station.id)}
        >
          <View style={styles.stationHeader}>
            <View style={styles.stationInfo}>
              <Text style={styles.stationName}>{station.name}</Text>
              <Text style={styles.stationAddress}>{station.location.address}</Text>
              <View style={styles.distanceContainer}>
                <Text style={styles.distanceText}>
                  📍 {station.distanceKm?.toFixed(1)} km away
                </Text>
                <View style={[styles.statusBadge, getStatusColor(station.status)]}>
                  <Text style={styles.statusText}>{station.status}</Text>
                </View>
              </View>
            </View>
          </View>

          <View style={styles.tanksContainer}>
            <Text style={styles.tanksTitle}>Fuel Availability:</Text>
            {station.tankStatus && station.tankStatus.length > 0 ? (
              station.tankStatus.map((tank) => (
                <View key={tank.id} style={styles.tankRow}>
                  <View style={styles.tankInfo}>
                    <Text style={styles.tankProduct}>{tank.product}</Text>
                    <Text style={styles.tankLevel}>
                      {tank.levelLitres.toLocaleString()} / {tank.capacityLitres.toLocaleString()} L
                    </Text>
                  </View>
                  <View style={styles.tankProgress}>
                    <View 
                      style={[
                        styles.tankProgressBar, 
                        {
                          width: `${(tank.levelLitres / tank.capacityLitres) * 100}%`,
                          backgroundColor: getTankLevelColor(tank.levelLitres, tank.capacityLitres)
                        }
                      ]} 
                    />
                  </View>
                  <Text style={styles.tankPercentage}>
                    {Math.round((tank.levelLitres / tank.capacityLitres) * 100)}%
                  </Text>
                  {tank.lowLevelAlarm && (
                    <Text style={styles.warningText}>⚠️ Low</Text>
                  )}
                </View>
              ))
            ) : (
              <Text style={styles.noDataText}>Tank data not available</Text>
            )}
          </View>
        </TouchableOpacity>
      ))}

      <View style={{ height: 40 }} />
    </ScrollView>
  );
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'IDLE':
      return { backgroundColor: '#10B981' };
    case 'DISPENSING':
      return { backgroundColor: '#3B82F6' };
    case 'ALARM':
      return { backgroundColor: '#EF4444' };
    case 'OFFLINE':
      return { backgroundColor: '#6B7280' };
    case 'MAINTENANCE':
      return { backgroundColor: '#F59E0B' };
    default:
      return { backgroundColor: '#9CA3AF' };
  }
};

const getTankLevelColor = (level: number, capacity: number): string => {
  const percentage = (level / capacity) * 100;
  if (percentage > 50) return '#10B981'; // Green
  if (percentage > 25) return '#F59E0B'; // Orange
  return '#EF4444'; // Red
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  contentContainer: {
    paddingTop: 60,
    paddingHorizontal: 16,
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
    backgroundColor: '#F9FAFB',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 8,
    color: '#111827',
  },
  subtitle: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    marginBottom: 24,
  },
  loadingText: {
    marginTop: 16,
    fontSize: 16,
    color: '#6B7280',
  },
  errorContainer: {
    backgroundColor: '#FEE2E2',
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
  },
  errorText: {
    fontSize: 16,
    color: '#DC2626',
    textAlign: 'center',
  },
  emptyContainer: {
    padding: 32,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 18,
    fontWeight: '600',
    color: '#6B7280',
    marginBottom: 8,
  },
  stationCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 16,
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
  stationHeader: {
    marginBottom: 16,
  },
  stationInfo: {
    flex: 1,
  },
  stationName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  stationAddress: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 8,
  },
  distanceContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  distanceText: {
    fontSize: 14,
    color: '#10B981',
    fontWeight: '600',
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  statusText: {
    fontSize: 12,
    fontWeight: '600',
    color: '#fff',
  },
  tanksContainer: {
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    paddingTop: 12,
  },
  tanksTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#374151',
    marginBottom: 12,
  },
  tankRow: {
    marginBottom: 12,
  },
  tankInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 6,
  },
  tankProduct: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
  },
  tankLevel: {
    fontSize: 13,
    color: '#6B7280',
  },
  tankProgress: {
    height: 8,
    backgroundColor: '#E5E7EB',
    borderRadius: 4,
    overflow: 'hidden',
    marginBottom: 4,
  },
  tankProgressBar: {
    height: '100%',
    borderRadius: 4,
  },
  tankPercentage: {
    fontSize: 12,
    color: '#6B7280',
    textAlign: 'right',
  },
  warningText: {
    fontSize: 12,
    color: '#F59E0B',
    fontWeight: '600',
    marginTop: 4,
  },
  noDataText: {
    fontSize: 14,
    color: '#9CA3AF',
    fontStyle: 'italic',
  },
});
