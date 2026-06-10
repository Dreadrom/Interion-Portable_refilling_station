# System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            MOBILE APP (React Native + Expo)                  │
│                                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │   Login/     │  │   Station    │  │   Payment    │  │   Fueling    │   │
│  │   Register   │  │   Selection  │  │   QR Code    │  │   Progress   │   │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                     API Client Layer (Axios)                        │    │
│  │   - Auth API  - Stations API  - Payments API  - Dispense API       │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│                                    │ HTTPS / JWT Token                       │
└────────────────────────────────────┼─────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         AWS CLOUD BACKEND                                    │
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                     Amazon API Gateway (REST)                       │    │
│  │   /auth/login  /stations  /payment/create  /dispense/start         │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│                                    │                                         │
│                                    ▼                                         │
│  ┌────────────────────────────────────────────────────────────────────┐    │
│  │                        AWS Lambda Functions                         │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐          │    │
│  │  │   Auth   │  │ Stations │  │ Payments │  │ Dispense │          │    │
│  │  │ Handler  │  │ Handler  │  │ Handler  │  │ Handler  │          │    │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘          │    │
│  └────────────────────────────────────────────────────────────────────┘    │
│           │                   │                  │                           │
│           │                   │                  └──────────┐                │
│           ▼                   ▼                             ▼                │
│  ┌────────────────┐  ┌─────────────────┐        ┌─────────────────┐       │
│  │   Amazon RDS   │  │  AWS IoT Core   │        │   Fiuu Payment  │       │
│  │  (PostgreSQL)  │◄─┤  (MQTT Broker)  │        │      API        │       │
│  │                │  │                 │        │  (QR Code Gen)  │       │
│  │ • Users        │  │ Pub Topics:     │        └─────────────────┘       │
│  │ • Stations     │  │  status         │                │                   │
│  │ • Transactions │  │  tank           │                │ Webhook          │
│  │ • Payments     │  │  dispense/*     │                │ Callback         │
│  │ • Alarms       │  │  alarms         │                └─────────┐        │
│  └────────────────┘  │                 │                          ▼        │
│                      │ Sub Topics:     │                ┌─────────────────┐│
│                      │  dispense/req   │                │ Webhook Handler ││
│                      │  config/update  │                │    Lambda       ││
│                      └─────────────────┘                └─────────────────┘│
│                              ▲                                              │
│                              │ MQTT over TLS (X.509 cert)                   │
└──────────────────────────────┼──────────────────────────────────────────────┘
                               │
                               │
┌──────────────────────────────┼──────────────────────────────────────────────┐
│                              │         HARDWARE LAYER                        │
│                              ▼                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │              Portable Petrol Station Controller                      │   │
│  │                                                                       │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐             │   │
│  │  │   Sensors    │  │  Controller  │  │  Actuators   │             │   │
│  │  │              │  │   (Gateway)  │  │              │             │   │
│  │  │ • Tank Level │─▶│              │─▶│ • Pump       │             │   │
│  │  │ • Flow Meter │  │ • MQTT Client│  │ • Valves     │             │   │
│  │  │ • Temperature│  │ • Business   │  │ • Indicators │             │   │
│  │  │ • Pressure   │  │   Logic      │  │              │             │   │
│  │  │ • Safety     │  │              │  │              │             │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘             │   │
│  │                                                                       │   │
│  │  Publishes:                    Subscribes:                          │   │
│  │  • station/{id}/status         • station/{id}/dispense/request      │   │
│  │  • station/{id}/tank           • station/{id}/dispense/command      │   │
│  │  • station/{id}/dispense/*     • station/{id}/config/update         │   │
│  │  • station/{id}/alarms                                              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
│                        [Tank] [Pump] [Flow Meter] [Nozzle]                  │
└──────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    OPTIONAL: ADMIN DASHBOARD (Web)                           │
│                                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │  Dashboard   │  │   Stations   │  │ Transactions │  │   Alarms     │   │
│  │   Overview   │  │ Management   │  │   Reports    │  │  Monitoring  │   │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘   │
│                                                                              │
│  Next.js 14 + TypeScript + Tailwind CSS                                     │
│  Deployed on Vercel                                                          │
│  Calls same AWS API Gateway (with ADMIN role)                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Examples

### 1. User Login Flow
```
Mobile App               AWS API Gateway           AWS Lambda              RDS
   │                           │                       │                   │
   │─── POST /auth/login ────▶│                       │                   │
   │    { email, password }    │                       │                   │
   │                           │─── Invoke Lambda ───▶│                   │
   │                           │                       │─── Query User ──▶│
   │                           │                       │◀── User Data ────│
   │                           │                       │                   │
   │                           │                       │ Generate JWT      │
   │◀── JWT Token + User ─────│◀─── Response ────────│                   │
   │                           │                       │                   │
   │ Store token securely      │                       │                   │
   │ (expo-secure-store)       │                       │                   │
```

### 2. Payment & Fueling Flow
```
Mobile App          AWS API          AWS Lambda        Fiuu API         RDS         AWS IoT       Hardware
   │                  │                  │                │              │             │              │
   │ Create Payment   │                  │                │              │             │              │
   │────────────────▶│                  │                │              │              │              │
   │                  │────────────────▶│                │              │              │              │
   │                  │                  │─── Create ───▶│              │              │              │
   │                  │                  │◀── QR Code ───│              │              │              │
   │                  │                  │─── Save ─────────────────▶│              │              │
   │◀── QR Code ─────│◀────────────────│                │              │              │              │
   │                  │                  │                │              │              │              │
   │ Display QR       │                  │                │              │              │              │
   │ User Scans       │                  │                │              │              │              │
   │ User Pays ──────────────────────────────────────────▶              │              │              │
   │                  │                  │                │              │              │              │
   │                  │                  │◀── Webhook ───│              │              │              │
   │                  │                  │─── Update ───────────────▶│              │              │
   │                  │                  │                │              │              │              │
   │ Poll Status      │                  │                │              │              │              │
   │────────────────▶│                  │                │              │              │              │
   │◀── SUCCESS ─────│◀────────────────│◀── Query ───────────────────│              │              │
   │                  │                  │                │              │              │              │
   │ Start Fueling    │                  │                │              │              │              │
   │────────────────▶│────────────────▶│                │              │              │              │
   │                  │                  │─── Publish MQTT ────────────────────────▶│──────────▶│
   │                  │                  │                │              │              │              │
   │                  │                  │                │              │              │  Start Pump  │
   │                  │                  │                │              │              │◀─────────────│
   │                  │                  │                │              │              │  Progress    │
   │                  │                  │◀── MQTT Progress ───────────────────────────│◀─────────────│
   │                  │                  │─── Save ─────────────────▶│              │              │
   │                  │                  │                │              │              │              │
   │ Poll Progress    │                  │                │              │              │              │
   │────────────────▶│────────────────▶│◀── Query ───────────────────│              │              │
   │◀── Progress ────│◀────────────────│                │              │              │              │
   │                  │                  │                │              │              │              │
   │ Display:         │                  │                │              │              │  Complete    │
   │ 45.5L / RM114.75 │                  │                │              │              │◀─────────────│
```

### 3. Real-Time Tank Monitoring
```
Hardware         AWS IoT Core       AWS Lambda        RDS          Mobile App
   │                   │                 │             │                │
   │ Measure Tank      │                 │             │                │
   │ Level: 850L       │                 │             │                │
   │                   │                 │             │                │
   │─── Publish ─────▶│                 │             │                │
   │ tank/status       │                 │             │                │
   │                   │─── IoT Rule ──▶│             │                │
   │                   │                 │─── Save ──▶│                │
   │                   │                 │             │                │
   │                   │                 │             │                │
   │                   │                 │             │◀── Poll ──────│
   │                   │                 │◀── Query ───│                │
   │                   │                 │── Return ──────────────────▶│
   │                   │                 │             │                │
   │                   │                 │             │  Display:      │
   │                   │                 │             │  85% Full      │
   │                   │                 │             │  850L / 1000L  │
```

---

## Security & Authentication Flow

```
┌─────────────┐                          ┌──────────────┐
│  Mobile App │                          │  AWS Backend │
└──────┬──────┘                          └──────┬───────┘
       │                                        │
       │ 1. POST /auth/login                    │
       │    { email, password }                 │
       │───────────────────────────────────────▶│
       │                                        │
       │                                  2. Validate
       │                                     credentials
       │                                        │
       │                                  3. Generate
       │                                     JWT token
       │                                        │
       │ 4. Return JWT + User Info              │
       │◀───────────────────────────────────────│
       │                                        │
 5. Store token in                              │
    expo-secure-store                           │
       │                                        │
       │ 6. Subsequent requests                 │
       │    Authorization: Bearer <token>       │
       │───────────────────────────────────────▶│
       │                                        │
       │                                  7. Verify token
       │                                     in interceptor
       │                                        │
       │                                  8. Process
       │                                     request
       │                                        │
       │ 9. Response                            │
       │◀───────────────────────────────────────│
       │                                        │
       │ (Token expires after N hours)          │
       │                                        │
       │ 10. GET /stations                      │
       │     Authorization: Bearer <expired>    │
       │───────────────────────────────────────▶│
       │                                        │
       │ 11. 401 Unauthorized                   │
       │◀───────────────────────────────────────│
       │                                        │
12. Clear token                                 │
13. Redirect to login                           │
```

---

## Component Architecture (Mobile App)

```
App
├── AuthProvider (Zustand)
│   ├── login()
│   ├── logout()
│   ├── register()
│   └── user state
│
├── QueryClientProvider (React Query)
│   ├── useStations()
│   ├── usePayments()
│   └── cache management
│
└── Navigation (Expo Router)
    │
    ├── (auth) - Unauthenticated Stack
    │   ├── login.tsx
    │   ├── register.tsx
    │   └── forgot-password.tsx
    │
    └── (main) - Authenticated Stack
        ├── _layout.tsx (Tab Navigator)
        │   ├── home.tsx
        │   ├── history.tsx
        │   └── profile.tsx
        │
        ├── station/[id].tsx
        ├── payment/[id].tsx
        └── fueling/[transactionId].tsx
```

---

This diagram shows the complete architecture from hardware to mobile app!
