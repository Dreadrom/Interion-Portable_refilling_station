# 🛢️ Portable Petrol Station - Cloud-Connected IoT System

**A complete cloud-connected, autonomous portable petrol station system with mobile app, payment integration, and real-time monitoring.**

---

## 📚 Documentation Index

### 🎯 Start Here
1. **[Documentation Hub](docs/README.md)** - Central index for all project documents
2. **[GUIDE_FOR_JQ.md](docs/guides/GUIDE_FOR_JQ.md)** - 👋 **NEW? START HERE!** - Complete onboarding guide
3. **[TODO.md](docs/operations/TODO.md)** - 📋 **Task tracking & progress** - Check what's done and what's next
4. **[PROJECT_SUMMARY.md](docs/guides/PROJECT_SUMMARY.md)** - Complete overview of what's built and what's next
5. **[QUICKSTART.md](portable-refill-app/QUICKSTART.md)** - Get started in 5 minutes
6. **[SETUP.md](portable-refill-app/SETUP.md)** - Detailed setup instructions

### 🏗️ Architecture & Design
7. **[ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md)** - Full system architecture (mobile + cloud + hardware)
8. **[ARCHITECTURE_DIAGRAM.md](docs/architecture/ARCHITECTURE_DIAGRAM.md)** - Visual diagrams and data flows
9. **[PROMPTS.md](docs/operations/PROMPTS.md)** - Development history and version tracking

---

## 🚀 Quick Start

```bash
# 1. Navigate to mobile app
cd portable-refill-app

# 2. Install dependencies
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns @types/node

# 3. Setup environment
copy .env.example .env
# Edit .env with your API endpoint

# 4. Start development server
npm start
```

---

## 📁 Project Structure

```
Interion-Portable_refilling_station/
├── docs/                          # Project documentation
│   ├── README.md                  # Documentation hub
│   ├── architecture/              # Architecture and diagrams
│   ├── backend/                   # Server/database setup notes
│   ├── guides/                    # Onboarding and project summaries
│   ├── operations/                # Progress, prompts, and task tracking
│   └── payments/                  # Payment integration notes
├── portable-refill-app/           # React Native mobile app (iOS + Android)
│   ├── src/
│   │   ├── api/                   # API client layer
│   │   ├── types/                 # TypeScript definitions
│   │   ├── utils/                 # Utilities (formatters, validators)
│   │   ├── config/                # Environment configuration
│   │   ├── stores/                # Zustand stores
│   │   ├── hooks/                 # React Query hooks
│   │   └── components/            # UI components
│   ├── app/                       # Expo Router screens
│   ├── QUICKSTART.md              # Quick start guide
│   └── SETUP.md                   # Setup and troubleshooting
└── README.md                      # This file
```

---

## ✅ Current Status

### Completed (Foundation Phase)
- ✅ Architecture confirmed and documented
- ✅ Complete TypeScript type system
- ✅ API client layer with Axios
- ✅ Utility functions (formatters, validators, constants)
- ✅ Environment configuration
- ✅ Comprehensive documentation

### Next Phase: Authentication & State Management
- Install dependencies
- Create Zustand stores for auth and fueling state
- Build login, registration, and forgot password screens
- Implement JWT token persistence
- Add protected route navigation

---

## 🎯 System Overview

### Mobile App (React Native + Expo)
- **Platform**: iOS + Android
- **Tech**: React Native, Expo, TypeScript
- **State**: Zustand + React Query
- **Navigation**: Expo Router

**Key Features**:
- User authentication (login, register)
- Station discovery (find nearby stations)
- Tank level monitoring
- Payment via Fiuu (QR code)
- Live fueling progress
- Transaction history
- Digital receipts

### Cloud Backend (AWS)
- **MQTT**: AWS IoT Core (hardware communication)
- **API**: API Gateway + Lambda (REST endpoints)
- **Database**: RDS (PostgreSQL/MySQL)
- **Payment**: Fiuu API integration
- **Auth**: JWT tokens (or AWS Cognito)

**Key Capabilities**:
- Real-time hardware communication
- Payment processing and verification
- Transaction management
- Station monitoring
- Alarm handling

### Hardware
- **Device**: Portable petrol station with controller
- **Sensors**: Tank level, flow meter, temperature, safety sensors
- **Actuators**: Pump, valves
- **Communication**: MQTT over TLS to AWS IoT Core

---

## 🔐 Security

- ✅ JWT token authentication
- ✅ Secure token storage (expo-secure-store)
- ✅ HTTPS/TLS for all communications
- ✅ Request/response interceptors
- 🔧 Backend: API rate limiting
- 🔧 Backend: Input validation
- 🔧 Hardware: X.509 certificates for MQTT

