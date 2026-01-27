import { router } from 'expo-router';
import React, { useState, useEffect } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { CameraView, Camera } from 'expo-camera';
import { globalStyles } from '../styles/globalStyles';

export default function QRScannerScreen() {
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const [scanned, setScanned] = useState(false);

  useEffect(() => {
    const getCameraPermissions = async () => {
      const { status } = await Camera.requestCameraPermissionsAsync();
      setHasPermission(status === 'granted');
    };

    getCameraPermissions();
  }, []);

  const handleBarCodeScanned = ({ type, data }: { type: string; data: string }) => {
    setScanned(true);
    
    console.log(`QR Code scanned: ${data}`);
    
    // Parse QR code data (expecting format: "station-id" or JSON with station info)
    let stationId = data;
    
    try {
      // Try to parse as JSON if it's a complex format
      const parsed = JSON.parse(data);
      if (parsed.stationId) {
        stationId = parsed.stationId;
      }
    } catch (e) {
      // Not JSON, use as-is (assume it's the station ID)
    }

    Alert.alert(
      'QR Code Scanned',
      `Station ID: ${stationId}`,
      [
        {
          text: 'Cancel',
          onPress: () => setScanned(false),
          style: 'cancel',
        },
        {
          text: 'View Station',
          onPress: () => {
            router.push({
              pathname: './station-info',
              params: { stationId },
            });
          },
        },
      ]
    );
  };

  // Test mode function to simulate QR scan without actual camera
  const handleTestMode = () => {
    const testStationId = 'test-station-1';
    Alert.alert(
      'Test Mode',
      `Simulating scan for station: ${testStationId}`,
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'View Station',
          onPress: () => {
            router.push({
              pathname: './station-info',
              params: { stationId: testStationId },
            });
          },
        },
      ]
    );
  };

  if (hasPermission === null) {
    return (
      <View style={styles.container}>
        <Text>Requesting camera permission...</Text>
      </View>
    );
  }

  if (hasPermission === false) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>No access to camera</Text>
        <TouchableOpacity 
          style={[globalStyles.secondaryButton, { marginTop: 20 }]} 
          onPress={() => router.back()}
        >
          <Text style={globalStyles.secondaryButtonText}>Go Back</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <Text style={styles.title}>Scan Station QR Code</Text>
      <Text style={styles.subtitle}>Position the QR code within the frame</Text>

      <View style={styles.cameraContainer}>
        <CameraView
          onBarcodeScanned={scanned ? undefined : handleBarCodeScanned}
          barcodeScannerSettings={{
            barcodeTypes: ['qr', 'pdf417'],
          }}
          style={styles.camera}
        />
        
        <View style={styles.overlay}>
          <View style={styles.unfocusedContainer}></View>
          <View style={styles.middleContainer}>
            <View style={styles.unfocusedContainer}></View>
            <View style={styles.focusedContainer}>
              <View style={[styles.corner, styles.topLeft]} />
              <View style={[styles.corner, styles.topRight]} />
              <View style={[styles.corner, styles.bottomLeft]} />
              <View style={[styles.corner, styles.bottomRight]} />
            </View>
            <View style={styles.unfocusedContainer}></View>
          </View>
          <View style={styles.unfocusedContainer}></View>
        </View>
      </View>

      {scanned && (
        <TouchableOpacity
          style={[globalStyles.primaryButton, { marginTop: 20 }]}
          onPress={() => setScanned(false)}
        >
          <Text style={globalStyles.primaryButtonText}>Tap to Scan Again</Text>
        </TouchableOpacity>
      )}

      <TouchableOpacity
        style={[globalStyles.primaryButton, { marginTop: 16, backgroundColor: '#3B82F6' }]}
        onPress={handleTestMode}
      >
        <Text style={globalStyles.primaryButtonText}>🧪 Test Mode (Skip Scan)</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[globalStyles.secondaryButton, { marginTop: 12, marginBottom: 40 }]}
        onPress={() => router.back()}
      >
        <Text style={globalStyles.secondaryButtonText}>Cancel</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentContainer: {
    paddingTop: 60,
    paddingHorizontal: 24,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 24,
  },
  cameraContainer: {
    width: '100%',
    height: 350,
    borderRadius: 16,
    overflow: 'hidden',
    position: 'relative',
  },
  camera: {
    flex: 1,
  },
  overlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
  },
  unfocusedContainer: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.6)',
  },
  middleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  focusedContainer: {
    width: 250,
    height: 250,
    position: 'relative',
  },
  corner: {
    position: 'absolute',
    width: 40,
    height: 40,
    borderColor: '#10B981',
    borderWidth: 4,
  },
  topLeft: {
    top: 0,
    left: 0,
    borderBottomWidth: 0,
    borderRightWidth: 0,
    borderTopLeftRadius: 8,
  },
  topRight: {
    top: 0,
    right: 0,
    borderBottomWidth: 0,
    borderLeftWidth: 0,
    borderTopRightRadius: 8,
  },
  bottomLeft: {
    bottom: 0,
    left: 0,
    borderTopWidth: 0,
    borderRightWidth: 0,
    borderBottomLeftRadius: 8,
  },
  bottomRight: {
    bottom: 0,
    right: 0,
    borderTopWidth: 0,
    borderLeftWidth: 0,
    borderBottomRightRadius: 8,
  },
  errorText: {
    fontSize: 16,
    color: '#EF4444',
    textAlign: 'center',
  },
});
