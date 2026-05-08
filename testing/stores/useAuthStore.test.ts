/**
 * Store tests — useAuthStore.ts
 * Tests Zustand auth store actions including login, register, logout,
 * token persistence, balance operations, and error handling.
 *
 * All external dependencies are mocked.
 */

// ── Mocks ───────────────────────────────────────────────────────────────────
jest.mock('expo-secure-store', () => ({
  getItemAsync: jest.fn(),
  setItemAsync: jest.fn(),
  deleteItemAsync: jest.fn(),
}));

jest.mock('../../portable-refill-app/src/api/auth', () => ({
  login: jest.fn(),
  register: jest.fn(),
  getCurrentUser: jest.fn(),
  logout: jest.fn(),
}));

jest.mock('../../portable-refill-app/src/api/client', () => ({
  post: jest.fn(),
  get: jest.fn(),
}));

import * as SecureStore from 'expo-secure-store';
import * as AuthApi from '../../portable-refill-app/src/api/auth';
import { post } from '../../portable-refill-app/src/api/client';
import { useAuthStore } from '../../portable-refill-app/src/stores/useAuthStore';

const mockSecureGet = SecureStore.getItemAsync as jest.MockedFunction<typeof SecureStore.getItemAsync>;
const mockSecureSet = SecureStore.setItemAsync as jest.MockedFunction<typeof SecureStore.setItemAsync>;
const mockSecureDelete = SecureStore.deleteItemAsync as jest.MockedFunction<typeof SecureStore.deleteItemAsync>;
const mockLogin = AuthApi.login as jest.MockedFunction<typeof AuthApi.login>;
const mockRegister = AuthApi.register as jest.MockedFunction<typeof AuthApi.register>;
const mockPost = post as jest.MockedFunction<typeof post>;

const MOCK_USER = {
  id: 'user-123',
  email: 'driver@interion.com.sg',
  name: 'Ali Hassan',
  phone: '0123456789',
  role: 'DRIVER' as const,
  createdAt: new Date().toISOString(),
};

const MOCK_LOGIN_RESPONSE = {
  user: MOCK_USER,
  token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock',
  refreshToken: 'refresh-mock-token',
  expiresIn: 900,
};