---

## 🎓 Technology Stack

### Mobile
- React Native 0.81.5
- Expo ~54.0
- TypeScript ~5.9
- Expo Router ~6.0
- Axios (HTTP client)
- Zustand (global state)
- TanStack Query (server state)
- expo-secure-store (secure storage)

### Backend (AWS)
- AWS IoT Core (MQTT)
- AWS Lambda (serverless functions)
- Amazon API Gateway (REST API)
- Amazon RDS (PostgreSQL/MySQL)
- AWS Cognito (optional auth)

### Payment
- Fiuu Payment Gateway
- QR code payment flow
- Webhook integration

---

## 📊 Features Roadmap

### Phase 1: Authentication ⏭️ NEXT
- [ ] Auth store (Zustand)
- [ ] Login screen
- [ ] Registration screen
- [ ] Forgot password
- [ ] Protected routes

### Phase 2: Station Selection
- [ ] Home screen with station list
- [ ] Station detail screen
- [ ] Tank level visualization
- [ ] Product selection

### Phase 3: Payment Flow
- [ ] Create payment intent
- [ ] Display Fiuu QR code
- [ ] Poll payment status
- [ ] Handle success/failure

### Phase 4: Fueling Process
- [ ] Start fueling command
- [ ] Live progress screen
- [ ] Volume/amount tracking
- [ ] Emergency stop
- [ ] Completion screen

### Phase 5: History & Profile
- [ ] Transaction history
- [ ] Receipt viewer
- [ ] User profile
- [ ] Settings

### Phase 6: Polish
- [ ] Error handling
- [ ] Loading states
- [ ] Animations
- [ ] Offline mode

### Phase 7: Admin Dashboard (Optional)
- [ ] Next.js web app
- [ ] Station monitoring
- [ ] Transaction reports
- [ ] Configuration UI

---

## 🎨 Design Principles

1. **Type Safety First**: 100% TypeScript coverage
2. **Clean Architecture**: Clear separation of concerns
3. **Developer Experience**: Auto-complete, type checking
4. **User Experience**: Smooth, intuitive, fast
5. **Security**: Token-based auth, secure storage
6. **Reliability**: Comprehensive error handling
7. **Performance**: Optimized with caching strategies
8. **Maintainability**: Well-documented, organized code

---

## 📞 Resources

- **Expo Documentation**: https://docs.expo.dev/
- **React Navigation**: https://reactnavigation.org/
- **Zustand**: https://github.com/pmndrs/zustand
- **TanStack Query**: https://tanstack.com/query
- **AWS IoT Core**: https://aws.amazon.com/iot-core/
- **Fiuu Payment**: https://fiuu.com/

---

## 🤝 Development Workflow

### Current State
✅ **Foundation Complete** - All core types, API client, and utilities ready

### Next Steps
1. **Install dependencies** (see QUICKSTART.md)
2. **Create .env file** with API endpoint
3. **Build auth stores** (Zustand)
4. **Create auth screens** (Login, Register)
5. **Implement protected navigation**

### Working Method
- **Types First**: Define interfaces before implementation
- **API Layer**: Create API methods for each feature
- **State Management**: Setup stores and hooks
- **UI Screens**: Build screens using components
- **Testing**: Test on iOS and Android
- **Polish**: Add loading states, errors, animations

---

## 🐛 Troubleshooting

See [SETUP.md](portable-refill-app/SETUP.md) for detailed troubleshooting steps.

Common issues:
- **TypeScript errors**: Check tsconfig.json path configuration
- **Environment variables**: Restart Metro bundler after .env changes
- **Module not found**: Clear cache with `npm start -- --clear`
- **Build errors**: Clean and reinstall node_modules

---

## 📝 License

Proprietary - All rights reserved

---

## 👥 Team

- **Senior Architect**: System design and implementation
- **Mobile Developer**: React Native development
- **Backend Developer**: AWS infrastructure
- **Hardware Engineer**: Station controller firmware

---

**Ready to build?** Start with [QUICKSTART.md](portable-refill-app/QUICKSTART.md)! 🚀

For the full documentation map, see [docs/README.md](docs/README.md).  
For detailed architecture, see [ARCHITECTURE.md](docs/architecture/ARCHITECTURE.md).  
For project status, see [PROJECT_SUMMARY.md](docs/guides/PROJECT_SUMMARY.md).  
For development history, see [PROMPTS.md](docs/operations/PROMPTS.md).  
For task tracking, see [TODO.md](docs/operations/TODO.md).
