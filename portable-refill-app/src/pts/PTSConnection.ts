import AsyncStorage from '@react-native-async-storage/async-storage';

const STORAGE_KEY = 'PTS_CONNECTION_SETTINGS';

export async function connectToPTS(): Promise<void> {
  const raw = await AsyncStorage.getItem(STORAGE_KEY);
  if (!raw) throw new Error('No connection settings');

  const settings = JSON.parse(raw);

  const protocol = settings.protocol === 'HTTPS' ? 'https' : 'http';
  const port =
    protocol === 'https'
      ? settings.httpsPort ?? settings.port
      : settings.httpPort ?? settings.port;

  const baseUrl = `${protocol}://${settings.host}:${port}`;

  const response = await fetch(`${baseUrl}/`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      Protocol: 'jsonPTS',
      Packets: [],
    }),
  });

  if (!response.ok) {
    throw new Error(`PTS connection failed: ${response.status}`);
  }
}
