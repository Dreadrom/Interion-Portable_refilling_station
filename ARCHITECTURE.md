# Portable Petrol Station - Architecture Document

**Date Created**: January 24, 2026  
**Version**: 1.0.0  
**Project**: Cloud-Connected Autonomous Portable Petrol Station

---

## 🎯 System Overview

A complete IoT + Cloud + Mobile solution for autonomous petrol dispensing with real-time monitoring, payment integration, and admin controls.

---

## 🏗️ Architecture Layers

### 1. Hardware Layer
- **Device**: Portable petrol station with tank, pump, flow meter, valves, safety sensors
- **Controller**: Gateway device running embedded software
- **Protocol**: MQTT over TLS to AWS IoT Core
- **Publishing Topics**:
  - `station/{stationId}/status` - Device heartbeat and status
  - `station/{stationId}/tank` - Tank level, temperature, capacity
  - `station/{stationId}/dispense/progress` - Real-time fueling progress
  - `station/{stationId}/dispense/summary` - Completed transaction summary
  - `station/{stationId}/alarms` - Safety alarms and warnings
  
- **Subscribing Topics**:
  - `station/{stationId}/dispense/request` - Start fueling command
  - `station/{stationId}/dispense/command` - Control commands (stop, pause, resume)
  - `station/{stationId}/config/update` - Configuration updates

---

### 2. Cloud Backend (AWS)

#### AWS IoT Core
- **Purpose**: MQTT broker for bidirectional hardware communication
- **Security**: X.509 certificates for device authentication
- **Rules Engine**: Routes messages to Lambda for processing

#### AWS Lambda Functions
- **IoT Message Handler**: Processes MQTT messages and writes to RDS
- **REST API Handlers**: Business logic for mobile/web apps
- **Payment Webhook Handler**: Processes Fiuu payment callbacks
- **Admin Operations**: Station config, pricing, locks

#### Amazon RDS (PostgreSQL/MySQL)
**Database Schema**:

```sql
-- Users and Authentication
users (id, email, name, phone, role, password_hash, created_at)

-- Stations
stations (id, name, location_lat, location_lng, timezone, status, last_heartbeat)

-- Tank Status (real-time)
tank_status (id, station_id, product, level_litres, capacity_litres, temperature_c, 
             low_level_alarm, high_level_alarm, timestamp)

-- Transactions
transactions (id, station_id, user_id, nozzle, product, volume_l, unit_price, 
              total_amount, start_time, end_time, stop_reason, payment_id)

-- Payments
payments (id, user_id, station_id, amount, currency, status, fiuu_payment_id, 
          qr_code_data, created_at, expires_at)

-- Alarms
alarms (id, station_id, code, severity, message, active, triggered_at, cleared_at)

-- Station Configuration
station_config (station_id, max_dispense_volume, max_dispense_amount, 
                maintenance_mode, enabled)

-- Pricing
pricing (id, station_id, product, unit_price, currency, effective_from, effective_to)
```

#### Amazon API Gateway
**REST API Endpoints**:

**Public Endpoints**:
- `POST /auth/register` - User registration
- `POST /auth/login` - User login (returns JWT)
- `GET /auth/me` - Get current user profile

**Station Endpoints**:
- `GET /stations` - List nearby stations
- `GET /stations/{id}` - Get station details
- `GET /stations/{id}/tank` - Get tank status
- `GET /stations/{id}/status` - Get device status

**Payment Endpoints**:
- `POST /payment/create` - Create payment intent with Fiuu
- `GET /payment/{paymentId}` - Check payment status
- `POST /payment/fiuu/webhook` - Fiuu callback (no auth)

**Dispense Endpoints**:
- `POST /dispense/start` - Start fueling (publishes MQTT)
- `POST /dispense/stop` - Stop fueling
- `GET /dispense/progress/{transactionId}` - Get live progress

**Transaction History**:
- `GET /transactions` - User's transaction history
- `GET /transactions/{id}` - Transaction details and receipt

