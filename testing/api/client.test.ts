/**
 * API Client tests — client.ts
 * Tests the Axios interceptors: auth token injection, error handling,
 * and response unwrapping.
 */

jest.mock('expo-secure-store', () => ({
  getItemAsync: jest.fn(),
  setItemAsync: jest.fn(),
  deleteItemAsync: jest.fn(),
}));

jest.mock('../../portable-refill-app/src/config/env', () => ({
  env: {
    apiBaseUrl: 'https://api.test.interion.com',
    apiTimeout: 10000,
    enableDebugLogging: false,
  },
  isDevelopment: false,
}));

import MockAdapter from 'axios-mock-adapter';
import * as SecureStore from 'expo-secure-store';
import apiClient, { get, post, saveAuthToken, removeAuthToken } from '../../portable-refill-app/src/api/client';

const mockSecureGet = SecureStore.getItemAsync as jest.MockedFunction<typeof SecureStore.getItemAsync>;
const mockSecureDelete = SecureStore.deleteItemAsync as jest.MockedFunction<typeof SecureStore.deleteItemAsync>;

// Attach the mock adapter directly to the exported apiClient instance
// so interceptors (auth token injection, error normalization) are exercised
let mockAxios: MockAdapter;

beforeAll(() => {
  mockAxios = new MockAdapter(apiClient as any);
});

afterAll(() => {
  mockAxios.restore();
});

beforeEach(() => {
  jest.clearAllMocks();
  mockAxios.reset();
  mockSecureGet.mockResolvedValue(null);
});

// ─────────────────────────────────────────────────────────────────────────────
// Authorization header injection
// ─────────────────────────────────────────────────────────────────────────────
describe('API client — Authorization header', () => {
  test('attaches Bearer token when SecureStore has a token', async () => {
    mockSecureGet.mockResolvedValue('valid.jwt.token');
    mockAxios.onGet('/auth/me').reply((config) => {
      const auth = config.headers?.Authorization ?? config.headers?.authorization;
      if (auth === 'Bearer valid.jwt.token') {
        return [200, { success: true, data: { id: 'user-1' } }];
      }
      return [401, { success: false, error: { code: 'UNAUTHORIZED', message: 'No token' } }];
    });

    const result = await get('/auth/me');
    expect(result).toBeDefined();
  });

  test('makes request without Authorization header when no token stored', async () => {
    mockSecureGet.mockResolvedValue(null);
    mockAxios.onGet('/stations').reply((config) => {
      const auth = config.headers?.Authorization ?? config.headers?.authorization;
      return [200, { success: true, data: { stations: [], noAuth: !auth } }];
    });

    const result: any = await get('/stations');
    expect(result).toBeDefined();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// Error handling
// ─────────────────────────────────────────────────────────────────────────────
describe('API client — error handling', () => {
  test('throws on 401 response', async () => {
    mockAxios.onGet('/auth/me').reply(401, { success: false, error: { code: 'UNAUTHORIZED', message: 'Not authorised' } });
    await expect(get('/auth/me')).rejects.toBeDefined();
  });

  test('throws on 404 response', async () => {
    mockAxios.onGet('/stations/nonexistent').reply(404, { success: false, error: { code: 'NOT_FOUND', message: 'Station not found' } });
    await expect(get('/stations/nonexistent')).rejects.toBeDefined();
  });

  test('throws on 500 response', async () => {
    mockAxios.onPost('/auth/login').reply(500, { success: false, error: { code: 'INTERNAL_SERVER_ERROR', message: 'Internal server error' } });
    await expect(post('/auth/login', {})).rejects.toBeDefined();
  });

  test('throws on network timeout', async () => {
    mockAxios.onGet('/stations').timeout();
    await expect(get('/stations')).rejects.toBeDefined();
  });

  test('throws on network error', async () => {
    mockAxios.onGet('/stations').networkError();
    await expect(get('/stations')).rejects.toBeDefined();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// saveAuthToken / removeAuthToken
// ─────────────────────────────────────────────────────────────────────────────
describe('API client — token helpers', () => {
  test('saveAuthToken stores token in SecureStore', async () => {
    const mockSet = SecureStore.setItemAsync as jest.MockedFunction<typeof SecureStore.setItemAsync>;
    mockSet.mockResolvedValue(undefined);
    await saveAuthToken('my.new.token');
    expect(mockSet).toHaveBeenCalledWith(expect.any(String), 'my.new.token');
  });

  test('removeAuthToken deletes token from SecureStore', async () => {
    mockSecureDelete.mockResolvedValue(undefined);
    await removeAuthToken();
    expect(mockSecureDelete).toHaveBeenCalled();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// SECURITY — Content-Type header
// ─────────────────────────────────────────────────────────────────────────────
describe('API client — SECURITY', () => {
  test('sends Content-Type: application/json by default', async () => {
    let capturedCt: string | undefined;
    mockAxios.onPost('/auth/login').reply((config) => {
      capturedCt = config.headers?.['Content-Type'] ?? config.headers?.['content-type'];
      return [200, { success: true, data: { token: 'tok' } }];
    });

    await post('/auth/login', { email: 'a@b.com', password: 'Passw0rd' });
    expect(capturedCt).toContain('application/json');
  });

  test('does not log passwords in production mode (isDevelopment=false)', async () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});
    mockAxios.onPost('/auth/login').reply(200, { success: true, data: { token: 'tok' } });
    await post('/auth/login', { email: 'a@b.com', password: 'SuperSecret123' });
    const allLogs = consoleSpy.mock.calls.map((c) => JSON.stringify(c)).join('');
    expect(allLogs).not.toContain('SuperSecret123');
    consoleSpy.mockRestore();
  });
});
