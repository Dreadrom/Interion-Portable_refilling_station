/** @type {import('jest').Config} */
module.exports = {
  displayName: 'portable-refill-app',
  preset: 'ts-jest',
  testEnvironment: 'node',

  // rootDir is the workspace root so coverage can reach portable-refill-app/src/
  rootDir: '..',
  testMatch: [
    '<rootDir>/testing/**/*.test.ts',
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
    '^expo-secure-store$': '<rootDir>/testing/__mocks__/expo-secure-store.ts',
    '^expo-constants$': '<rootDir>/testing/__mocks__/expo-constants.ts',
    '^expo-crypto$': '<rootDir>/testing/__mocks__/expo-crypto.ts',
    // React Native AsyncStorage
    '^@react-native-async-storage/async-storage$':
      '<rootDir>/testing/__mocks__/@react-native-async-storage/async-storage.ts',
    // Alias for workspace src paths
    '^../../portable-refill-app/(.*)$': '<rootDir>/portable-refill-app/$1',
  },

  // Coverage settings
  collectCoverageFrom: [
    '<rootDir>/portable-refill-app/src/**/*.{ts,tsx}',
    '!<rootDir>/portable-refill-app/src/**/*.d.ts',
    '!<rootDir>/portable-refill-app/src/types/**',
    '!<rootDir>/portable-refill-app/src/**/*.test.{ts,tsx}',
  ],
  coverageDirectory: '<rootDir>/testing/coverage',
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
