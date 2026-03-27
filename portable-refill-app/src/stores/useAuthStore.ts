import { create } from 'zustand';
import * as SecureStore from 'expo-secure-store';
import { login as apiLogin, register as apiRegister, getCurrentUser } from '../api/auth';
import { User, LoginRequest, RegisterRequest } from '../types';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

interface AuthActions {
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  loadStoredAuth: () => Promise<void>;
  clearError: () => void;
}

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

export const useAuthStore = create<AuthState & AuthActions>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (data: LoginRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await apiLogin(data);
      await SecureStore.setItemAsync(TOKEN_KEY, response.token);
      await SecureStore.setItemAsync(USER_KEY, JSON.stringify(response.user));
      set({
        user: response.user,
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
      await SecureStore.setItemAsync(TOKEN_KEY, response.token);
      await SecureStore.setItemAsync(USER_KEY, JSON.stringify(response.user));
      set({
        user: response.user,
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

  logout: async () => {
    await SecureStore.deleteItemAsync(TOKEN_KEY);
    await SecureStore.deleteItemAsync(USER_KEY);
    set({
      user: null,
      token: null,
      isAuthenticated: false,
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