**Admin Endpoints** (requires ADMIN role):
- `GET /admin/summary` - Dashboard summary (revenue, stations, alarms)
- `GET /admin/alarms` - All active alarms
- `PUT /admin/stations/{id}/config` - Update station config
- `PUT /admin/stations/{id}/lock` - Lock/unlock station
- `GET /admin/transactions` - All transactions with filters

#### AWS Cognito (Optional)
- User pools for authentication
- JWT token management
- Can be replaced with custom JWT auth in Lambda

---

### 3. Payment Integration (Fiuu)

#### Flow:
1. **Backend** creates payment intent via Fiuu API
2. **Fiuu** returns payment details + QR code data
3. **Mobile App** displays QR code
4. **User** scans with banking/e-wallet app
5. **User** completes payment in their app
6. **Fiuu** calls our webhook: `POST /payment/fiuu/webhook`
7. **Backend** validates webhook signature, updates payment status
8. **Mobile App** polls `GET /payment/{paymentId}` until SUCCESS
9. **Only then** can fueling start

---

### 4. Mobile App (React Native + Expo)

#### Tech Stack:
- **Framework**: Expo (managed workflow)
- **Language**: TypeScript
- **Navigation**: Expo Router (file-based routing)
- **HTTP Client**: Axios
- **State Management**: 
  - Zustand (global state)
  - React Query / TanStack Query (server state & caching)
- **Auth**: AsyncStorage for JWT token
- **QR Code**: expo-camera or react-native-qrcode-svg
- **Environment**: expo-constants + .env

#### Folder Structure:
```
portable-refill-app/
├── app/                      # Expo Router screens
│   ├── (auth)/              # Auth stack
│   │   ├── login.tsx
│   │   ├── register.tsx
│   │   └── forgot-password.tsx
│   ├── (main)/              # Main app stack
│   │   ├── _layout.tsx      # Tab navigator
│   │   ├── home.tsx         # Dashboard
│   │   ├── stations.tsx     # Station list
│   │   ├── history.tsx      # Transaction history
│   │   └── profile.tsx      # User profile
│   ├── station/
│   │   └── [id].tsx         # Station detail
│   ├── payment/
│   │   └── [id].tsx         # Payment QR screen
│   ├── fueling/
│   │   └── [transactionId].tsx  # Live fueling progress
│   └── _layout.tsx          # Root layout
│
├── src/
│   ├── api/                 # API client layer
│   │   ├── client.ts        # Axios instance + interceptors
│   │   ├── auth.ts          # Auth API calls
│   │   ├── stations.ts      # Station API calls
│   │   ├── payments.ts      # Payment API calls
│   │   ├── dispense.ts      # Dispense API calls
│   │   └── transactions.ts  # Transaction API calls
│   │
│   ├── types/               # TypeScript type definitions
│   │   ├── user.ts
│   │   ├── station.ts
│   │   ├── payment.ts
│   │   ├── transaction.ts
│   │   ├── alarm.ts
│   │   └── api.ts           # API response types
│   │
│   ├── stores/              # Zustand stores
│   │   ├── useAuthStore.ts  # Auth state (user, token)
│   │   ├── useStationStore.ts
│   │   └── useFuelingStore.ts
│   │
│   ├── hooks/               # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useStations.ts   # React Query hooks
│   │   ├── usePayment.ts
│   │   └── useFueling.ts
│   │
│   ├── components/          # Reusable components
│   │   ├── StationCard.tsx
│   │   ├── TankLevel.tsx
│   │   ├── PaymentQR.tsx
│   │   ├── FuelingProgress.tsx
│   │   ├── TransactionItem.tsx
│   │   └── AlarmBadge.tsx
│   │
│   ├── utils/               # Utility functions
│   │   ├── formatters.ts    # Currency, volume, date formatters
│   │   ├── validators.ts    # Form validation
│   │   └── constants.ts     # App constants
│   │
│   └── config/
│       └── env.ts           # Environment configuration
│
├── assets/                  # Images, fonts
├── .env                     # Environment variables
├── .env.example            # Template
├── app.json                # Expo config
├── package.json
└── tsconfig.json
```

