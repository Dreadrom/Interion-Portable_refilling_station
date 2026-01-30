/**
 * Authentication API
 */

import { Alert } from 'react-native';
import { post, get } from './client';
import { 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest,
  User,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  UpdateProfileRequest,
  ChangePasswordRequest,
} from '../types';
import { API_ENDPOINTS } from '../utils/constants';

/**
 * Login
 */
export async function login(data: LoginRequest): Promise<LoginResponse> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: login()', `Email: ${data.email}\nPassword: ${data.password}`);
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  // Return mock response
  return {
    token: 'mock-jwt-token-' + Date.now(),
    user: {
      id: '1',
      email: data.email,
      name: 'Test User',
      role: 'DRIVER',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    expiresIn: 3600 // 1 hour 
  };
  
  // TODO: Uncomment when backend is ready
  // return post<LoginResponse>(API_ENDPOINTS.AUTH_LOGIN, data);
}

/**
 * Register new user
 */
export async function register(data: RegisterRequest): Promise<LoginResponse> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: register()', `Email: ${data.email}\nName: ${data.name}`);
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  // Return mock response
  return {
    token: 'mock-jwt-token-' + Date.now(),
    user: {
      id: '1',
      email: data.email,
      name: data.name,
      role: 'DRIVER',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    expiresIn: 3600 // 1 hour
  };
  
  // TODO: Uncomment when backend is ready
  // return post<LoginResponse>(API_ENDPOINTS.AUTH_REGISTER, data);
}

/**
 * Get current user profile
 */
export async function getCurrentUser(): Promise<User> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: getCurrentUser()', 'Fetching user profile...');
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Return mock user data
  return {
    id: '1',
    email: 'testuser@example.com',
    name: 'Test User',
    role: 'DRIVER',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
  
  // TODO: Uncomment when backend is ready
  // return get<User>(API_ENDPOINTS.AUTH_ME);
}

/**
 * Forgot password - Send reset email
 */
export async function forgotPassword(data: ForgotPasswordRequest): Promise<{ message: string }> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: forgotPassword()', `Email: ${data.email}`);
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Return mock response
  return {
    message: `Password reset email sent to ${data.email}`
  };
  
  // TODO: Uncomment when backend is ready
  // return post(API_ENDPOINTS.AUTH_FORGOT_PASSWORD, data);
}

/**
 * Reset password with token
 */
export async function resetPassword(data: ResetPasswordRequest): Promise<{ message: string }> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: resetPassword()', `Token: ${data.token}\nNew Password: ${'*'.repeat(data.newPassword.length)}`);
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Return mock response
  return {
    message: 'Password has been reset successfully'
  };
  
  // TODO: Uncomment when backend is ready
  // return post(API_ENDPOINTS.AUTH_RESET_PASSWORD, data);
}

/**
 * Update user profile
 */
export async function updateProfile(data: UpdateProfileRequest): Promise<User> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: updateProfile()', `Updating profile with: ${JSON.stringify(data)}`);
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Return mock updated user
  return {
    id: '1',
    email: data.email || 'testuser@example.com',
    name: data.name || 'Test User',
    phone: data.phone || '00000000',
    role: 'DRIVER',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
  
  // TODO: Uncomment when backend is ready
  // return post<User>(API_ENDPOINTS.USER_UPDATE, data);
}

/**
 * Change password
 */
export async function changePassword(data: ChangePasswordRequest): Promise<{ message: string }> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: changePassword()', 'Changing password...');
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Return mock response
  return {
    message: 'Password changed successfully'
  };
  
  // TODO: Uncomment when backend is ready
  // return post(API_ENDPOINTS.USER_CHANGE_PASSWORD, data);
}

/**
 * Logout (optional - mainly just clear token on client)
 */
export async function logout(): Promise<void> {
  // TEMPORARY: Mock implementation until backend is ready
  Alert.alert('API Called: logout()', 'Logging out user...');
  
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // TODO: Uncomment when backend is ready
  /*
  try {
    await post(API_ENDPOINTS.AUTH_LOGOUT, {});
  } catch (error) {
    // Logout endpoint might not exist, that's ok
    console.log('Logout API call failed, continuing with local cleanup');
  }
  */
}
