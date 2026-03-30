import { create } from 'zustand';
import * as SecureStore from 'expo-secure-store';
import { post, get } from '../api/client';
import { login as apiLogin, register as apiRegister, getCurrentUser } from '../api/auth';
import { User, LoginRequest, RegisterRequest } from '../types';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isGuest: boolean;
  isLoading: boolean;
  error: string | null;
}

interface AuthActions {
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  phoneLogin: (phone: string, otp: string) => Promise<{ isNewUser: boolean }>;
  loginAsGuest: (phone: string, name?: string) => Promise<void>;
  devLogin: () => Promise<void>;
  logout: () => Promise<void>;
  loadStoredAuth: () => Promise<void>;
  clearError: () => void;
  topUpBalance: (amount: number) => Promise<void>;
  deductBalance: (amount: number) => Promise<void>;
  updateUser: (updatedUser: User) => Promise<void>;
}

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export const useAuthStore = create<AuthState & AuthActions>((set, get) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  isGuest: false,
  isLoading: false,
  error: null,

  login: async (data: LoginRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await apiLogin(data);
      const user: User = { walletBalance: 500.00, ...response.user };
      await SecureStore.setItemAsync(TOKEN_KEY, response.token);
      await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));
      set({
        user,
        token: response.token,
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.message || 'Login failed',
      });
      throw error;
    }
  },

  register: async (data: RegisterRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await apiRegister(data);
      const user: User = { walletBalance: 500.00, ...response.user };
      await SecureStore.setItemAsync(TOKEN_KEY, response.token);
      await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));
      set({
        user,
        token: response.token,
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error: any) {
      set({
        isLoading: false,
        error: error.message || 'Registration failed',
      });
      throw error;
    }
  },

  topUpBalance: async (amount: number) => {
    const user = get().user;
    if (!user) return;
    const updatedUser: User = { ...user, walletBalance: (user.walletBalance ?? 0) + amount };
    await SecureStore.setItemAsync(USER_KEY, JSON.stringify(updatedUser));
    set({ user: updatedUser });
  },

  deductBalance: async (amount: number) => {
    const user = get().user;
    if (!user) return;
    const updatedUser: User = { ...user, walletBalance: Math.max((user.walletBalance ?? 0) - amount, 0) };
    await SecureStore.setItemAsync(USER_KEY, JSON.stringify(updatedUser));
    set({ user: updatedUser });
  },

  phoneLogin: async (phone: string, otp: string) => {
    set({ isLoading: true, error: null });
    try {
      const response: any = await post('/auth/verify-otp', { phone, otp });
      const user: User = { walletBalance: response.user.walletBalance ?? 0, ...response.user };
      await SecureStore.setItemAsync(TOKEN_KEY, response.token);
      await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));
      set({ user, token: response.token, isAuthenticated: true, isGuest: false, isLoading: false });
      return { isNewUser: !!response.isNewUser };
    } catch (error: any) {
      set({ isLoading: false, error: error.message || 'Verification failed' });
      throw error;
    }
  },

  loginAsGuest: async (phone: string, name?: string) => {
    set({ isLoading: true, error: null });
    // Guest users get a temporary local session — no backend account created
    const guestUser: User = {
      id: `guest-${Date.now()}`,
      email: '',
      name: name || `Driver (${phone.slice(-4)})`,
      phone,
      role: 'DRIVER',
      createdAt: new Date().toISOString(),
      walletBalance: 0,
    };
    const guestToken = `guest-${Date.now()}-${Math.random().toString(36).substring(2)}`;
    // Don't persist to SecureStore — guest session ends when app closes
    set({ user: guestUser, token: guestToken, isAuthenticated: true, isGuest: true, isLoading: false });
  },

  devLogin: async () => {
    // DEV ONLY — bypasses the backend entirely with a pre-configured test user
    const testUser: User = {
      id: 'dev-test-001',
      email: 'testdriver@interion.dev',
      name: 'Test Driver',
      phone: '+60123456789',
      role: 'DRIVER',
      createdAt: '2026-01-01T00:00:00.000Z',
      walletBalance: 500.00,
    };
    const devToken = `dev-token-${Date.now()}`;
    await SecureStore.setItemAsync(TOKEN_KEY, devToken);
    await SecureStore.setItemAsync(USER_KEY, JSON.stringify(testUser));
    set({ user: testUser, token: devToken, isAuthenticated: true, isGuest: false, isLoading: false, error: null });
  },

  updateUser: async (updatedUser: User) => {
    await SecureStore.setItemAsync(USER_KEY, JSON.stringify(updatedUser));
    set({ user: updatedUser });
  },

  logout: async () => {
    await SecureStore.deleteItemAsync(TOKEN_KEY);
    await SecureStore.deleteItemAsync(USER_KEY);
    set({
      user: null,
      token: null,
      isAuthenticated: false,
      isGuest: false,
      error: null,
    });
  },

  loadStoredAuth: async () => {
    set({ isLoading: true });
    try {
      const token = await SecureStore.getItemAsync(TOKEN_KEY);
      const userJson = await SecureStore.getItemAsync(USER_KEY);
      if (token && userJson) {
        const user = JSON.parse(userJson) as User;
        set({ user, token, isAuthenticated: true, isLoading: false });
      } else {
        set({ isLoading: false });
      }
    } catch {
      set({ isLoading: false });
    }
  },

  clearError: () => set({ error: null }),
}));