#### Key Screens:
1. **Auth Flow**: Login, Register, Forgot Password
2. **Home/Dashboard**: Station list, quick actions
3. **Station Detail**: Tank status, product selection, start flow
4. **Payment QR**: Display QR, poll for payment
5. **Fueling Progress**: Live volume, amount, progress bar
6. **Transaction History**: Past refills with receipts
7. **Profile**: User info, logout

---

### 5. Admin Dashboard (Next.js - Optional)

#### Tech Stack:
- **Framework**: Next.js 14+ (App Router)
- **Language**: TypeScript
- **Deployment**: Vercel
- **UI**: Tailwind CSS + shadcn/ui
- **Charts**: Recharts or Chart.js
- **Auth**: NextAuth.js or custom JWT

#### Folder Structure:
```
admin-dashboard/
├── app/
│   ├── (auth)/
│   │   └── login/
│   ├── (dashboard)/
│   │   ├── layout.tsx
│   │   ├── page.tsx         # Overview
│   │   ├── stations/
│   │   ├── transactions/
│   │   ├── alarms/
│   │   └── settings/
│   └── api/                 # API routes (proxies to AWS)
│
├── src/
│   ├── components/
│   ├── lib/
│   └── types/
│
├── .env.local
└── next.config.js
```

#### Key Features:
- Real-time station status dashboard
- Tank level monitoring
- Alarm management
- Transaction reports and analytics
- Station configuration (pricing, limits)
- Lock/unlock stations
- Revenue analytics

---

## 🔐 Security Considerations

1. **API Authentication**: JWT tokens (Bearer auth)
2. **Device Authentication**: X.509 certificates for MQTT
3. **Payment Webhook**: Signature verification (Fiuu provides)
4. **Rate Limiting**: API Gateway throttling
5. **Data Encryption**: TLS everywhere, encrypted RDS
6. **Role-Based Access**: ADMIN vs DRIVER permissions

---

## 🚀 Deployment Strategy

### Mobile App:
- **Development**: Expo Go app for testing
- **Staging**: Expo EAS Build (internal distribution)
- **Production**: App Store + Google Play via EAS Submit

### Admin Dashboard:
- **Development**: `npm run dev`
- **Staging**: Vercel preview deployments
- **Production**: Vercel production domain

### Backend:
- **Infrastructure as Code**: AWS CDK or CloudFormation
- **CI/CD**: GitHub Actions → AWS Lambda deployment
- **Environments**: dev, staging, production

---

## 📊 Data Flow Examples

### Fueling Flow:
1. User opens app → sees nearby stations
2. User selects station → sees tank levels and products
3. User selects product + amount → creates payment
4. Backend calls Fiuu API → returns QR code
5. User scans QR in banking app → pays
6. Fiuu webhook hits backend → updates payment status
7. App polls payment status → sees SUCCESS
8. User taps "Start Fueling" → app calls `POST /dispense/start`
9. Backend publishes MQTT to `station/{id}/dispense/request`
10. Hardware receives command → starts pump
11. Hardware publishes progress to `station/{id}/dispense/progress`
12. IoT Rule → Lambda → writes to RDS
13. App polls `GET /dispense/progress/{id}` → shows live litres
14. Fueling completes → hardware publishes summary
15. App shows receipt

---

## 🔄 Future Enhancements

- Push notifications for fueling complete
- Loyalty points and rewards
- Multiple payment methods (credit card, e-wallet)
- Scheduled refills
- Fleet management for businesses
- Predictive maintenance based on sensor data
- Multi-language support
- Dark mode

---

## 📞 Support & Maintenance

- **Monitoring**: AWS CloudWatch for Lambda, IoT metrics
- **Logging**: CloudWatch Logs
- **Alerting**: SNS for critical alarms
- **Backup**: RDS automated backups
- **Updates**: OTA updates via Expo

---

**Document Version History**:
- v1.0.0 (2026-01-24): Initial architecture document
