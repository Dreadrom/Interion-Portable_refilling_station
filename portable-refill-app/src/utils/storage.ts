/**
 * Cross-platform secure storage utility
 * Uses SecureStore on native platforms and localStorage on web
 */

import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

const isWeb = Platform.OS === 'web';

/**
 * Store a key-value pair securely
 */
export async function setItem(key: string, value: string): Promise<void> {
  if (isWeb) {
    try {
      localStorage.setItem(key, value);
    } catch (error) {
      console.error(`Error storing ${key} in localStorage:`, error);
      throw error;
    }
  } else {
    try {
      await SecureStore.setItemAsync(key, value);
    } catch (error) {
      console.error(`Error storing ${key} in SecureStore:`, error);
      throw error;
    }
  }
}

/**
 * Retrieve a value by key
 */
export async function getItem(key: string): Promise<string | null> {
  if (isWeb) {
    try {
      return localStorage.getItem(key);
    } catch (error) {
      console.error(`Error reading ${key} from localStorage:`, error);
      return null;
    }
  } else {
    try {
      return await SecureStore.getItemAsync(key);
    } catch (error) {
      console.error(`Error reading ${key} from SecureStore:`, error);
      return null;
    }
  }
}

/**
 * Delete a key-value pair
 */
export async function deleteItem(key: string): Promise<void> {
  if (isWeb) {
    try {
      localStorage.removeItem(key);
    } catch (error) {
      console.error(`Error deleting ${key} from localStorage:`, error);
      throw error;
    }
  } else {
    try {
      await SecureStore.deleteItemAsync(key);
    } catch (error) {
      console.error(`Error deleting ${key} from SecureStore:`, error);
      throw error;
    }
  }
}
