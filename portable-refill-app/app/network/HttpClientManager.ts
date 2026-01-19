import AsyncStorage from '@react-native-async-storage/async-storage';
import { buildDigestAuth } from './DigestAuth';

const STORAGE_KEY = 'PTS_CONNECTION_SETTINGS';

export class HttpClientManager {
  private baseUrl = '';
  private timeoutMs = 5000;
  private authHeader = ''; // store digest auth header after open

  private async fetchWithTimeout(input: RequestInfo, init?: RequestInit): Promise<Response> {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), this.timeoutMs);

    try {
      const response = await fetch(input, { ...init, signal: controller.signal });
      return response;
    } finally {
      clearTimeout(timeout);
    }
  }

  async open(): Promise<void> {
    const raw = await AsyncStorage.getItem(STORAGE_KEY);
    if (!raw) throw new Error('No connection settings');

    const settings = JSON.parse(raw);
    this.baseUrl = this.buildBaseUrl(settings);

    const endpoint = '/jsonPTS';
    const url = `${this.baseUrl}${endpoint}`;

    // STEP 1 — provoke digest challenge
    const first = await fetch(url, { method: 'POST' });

    if (first.status !== 401) {
      throw new Error('Digest auth not requested by server');
    }

    const challenge = first.headers.get('www-authenticate');
    if (!challenge) {
      throw new Error('No digest challenge received');
    }

    // STEP 2 — build digest header
    this.authHeader = await buildDigestAuth(
      'POST',
      endpoint,
      settings.login,
      settings.password,
      challenge
    );

    // STEP 3 — authenticated request
    let response: Response;
    try {
      response = await this.fetchWithTimeout(url, {
        method: 'POST',
        headers: {
          Authorization: this.authHeader,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          Protocol: 'jsonPTS',
          Packets: [],
        }),
      });
    } catch (err) {
      throw new Error(`PTS authenticated request timed out after ${this.timeoutMs}ms`);
    }

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    console.log('[HttpClientManager] Digest connection SUCCESS');
  }

  public async sendJson(payload: any): Promise<any> {
    if (!this.baseUrl) {
      throw new Error('HttpClientManager not open');
    }

    const endpoint = '/jsonPTS';
    const url = `${this.baseUrl}${endpoint}`;

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          Authorization: this.authHeader,
          'Content-Type': 'application/json',
        },
        body: typeof payload === 'string' ? payload : JSON.stringify(payload),
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      console.log('[HttpClientManager] sendJson response:', data);
      return data;
    } catch (err) {
      console.error('[HttpClientManager] sendJson failed', err);
      throw err;
    }
  }

  public close(): void {
    // Currently, nothing persistent to close, but this can reset state if needed
    this.baseUrl = '';
    console.log('[HttpClientManager] Connection CLOSED');
  }

  private buildBaseUrl(settings: any): string {
    const protocol = settings.protocol === 'HTTPS' ? 'https' : 'http';
    const port =
      protocol === 'https'
        ? settings.httpsPort ?? settings.port
        : settings.httpPort ?? settings.port;

    if (!settings.host || !port) {
      throw new Error('Invalid host/port');
    }

    return `${protocol}://${settings.host}:${port}`;
  }
}
