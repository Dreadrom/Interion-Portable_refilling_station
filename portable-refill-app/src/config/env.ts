/**
 * Environment configuration
 */

import Constants from 'expo-constants';

interface EnvConfig {
  apiBaseUrl: string;
  apiTimeout: number;
  fiuuEnv: 'sandbox' | 'production';
  appEnv: 'development' | 'staging' | 'production';
  enableMockApi: boolean;
  enableDebugLogging: boolean;
  paymentPollInterval: number;
  fuelingPollInterval: number;
}

/**
 * Get environment variable with fallback
 */
function getEnvVar(key: string, defaultValue: string = ''): string {
  return Constants.expoConfig?.extra?.[key] || process.env[key] || defaultValue;
}

/**
 * Environment configuration object
 */
export const env: EnvConfig = {
  apiBaseUrl: getEnvVar('API_BASE_URL', 'https://api.example.com'),
  apiTimeout: parseInt(getEnvVar('API_TIMEOUT', '30000'), 10),
  fiuuEnv: getEnvVar('FIUU_ENV', 'sandbox') as 'sandbox' | 'production',
  appEnv: getEnvVar('APP_ENV', 'development') as 'development' | 'staging' | 'production',
  enableMockApi: getEnvVar('ENABLE_MOCK_API', 'false') === 'true',
  enableDebugLogging: getEnvVar('ENABLE_DEBUG_LOGGING', 'true') === 'true',
  paymentPollInterval: parseInt(getEnvVar('PAYMENT_POLL_INTERVAL', '2000'), 10),
  fuelingPollInterval: parseInt(getEnvVar('FUELING_POLL_INTERVAL', '1000'), 10),
};

/**
 * Check if running in development mode
 */
export const isDevelopment = env.appEnv === 'development';

/**
 * Check if running in production mode
 */
export const isProduction = env.appEnv === 'production';

/**
 * Log configuration on app start (only in development)
 */
if (isDevelopment && env.enableDebugLogging) {
  console.log('📱 App Configuration:', {
    appEnv: env.appEnv,
    apiBaseUrl: env.apiBaseUrl,
    fiuuEnv: env.fiuuEnv,
    enableMockApi: env.enableMockApi,
  });
}
