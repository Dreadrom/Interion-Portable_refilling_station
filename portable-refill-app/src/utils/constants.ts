/**
 * Application constants
 */

/**
 * API endpoints
 */
export const API_ENDPOINTS = {
  // Auth
  AUTH_REGISTER: '/auth/register',
  AUTH_LOGIN: '/auth/login',
  AUTH_ME: '/auth/me',
  AUTH_REFRESH: '/auth/refresh',
  AUTH_LOGOUT: '/auth/logout',
  AUTH_FORGOT_PASSWORD: '/auth/forgot-password',
  AUTH_RESET_PASSWORD: '/auth/reset-password',
  
  // User
  USER_PROFILE: '/user/profile',
  USER_UPDATE: '/user/update',
  USER_CHANGE_PASSWORD: '/user/change-password',
  
  // Stations
  STATIONS: '/stations',
  STATION_DETAIL: (id: string) => `/stations/${id}`,
  STATION_TANK: (id: string) => `/stations/${id}/tank`,
  STATION_STATUS: (id: string) => `/stations/${id}/status`,
  
  // Payment
  PAYMENT_CREATE: '/payment/create',
  PAYMENT_DETAIL: (id: string) => `/payment/${id}`,
  PAYMENT_WEBHOOK: '/payment/fiuu/webhook',
  
  // Dispense
  DISPENSE_START: '/dispense/start',
  DISPENSE_STOP: '/dispense/stop',
  DISPENSE_PROGRESS: (id: string) => `/dispense/progress/${id}`,
  
  // Transactions
  TRANSACTIONS: '/transactions',
  TRANSACTION_DETAIL: (id: string) => `/transactions/${id}`,
  
  // Admin
  ADMIN_SUMMARY: '/admin/summary',
  ADMIN_ALARMS: '/admin/alarms',
  ADMIN_STATION_CONFIG: (id: string) => `/admin/stations/${id}/config`,
  ADMIN_STATION_LOCK: (id: string) => `/admin/stations/${id}/lock`,
} as const;

/**
 * Storage keys for AsyncStorage
 */
export const STORAGE_KEYS = {
  AUTH_TOKEN: '@auth_token',
  REFRESH_TOKEN: '@refresh_token',
  USER: '@user',
  LAST_STATION: '@last_station',
  SETTINGS: '@settings',
} as const;

/**
 * Default values
 */
export const DEFAULTS = {
  CURRENCY: 'MYR',
  LOCALE: 'en-MY',
  TIMEZONE: 'Asia/Kuala_Lumpur',
  
  // Polling intervals (milliseconds)
  PAYMENT_POLL_INTERVAL: 2000,
  PAYMENT_POLL_MAX_ATTEMPTS: 60, // 2 minutes
  FUELING_POLL_INTERVAL: 1000,
  
  // Timeouts
  API_TIMEOUT: 30000, // 30 seconds
  PAYMENT_TIMEOUT: 120000, // 2 minutes
  
  // Limits
  MAX_DISPENSE_VOLUME: 100, // litres
  MIN_DISPENSE_VOLUME: 1, // litres
  MAX_DISPENSE_AMOUNT: 500, // MYR
  MIN_DISPENSE_AMOUNT: 5, // MYR
  
  // Map
  DEFAULT_RADIUS_KM: 10,
  DEFAULT_MAP_ZOOM: 13,
} as const;

/**
 * Status colors
 */
export const STATUS_COLORS = {
  // Station status
  IDLE: '#10B981', // green
  DISPENSING: '#3B82F6', // blue
  ALARM: '#EF4444', // red
  OFFLINE: '#6B7280', // gray
  MAINTENANCE: '#F59E0B', // amber
  
  // Payment status
  PENDING: '#F59E0B', // amber
  SUCCESS: '#10B981', // green
  FAILED: '#EF4444', // red
  EXPIRED: '#6B7280', // gray
  CANCELLED: '#6B7280', // gray
  
  // Transaction status
  IN_PROGRESS: '#3B82F6', // blue
  COMPLETED: '#10B981', // green
  STOPPED: '#F59E0B', // amber
  
  // Alarm severity
  CRITICAL: '#EF4444', // red
  WARNING: '#F59E0B', // amber
  INFO: '#3B82F6', // blue
} as const;

/**
 * Animation durations (milliseconds)
 */
export const ANIMATION = {
  FAST: 200,
  NORMAL: 300,
  SLOW: 500,
} as const;

/**
 * Screen names (for navigation)
 */
export const SCREENS = {
  // Auth
  LOGIN: 'login',
  REGISTER: 'register',
  FORGOT_PASSWORD: 'forgot-password',
  
  // Main
  HOME: 'home',
  STATIONS: 'stations',
  HISTORY: 'history',
  PROFILE: 'profile',
  
  // Details
  STATION_DETAIL: 'station/[id]',
  PAYMENT: 'payment/[id]',
  FUELING: 'fueling/[transactionId]',
  TRANSACTION_DETAIL: 'transaction/[id]',
} as const;

/**
 * Error messages
 */
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'Please login to continue.',
  FORBIDDEN: 'You do not have permission to perform this action.',
  NOT_FOUND: 'Resource not found.',
  VALIDATION_ERROR: 'Please check your input.',
  SERVER_ERROR: 'Server error. Please try again later.',
  TIMEOUT: 'Request timeout. Please try again.',
  UNKNOWN_ERROR: 'An unexpected error occurred.',
  
  // Specific errors
  INVALID_CREDENTIALS: 'Invalid email or password.',
  EMAIL_ALREADY_EXISTS: 'Email already registered.',
  PAYMENT_FAILED: 'Payment failed. Please try again.',
  PAYMENT_EXPIRED: 'Payment expired. Please create a new payment.',
  STATION_OFFLINE: 'Station is offline. Please try another station.',
  STATION_MAINTENANCE: 'Station is under maintenance.',
  TANK_EMPTY: 'Tank is empty.',
  DISPENSE_IN_PROGRESS: 'A fueling session is already in progress.',
} as const;

/**
 * Success messages
 */
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Login successful!',
  REGISTER_SUCCESS: 'Registration successful!',
  PAYMENT_SUCCESS: 'Payment successful!',
  DISPENSE_STARTED: 'Fueling started!',
  DISPENSE_COMPLETED: 'Fueling completed!',
  PROFILE_UPDATED: 'Profile updated successfully!',
  PASSWORD_CHANGED: 'Password changed successfully!',
} as const;

/**
 * Product info
 */
export const PRODUCTS = {
  RON95: {
    name: 'RON 95',
    color: '#10B981',
    icon: '⛽',
  },
  RON97: {
    name: 'RON 97',
    color: '#3B82F6',
    icon: '⛽',
  },
  DIESEL: {
    name: 'Diesel',
    color: '#6B7280',
    icon: '⛽',
  },
  PREMIUM_DIESEL: {
    name: 'Premium Diesel',
    color: '#8B5CF6',
    icon: '⛽',
  },
} as const;

/**
 * Regex patterns
 */
export const PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE_MY: /^(01[0-9]{8}|601[0-9]{8})$/,
  PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/,
} as const;
