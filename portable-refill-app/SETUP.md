# Setup Guide

## Prerequisites
- Node.js 18+ installed
- npm or yarn
- iOS Simulator (Mac only) or Android Studio
- Expo Go app on physical device (optional)

---

## Step 1: Install Additional Dependencies

Run the following command in the `New_App/portable-refill-app` directory:

```bash
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns
```

Or with yarn:
```bash
yarn add axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns
```

### Dev Dependencies
```bash
npm install --save-dev @types/node
```

---

## Step 2: Create Environment File

Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

Then edit `.env` with your actual values:
```env
API_BASE_URL=https://your-api-gateway-url.execute-api.us-east-1.amazonaws.com
FIUU_ENV=sandbox
APP_ENV=development
```

---

## Step 3: Start Development Server

```bash
npm start
```

This will open Expo Developer Tools. You can then:
- Press `i` for iOS simulator
- Press `a` for Android emulator
- Scan QR code with Expo Go app on your phone

---

## Step 4: Project Structure Overview

After setup, your project structure should look like this:

```
New_App/portable-refill-app/
├── app/                          # Expo Router screens
│   ├── (auth)/                  # Auth screens (to be created)
│   ├── (main)/                  # Main app screens (to be created)
│   ├── station/                 # Station detail (to be created)
│   ├── payment/                 # Payment screens (to be created)
│   ├── fueling/                 # Fueling screens (to be created)
│   └── _layout.tsx              # Root layout
│
├── src/
│   ├── api/                     # API client layer
│   │   ├── client.ts           # ✅ Created
│   │   ├── auth.ts             # ✅ Created
│   │   ├── stations.ts         # ✅ Created
│   │   ├── payments.ts         # ✅ Created
│   │   ├── dispense.ts         # ✅ Created
│   │   └── transactions.ts     # ✅ Created
│   │
│   ├── types/                   # TypeScript types
│   │   ├── user.ts             # ✅ Created
│   │   ├── station.ts          # ✅ Created
│   │   ├── payment.ts          # ✅ Created
│   │   ├── transaction.ts      # ✅ Created
│   │   ├── alarm.ts            # ✅ Created
│   │   └── api.ts              # ✅ Created
│   │
│   ├── stores/                  # Zustand stores
│   │   ├── useAuthStore.ts     # Next
│   │   └── useFuelingStore.ts  # Next
│   │
│   ├── hooks/                   # Custom hooks
│   │   ├── useAuth.ts          # Next
│   │   └── useStations.ts      # Next
│   │
│   ├── components/              # Reusable components
│   │   └── (to be created)
│   │
│   ├── utils/                   # Utilities
│   │   ├── formatters.ts       # ✅ Created
│   │   ├── validators.ts       # ✅ Created
│   │   └── constants.ts        # ✅ Created
│   │
│   └── config/
│       └── env.ts              # ✅ Created
│
├── .env                         # Environment variables
├── .env.example                # Template
└── package.json
```

---

## Step 5: Development Workflow

1. **Types First**: Define all TypeScript interfaces
2. **API Layer**: Create API client functions
3. **Stores**: Setup global state management
4. **Screens**: Build UI screens one by one
5. **Components**: Extract reusable components
6. **Testing**: Test on both iOS and Android

---

## Step 6: Testing with Mock Backend

If your AWS backend is not ready yet, you can:

1. Set `ENABLE_MOCK_API=true` in `.env`
2. Create mock responses in `src/api/mocks.ts`
3. Use mock data for development

---

## Useful Commands

```bash
# Start development server
npm start

# Clear cache and restart
npm start -- --clear

# Run on iOS
npm run ios

# Run on Android
npm run android

# Type check
npx tsc --noEmit

# Lint
npm run lint

# Build for production (EAS)
npx eas build --platform all
```

---

## Next Steps

1. ✅ Architecture confirmed
2. ✅ Project structure created
3. ✅ Documentation written
4. ✅ Type definitions created
5. ✅ API client layer created
6. ⏭️ **Next**: Create auth store and login screen

---

## Troubleshooting

### Metro Bundler Issues
```bash
npx expo start --clear
```

### iOS Simulator Not Opening
```bash
npx expo run:ios
```

### Android Build Errors
```bash
cd android
./gradlew clean
cd ..
npx expo run:android
```

### TypeScript Errors
```bash
npx tsc --noEmit
```

---

**For questions or issues, refer to**:
- [ARCHITECTURE.md](../ARCHITECTURE.md) - System architecture
- [PROMPTS.md](../PROMPTS.md) - Development history
- [Expo Documentation](https://docs.expo.dev/)
