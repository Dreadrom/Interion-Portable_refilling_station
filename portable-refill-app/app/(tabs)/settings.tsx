import AsyncStorage from '@react-native-async-storage/async-storage';
import { router } from 'expo-router';
import { useEffect, useState } from 'react';
import { StyleSheet, Switch, Text, TextInput, TouchableOpacity, View } from 'react-native';

import {
  defaultNetworkSettings,
  NetworkSettings
} from '../network/NetworkSettings';

const STORAGE_KEY = 'PTS_CONNECTION_SETTINGS';

export default function SettingsScreen() {
  const [settings, setSettings] = useState<NetworkSettings>(defaultNetworkSettings);

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    const stored = await AsyncStorage.getItem(STORAGE_KEY);
    if (stored) {
      setSettings(JSON.parse(stored));
    }
  };

  const saveSettings = async () => {
    await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
    router.back();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Network Settings</Text>

      <Text>Host</Text>
      <TextInput
        style={styles.input}
        value={settings.host}
        onChangeText={(v) => setSettings({ ...settings, host: v })}
      />

      <Text>Protocol</Text>
      <View style={styles.row}>
        <TouchableOpacity
          style={[
            styles.protocolButton,
            settings.protocol === 'HTTP' && styles.selected,
          ]}
          onPress={() => setSettings({ ...settings, protocol: 'HTTP' })}
        >
          <Text>HTTP</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            styles.protocolButton,
            settings.protocol === 'HTTPS' && styles.selected,
          ]}
          onPress={() => setSettings({ ...settings, protocol: 'HTTPS' })}
        >
          <Text>HTTPS</Text>
        </TouchableOpacity>
      </View>

      <Text>HTTP Port</Text>
      <TextInput
        style={styles.input}
        keyboardType="numeric"
        value={String(settings.httpPort)}
        onChangeText={(v) =>
          setSettings({ ...settings, httpPort: Number(v) || 0 })
        }
      />

      <Text>HTTPS Port</Text>
      <TextInput
        style={styles.input}
        keyboardType="numeric"
        value={String(settings.httpsPort)}
        onChangeText={(v) =>
          setSettings({ ...settings, httpsPort: Number(v) || 0 })
        }
      />

      <View style={styles.row}>
        <Text>Authentication</Text>
        <Switch
          value={settings.useAuthentication}
          onValueChange={(v) =>
            setSettings({ ...settings, useAuthentication: v })
          }
        />
      </View>

      {settings.useAuthentication && (
        <>
          <Text>Login</Text>
          <TextInput
            style={styles.input}
            value={settings.login}
            onChangeText={(v) => setSettings({ ...settings, login: v })}
          />

          <Text>Password</Text>
          <TextInput
            style={styles.input}
            secureTextEntry
            value={settings.password}
            onChangeText={(v) => setSettings({ ...settings, password: v })}
          />
        </>
      )}

      <TouchableOpacity style={styles.saveButton} onPress={saveSettings}>
        <Text>Save</Text>
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
  header: { fontSize: 18, fontWeight: '700', marginBottom: 20 },
  input: {
    borderWidth: 1,
    borderRadius: 8,
    padding: 10,
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    gap: 10,
  },
  protocolButton: {
    flex: 1,
    borderWidth: 1,
    padding: 10,
    alignItems: 'center',
    borderRadius: 8,
  },
  selected: {
    backgroundColor: '#ddd',
  },
  saveButton: {
    marginTop: 20,
    borderWidth: 1,
    padding: 14,
    alignItems: 'center',
    borderRadius: 10,
  },
});
