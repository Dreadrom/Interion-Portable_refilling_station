/**
 * Authentication API
 */

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
  return post<LoginResponse>(API_ENDPOINTS.AUTH_LOGIN, data);
}

/**
 * Register new user
 */
export async function register(data: RegisterRequest): Promise<LoginResponse> {
  return post<LoginResponse>(API_ENDPOINTS.AUTH_REGISTER, data);
}

/**
 * Get current user profile
 */
export async function getCurrentUser(): Promise<User> {
  return get<User>(API_ENDPOINTS.AUTH_ME);
}

/**
 * Forgot password - Send reset email
 */
export async function forgotPassword(data: ForgotPasswordRequest): Promise<{ message: string }> {
  return post(API_ENDPOINTS.AUTH_FORGOT_PASSWORD, data);
}

/**
 * Reset password with token
 */
export async function resetPassword(data: ResetPasswordRequest): Promise<{ message: string }> {
  return post(API_ENDPOINTS.AUTH_RESET_PASSWORD, data);
}

/**
 * Update user profile
 */
export async function updateProfile(data: UpdateProfileRequest): Promise<User> {
  return post<User>(API_ENDPOINTS.USER_UPDATE, data);
}

/**
 * Change password
 */
export async function changePassword(data: ChangePasswordRequest): Promise<{ message: string }> {
  return post(API_ENDPOINTS.USER_CHANGE_PASSWORD, data);
}

/**
 * Logout (optional - mainly just clear token on client)
 */
export async function logout(): Promise<void> {
  try {
    await post(API_ENDPOINTS.AUTH_LOGOUT, {});
  } catch (error) {
    // Logout endpoint might not exist, that's ok
    console.log('Logout API call failed, continuing with local cleanup');
  }
}
