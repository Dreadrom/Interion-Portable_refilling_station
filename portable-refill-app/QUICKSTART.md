# Quick Start Guide

## 🎯 What We've Built So Far

### ✅ Completed:
1. **Architecture Confirmed** - Production-ready design
2. **Project Structure** - Clean, scalable folder organization
3. **Type Definitions** - Complete TypeScript interfaces
4. **API Client Layer** - Axios with interceptors and error handling
5. **Utilities** - Formatters, validators, constants
6. **Configuration** - Environment setup
7. **Documentation** - Architecture, prompts tracking, setup guide

---

## 📦 Installation Steps

### Step 1: Install Dependencies

Open a terminal in the `portable-refill-app` directory and run:

```bash
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns @types/node
```

Or with yarn:
```bash
yarn add axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns @types/node
```

### Step 2: Create Environment File

Copy the example environment file:
```bash
copy .env.example .env
```

Then edit `.env` with your API endpoint:
```env
API_BASE_URL=https://your-api-gateway.execute-api.region.amazonaws.com
FIUU_ENV=sandbox
APP_ENV=development
```

### Step 3: Update app.json

Add environment variables configuration to `app.json`:

```json
{
  "expo": {
    "extra": {
      "API_BASE_URL": process.env.API_BASE_URL,
      "API_TIMEOUT": process.env.API_TIMEOUT,
      "FIUU_ENV": process.env.FIUU_ENV,
      "APP_ENV": process.env.APP_ENV,
      "ENABLE_MOCK_API": process.env.ENABLE_MOCK_API,
      "ENABLE_DEBUG_LOGGING": process.env.ENABLE_DEBUG_LOGGING,
      "PAYMENT_POLL_INTERVAL": process.env.PAYMENT_POLL_INTERVAL,
      "FUELING_POLL_INTERVAL": process.env.FUELING_POLL_INTERVAL
    }
  }
}
```

### Step 4: Start Development Server

```bash
npm start
```

Then press:
- `i` for iOS simulator
- `a` for Android emulator
- Scan QR code with Expo Go app

---

## 📂 Current Project Structure

```
portable-refill-app/
├── src/
│   ├── api/                    # ✅ API client layer
│   │   ├── client.ts          # Axios instance + interceptors
│   │   ├── auth.ts            # Auth endpoints
│   │   ├── stations.ts        # Station endpoints
│   │   ├── payments.ts        # Payment endpoints
│   │   ├── dispense.ts        # Dispense endpoints
│   │   ├── transactions.ts    # Transaction endpoints
│   │   └── index.ts           # Barrel export
│   │
│   ├── types/                  # ✅ TypeScript definitions
│   │   ├── user.ts
│   │   ├── station.ts
│   │   ├── payment.ts
│   │   ├── transaction.ts
│   │   ├── alarm.ts
│   │   ├── api.ts
│   │   └── index.ts
│   │
│   ├── utils/                  # ✅ Utility functions
│   │   ├── formatters.ts      # Currency, date, volume formatters
│   │   ├── validators.ts      # Form validation
│   │   ├── constants.ts       # App constants
│   │   └── index.ts
│   │
│   ├── config/                 # ✅ Configuration
│   │   └── env.ts             # Environment config
│   │
│   ├── stores/                 # ⏭️ Next: Zustand stores
│   ├── hooks/                  # ⏭️ Next: Custom hooks
│   └── components/             # ⏭️ Next: Reusable components
│
├── app/                        # ⏭️ Next: Screens
├── .env.example               # ✅ Environment template
└── SETUP.md                   # ✅ Setup guide
```

---

## 🎓 Code Examples

### Using the API Client

```typescript
import { authApi } from '@/src/api';

// Login
const handleLogin = async () => {
  try {
    const response = await authApi.login({
      email: 'user@example.com',
      password: 'password123',
    });
    
    console.log('User:', response.user);
    console.log('Token:', response.token);
  } catch (error) {
    console.error('Login failed:', error);
  }
};

// Get stations
import { stationsApi } from '@/src/api';

const fetchStations = async () => {
  try {
    const stations = await stationsApi.getStations({
      latitude: 3.1390,
      longitude: 101.6869,
      radiusKm: 10,
    });
    
    console.log('Nearby stations:', stations);
  } catch (error) {
    console.error('Failed to fetch stations:', error);
  }
};
```

### Using Formatters

```typescript
import { formatCurrency, formatVolume, formatDateTime } from '@/src/utils';

// Format currency
const price = formatCurrency(2.50, 'MYR'); // "MYR 2.50"

// Format volume
const volume = formatVolume(45.50); // "45.50 L"

// Format date
const date = formatDateTime('2026-01-24T10:30:00Z'); // "Jan 24, 2026, 10:30"
```

### Using Validators

```typescript
import { validateLoginForm, isValidEmail } from '@/src/utils';

// Validate login form
const result = validateLoginForm({
  email: 'test@example.com',
  password: 'password123',
});

if (result.isValid) {
  // Proceed with login
} else {
  console.error('Validation errors:', result.errors);
}
```

---

## 🚀 Next Steps

### Phase 1: State Management (Now)
- Create auth store with Zustand
- Create fueling store
- Implement token persistence

### Phase 2: Authentication Screens
- Login screen
- Registration screen
- Forgot password screen

### Phase 3: Main Screens
- Home/Dashboard with station list
- Station detail screen
- Payment QR screen
- Fueling progress screen
- Transaction history

### Phase 4: Components
- Station card
- Tank level visualization
- QR code display
- Progress indicators
- Transaction items

---

## 🐛 Troubleshooting

### TypeScript Errors

If you see TypeScript path resolution errors, make sure your `tsconfig.json` has:

```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["./*"]
    }
  }
}
```

### Environment Variables Not Loading

Make sure you've:
1. Created `.env` file
2. Added `extra` config to `app.json`
3. Restarted the Metro bundler

### Module Not Found Errors

Clear cache and reinstall:
```bash
npm start -- --clear
rm -rf node_modules
npm install
```

---

## 📚 Resources

- [ARCHITECTURE.md](../docs/architecture/ARCHITECTURE.md) - Full system architecture
- [PROMPTS.md](../docs/operations/PROMPTS.md) - Development history
- [SETUP.md](SETUP.md) - Detailed setup guide
- [Expo Documentation](https://docs.expo.dev/)
- [React Navigation](https://reactnavigation.org/)
- [Zustand](https://github.com/pmndrs/zustand)
- [TanStack Query](https://tanstack.com/query)

---

**Ready to continue?** Let me know when you've installed the dependencies and I'll help you build the auth store and login screen! 🚀