// Reset store state before each test
beforeEach(() => {
  jest.clearAllMocks();
  mockSecureSet.mockResolvedValue(undefined);
  mockSecureDelete.mockResolvedValue(undefined);
  useAuthStore.setState({
    user: null,
    token: null,
    isAuthenticated: false,
    isGuest: false,
    isLoading: false,
    error: null,
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// login
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — login', () => {
  test('sets isLoading true then false on success', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    const loginPromise = useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(useAuthStore.getState().isLoading).toBe(true);
    await loginPromise;
    expect(useAuthStore.getState().isLoading).toBe(false);
  });

  test('sets isAuthenticated to true on success', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(useAuthStore.getState().isAuthenticated).toBe(true);
  });

  test('stores user and token in state', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    const state = useAuthStore.getState();
    expect(state.user).not.toBeNull();
    expect(state.token).toBe(MOCK_LOGIN_RESPONSE.token);
  });

  test('persists token to SecureStore', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(mockSecureSet).toHaveBeenCalledWith('auth_token', MOCK_LOGIN_RESPONSE.token);
  });

  test('sets error and isLoading=false on failure', async () => {
    mockLogin.mockRejectedValue(new Error('Invalid email or password'));
    await expect(useAuthStore.getState().login({ email: 'x@x.com', password: 'wrong' })).rejects.toThrow();
    const state = useAuthStore.getState();
    expect(state.isLoading).toBe(false);
    expect(state.error).toBe('Invalid email or password');
    expect(state.isAuthenticated).toBe(false);
  });

  test('initialises walletBalance to 500 when not provided', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    expect(useAuthStore.getState().user?.walletBalance).toBe(500);
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// register
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — register', () => {
  test('sets isAuthenticated true on success', async () => {
    mockRegister.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().register({ email: 'new@interion.com.sg', password: 'Passw0rd', name: 'New User' });
    expect(useAuthStore.getState().isAuthenticated).toBe(true);
  });

  test('persists token to SecureStore', async () => {
    mockRegister.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().register({ email: 'new@interion.com.sg', password: 'Passw0rd', name: 'New User' });
    expect(mockSecureSet).toHaveBeenCalledWith('auth_token', MOCK_LOGIN_RESPONSE.token);
  });

  test('sets error on failure', async () => {
    mockRegister.mockRejectedValue(new Error('User already exists'));
    await expect(
      useAuthStore.getState().register({ email: 'dup@x.com', password: 'Passw0rd', name: 'Dup' })
    ).rejects.toThrow();
    expect(useAuthStore.getState().error).toBe('User already exists');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// logout
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — logout', () => {
  test('clears user and token from state', async () => {
    // Set logged-in state
    useAuthStore.setState({ user: MOCK_USER as any, token: 'token', isAuthenticated: true });
    await useAuthStore.getState().logout();
    const state = useAuthStore.getState();
    expect(state.user).toBeNull();
    expect(state.token).toBeNull();
    expect(state.isAuthenticated).toBe(false);
  });

  test('deletes token from SecureStore', async () => {
    useAuthStore.setState({ user: MOCK_USER as any, token: 'token', isAuthenticated: true });
    await useAuthStore.getState().logout();
    expect(mockSecureDelete).toHaveBeenCalledWith('auth_token');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// topUpBalance / deductBalance
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — balance operations', () => {
  beforeEach(() => {
    useAuthStore.setState({
      user: { ...MOCK_USER, walletBalance: 100 } as any,
      token: 'token',
      isAuthenticated: true,
    });
  });

  test('topUpBalance increases balance', async () => {
    await useAuthStore.getState().topUpBalance(50);
    expect(useAuthStore.getState().user?.walletBalance).toBe(150);
  });

  test('deductBalance decreases balance', async () => {
    await useAuthStore.getState().deductBalance(30);
    expect(useAuthStore.getState().user?.walletBalance).toBe(70);
  });

  test('deductBalance never goes below 0', async () => {
    await useAuthStore.getState().deductBalance(9999);
    expect(useAuthStore.getState().user?.walletBalance).toBe(0);
  });

  test('topUpBalance does nothing when no user', async () => {
    useAuthStore.setState({ user: null });
    await useAuthStore.getState().topUpBalance(50);
    expect(useAuthStore.getState().user).toBeNull();
  });

  test('deductBalance persists updated user to SecureStore', async () => {
    await useAuthStore.getState().deductBalance(20);
    expect(mockSecureSet).toHaveBeenCalledWith('auth_user', expect.stringContaining('"walletBalance":80'));
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// phoneLogin
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — phoneLogin', () => {
  test('authenticates user on success', async () => {
    mockPost.mockResolvedValue({ token: 'phone-token', user: MOCK_USER, isNewUser: false } as any);
    await useAuthStore.getState().phoneLogin('0123456789', '123456');
    expect(useAuthStore.getState().isAuthenticated).toBe(true);
  });

  test('returns isNewUser flag', async () => {
    mockPost.mockResolvedValue({ token: 'phone-token', user: MOCK_USER, isNewUser: true } as any);
    const result = await useAuthStore.getState().phoneLogin('0123456789', '123456');
    expect(result.isNewUser).toBe(true);
  });

  test('sets error on failed OTP verification', async () => {
    mockPost.mockRejectedValue(new Error('Invalid or expired OTP'));
    await expect(useAuthStore.getState().phoneLogin('0123456789', '000000')).rejects.toThrow();
    expect(useAuthStore.getState().error).toBe('Invalid or expired OTP');
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// clearError
// ─────────────────────────────────────────────────────────────────────────────
describe('useAuthStore — clearError', () => {
  test('resets error to null', () => {
    useAuthStore.setState({ error: 'Some error' });
    useAuthStore.getState().clearError();
    expect(useAuthStore.getState().error).toBeNull();
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// SECURITY
// ─────────────────────────────────────────────────────────────────────────────
describe('SECURITY — token storage', () => {
  test('auth token is stored in SecureStore, not AsyncStorage', async () => {
    mockLogin.mockResolvedValue(MOCK_LOGIN_RESPONSE as any);
    await useAuthStore.getState().login({ email: 'driver@interion.com.sg', password: 'Passw0rd' });
    // Token must go to SecureStore
    expect(mockSecureSet).toHaveBeenCalledWith('auth_token', expect.any(String));
  });

  test('token is cleared from SecureStore on logout', async () => {
    useAuthStore.setState({ user: MOCK_USER as any, token: 'token', isAuthenticated: true });
    await useAuthStore.getState().logout();
    expect(mockSecureDelete).toHaveBeenCalledWith('auth_token');
  });
});
