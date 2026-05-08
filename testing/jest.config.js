/** @type {import('jest').Config} */
module.exports = {
  displayName: 'portable-refill-app',
  preset: 'ts-jest',
  testEnvironment: 'node',

  // Resolve tests from this testing/ folder only
  rootDir: '.',
  testMatch: [
    '<rootDir>/**/*.test.ts',
  ],

  // Transform TypeScript files
  transform: {
    '^.+\\.tsx?$': [
      'ts-jest',
      {
        tsconfig: {
          jsx: 'react',
          esModuleInterop: true,
          allowSyntheticDefaultImports: true,
          strict: false,
          module: 'commonjs',
          target: 'ES2017',
          moduleResolution: 'node',
        },
      },
    ],
  },

  // Module name mapper to shim native Expo/RN modules that cannot run in Node.js
  moduleNameMapper: {
    // Expo modules
    '^expo-secure-store$': '<rootDir>/__mocks__/expo-secure-store.ts',
    '^expo-constants$': '<rootDir>/__mocks__/expo-constants.ts',
    '^expo-crypto$': '<rootDir>/__mocks__/expo-crypto.ts',
    // React Native AsyncStorage
    '^@react-native-async-storage/async-storage$':
      '<rootDir>/__mocks__/@react-native-async-storage/async-storage.ts',
    // Alias for workspace src paths
    '^../../portable-refill-app/(.*)$': '<rootDir>/../portable-refill-app/$1',
  },

  // Setup file for global mocks
  // setupFilesAfterEach: [],

  // Coverage settings
  collectCoverageFrom: [
    '../portable-refill-app/src/**/*.{ts,tsx}',
    '!../portable-refill-app/src/**/*.d.ts',
    '!../portable-refill-app/src/types/**',
  ],
  coverageDirectory: '<rootDir>/coverage',
  coverageReporters: ['text', 'lcov', 'html'],
  coverageThreshold: {
    global: {
      branches: 60,
      functions: 70,
      lines: 70,
      statements: 70,
    },
  },

  // Verbose output
  verbose: true,

  // Timeout for async tests (ms)
  testTimeout: 15000,
};
