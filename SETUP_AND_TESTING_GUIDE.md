# Fiuu Payment Integration - Setup & Testing Guide

## ✅ Integration Complete

All interfaces are properly configured and the payment flow is ready for testing with Fiuu sandbox.

## 📋 What Was Updated

### Backend Changes

1. **Fixed TypeScript Configuration** ([backend/tsconfig.json](backend/tsconfig.json))
   - Changed `moduleResolution` from `bundler` to `node16` for CommonJS compatibility
   - ✅ Backend now compiles without errors

2. **Payment Handler** ([backend/src/handlers/PaymentHandler.ts](backend/src/handlers/PaymentHandler.ts))
   - Updated `/payment/create` request to accept `stationId`, `method`, `channel`, `description`, `metadata`
   - Updated response to match frontend `CreatePaymentResponse` interface
   - Updated `/payment/:id` response to match frontend `GetPaymentResponse` interface
   - All fields now align with mobile app expectations

3. **Environment Configuration** ([backend/.env.example](backend/.env.example))
   - Added all Fiuu configuration variables
   - Sandbox credentials pre-configured
   - Webhook URL templates included

### Mobile App Changes

1. **Created WebView Payment Screen** ([app/(tabs)/payment-webview.tsx](portable-refill-app/app/(tabs)/payment-webview.tsx))
   - Opens Fiuu hosted payment page in WebView
   - Auto-submits payment form with all required fields
   - Handles payment completion and redirects
   - Polls payment status after completion
   - Supports opening in external browser as fallback

2. **Updated Wallet Top-Up Screen** ([app/(tabs)/top-up-wallet.tsx](portable-refill-app/app/(tabs)/top-up-wallet.tsx))
   - Added multiple payment methods:
     - FPX Online Banking
     - DuitNow QR
     - Touch 'n Go eWallet
     - Boost
     - GrabPay
     - ShopeePay
   - Updated to navigate to WebView screen instead of QR screen
   - Passes `paymentUrl` and `paymentData` to WebView

3. **Added WebView Dependency** ([package.json](portable-refill-app/package.json))
   - Added `react-native-webview@^13.12.5`
   - Required for hosted payment page integration

4. **Payment Types** ([src/types/payment.ts](portable-refill-app/src/types/payment.ts))
   - Already updated with `paymentUrl` and `paymentData` fields
   - Matches backend response structure

## 🚀 Setup Instructions

### 1. Backend Setup

```powershell
cd backend

# Copy environment configuration
Copy-Item .env.example .env

# Install dependencies (if needed)
npm install

# Build TypeScript
npm run build

# Start server
npm run dev
```

Backend will run on http://localhost:3000

### 2. Mobile App Setup

```powershell
cd portable-refill-app

# Install dependencies including new WebView package
npm install

# For iOS (if on Mac)
cd ios
pod install
cd ..

# Start Expo
npm start

# Or run directly on device
npm run android  # For Android
npm run ios      # For iOS
```

### 3. Environment Configuration

Edit `backend/.env`:

```bash
# Database (if needed)
DB_HOST=your-db-host
DB_PORT=5432
DB_USER=your-db-user
DB_PASSWORD=your-db-password
DB_NAME=your-db-name

# JWT (if needed)
JWT_SECRET=your-jwt-secret
JWT_REFRESH_SECRET=your-refresh-secret

# Fiuu Sandbox (already configured)
FIUU_MERCHANT_ID=SB_bluediesel
FIUU_VERIFY_KEY=f90028941214219e6d815fe27efd2937
FIUU_SECRET_KEY=dc66f1d6cd273b828dace4f8ada74dd8
FIUU_SANDBOX=true

# Fiuu Webhook URLs (update with your domain or ngrok URL)
FIUU_RETURN_URL=https://your-domain.com/payment/return
FIUU_CALLBACK_URL=https://your-domain.com/api/payment/fiuu/callback
FIUU_NOTIFICATION_URL=https://your-domain.com/api/payment/fiuu/notify
FIUU_CANCEL_URL=https://your-domain.com/payment/cancel
```

### 4. Database Setup (if not already done)

```powershell
# Run schema to create payment/wallet tables
psql -U your_user -d your_database -f backend/database/complete-schema.sql
```

Tables created:
- `Payments` - stores payment records
- `Wallets` - stores user wallet balances
- `WalletTransactions` - stores wallet transaction audit trail

### 5. Webhook Setup (for local testing)

Install and run ngrok:

```powershell
ngrok http 3000
```

Update `.env` with ngrok URL:

```bash
FIUU_CALLBACK_URL=https://abc123.ngrok.io/payment/fiuu/callback
FIUU_NOTIFICATION_URL=https://abc123.ngrok.io/payment/fiuu/notify
FIUU_RETURN_URL=https://abc123.ngrok.io/payment/fiuu/return
```

## 🧪 Testing the Integration

### Test Flow

1. **Open Mobile App**
   - Login/register a user
   - Navigate to Wallet or Top-Up screen

2. **Select Payment Method**
   - Choose amount (preset or custom)
   - Select payment method:
     - FPX (Online Banking) - easiest to test
     - DuitNow QR
     - E-wallets (TnG, Boost, GrabPay, ShopeePay)

3. **Initiate Payment**
   - Tap "Top-Up" button
   - App calls `POST /payment/create`
   - Backend returns `paymentUrl` and `paymentData`
   - App opens WebView with Fiuu hosted payment page

4. **Complete Payment in WebView**
   - For FPX:
     - Select a bank (e.g., Maybank2u)
     - Enter test credentials:
       - Username: `Gaara`
       - Password: `Letmepaywithsand`
     - Confirm payment
   
   - For Cards (if testing credit channel):
     - Visa: `4229989999000012`, Exp: `12/31`, CVC: `871`
     - Mastercard: `5567630009904309`, Exp: `12/49`, CVC: `433`
     - OTP: `123456`

5. **Payment Completion**
   - After payment, Fiuu redirects to return URL
   - Backend receives webhook callbacks (notify and callback)
   - Backend verifies signature and updates payment status
   - Backend credits wallet
   - App polls `/payment/:id` and gets status
   - App shows success screen

### Manual API Testing

You can also test the API directly:

```powershell
# 1. Login to get JWT token
$response = Invoke-RestMethod -Uri "http://localhost:3000/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"email":"test@example.com","password":"password123"}'

$token = $response.token

# 2. Create payment
$payment = Invoke-RestMethod -Uri "http://localhost:3000/payment/create" `
  -Method POST `
  -Headers @{
    "Content-Type"="application/json"
    "Authorization"="Bearer $token"
  } `
  -Body '{"amount":"10.00","currency":"MYR","method":"FIUU_FPX","channel":"fpx"}'

Write-Host "Payment URL: $($payment.payment.paymentUrl)"

# 3. Check payment status
$status = Invoke-RestMethod -Uri "http://localhost:3000/payment/$($payment.payment.id)" `
  -Method GET `
  -Headers @{"Authorization"="Bearer $token"}

Write-Host "Status: $($status.payment.status)"
```

### Using the Test Script

We've included a quick test script:

```powershell
cd backend

# Set your JWT token
$env:TEST_JWT_TOKEN = "your-jwt-token-here"

# Run test
.\test-fiuu-payment.ps1
```

This will:
- Create a payment
- Generate an HTML form
- Open it in your browser
- Wait for you to complete payment
- Check the status

## 📱 Mobile App Payment Flow

### 1. Top-Up Wallet Screen
- User enters amount
- Selects payment method
- Taps "Top-Up"

### 2. Backend Creates Payment
```json
POST /payment/create
{
  "amount": 10.00,
  "currency": "MYR",
  "method": "FIUU_FPX",
  "stationId": "",
  "description": "Wallet Top-Up"
}

Response:
{
  "payment": {
    "id": "uuid",
    "userId": "user-id",
    "stationId": "",
    "amount": 10.00,
    "currency": "MYR",
    "status": "PENDING",
    "method": "FIUU_FPX",
    "paymentUrl": "https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/fpx.php",
    "paymentData": {
      "amount": "10.00",
      "orderid": "uuid",
      "vcode": "...",
      ...
    },
    "createdAt": "2026-06-08T...",
    "expiresAt": "2026-06-08T..."
  }
}
```

### 3. WebView Opens Fiuu
- Auto-submits form to `paymentUrl` with `paymentData`
- User completes payment in Fiuu hosted page
- Fiuu redirects back

### 4. Backend Receives Webhooks
```
POST /payment/fiuu/callback
POST /payment/fiuu/notify
```
- Verifies `skey` signature
- Updates payment status to `COMPLETED`
- Credits wallet balance
- Records transaction in `WalletTransactions`

### 5. App Polls Status
```json
GET /payment/:id

Response:
{
  "payment": {
    "id": "uuid",
    "status": "SUCCESS",  // Mapped from COMPLETED
    "amount": 10.00,
    "currency": "MYR",
    "completedAt": "2026-06-08T...",
    ...
  }
}
```

### 6. Success Screen
- Shows payment success
- Displays new balance
- Option to view transaction history

## 🔍 Verification Checklist

- [ ] Backend compiles without errors ✅
- [ ] Mobile app compiles without errors ✅
- [ ] Payment types match between backend and frontend ✅
- [ ] `/payment/create` request/response interfaces match ✅
- [ ] `/payment/:id` request/response interfaces match ✅
- [ ] WebView payment screen created ✅
- [ ] Top-up wallet screen updated ✅
- [ ] Multiple payment methods available ✅
- [ ] `react-native-webview` dependency added ✅
- [ ] Database schema supports payment flow ✅
- [ ] Webhook endpoints implemented ✅
- [ ] Signature verification implemented ✅
- [ ] Wallet crediting with idempotency ✅

## 🐛 Troubleshooting

### Backend Issues

**TypeScript compilation fails**
```powershell
cd backend
rm -rf dist node_modules package-lock.json
npm install
npm run build
```

**Port 3000 already in use**
```powershell
# Change PORT in .env
PORT=3001
```

**Database connection fails**
- Check DB credentials in `.env`
- Ensure PostgreSQL is running
- Run schema: `psql -f backend/database/complete-schema.sql`

### Mobile App Issues

**WebView not found error**
```powershell
cd portable-refill-app
npm install react-native-webview
# For iOS
cd ios && pod install && cd ..
```

**Payment URL not opening**
- Check backend is running
- Check API endpoint in `src/utils/constants.ts`
- Verify JWT token is valid

**Webhook not called**
- Use ngrok for local testing
- Register ngrok URL in `.env`
- Check ngrok console for incoming requests

## 📊 Status Mappings

### Backend (Database) → Frontend (App)
- `PENDING` → `PENDING`
- `PROCESSING` → `PENDING`
- `COMPLETED` → `SUCCESS`
- `FAILED` → `FAILED`
- `CANCELLED` → `CANCELLED`
- `REFUNDED` → `REFUNDED`

### Fiuu Response → Backend
- `00` → `COMPLETED`
- `11` → `FAILED`
- `22` → `PENDING`

## 🎯 Next Steps

1. **Test with Sandbox**
   - Run backend and mobile app
   - Complete a full payment flow
   - Verify wallet is credited

2. **Monitor Webhooks**
   - Check backend logs for webhook hits
   - Verify signature validation
   - Check database updates

3. **Production Preparation**
   - Wait for Fiuu production credentials
   - Update `.env` with production keys
   - Set `FIUU_SANDBOX=false`
   - Update webhook URLs to production HTTPS domains
   - Register domains in Fiuu portal

4. **Optional Enhancements**
   - Add payment history screen
   - Implement refund functionality
   - Add status requery for stuck payments
   - Set up reconciliation jobs

## 📚 Documentation

- [Complete Integration Guide](FIUU_INTEGRATION_COMPLETE.md)
- [Official Fiuu API Spec](official API doc provided by user)
- [Backend Payment Handler](backend/src/handlers/PaymentHandler.ts)
- [Mobile Payment Store](portable-refill-app/src/stores/usePaymentStore.ts)

---

**Status**: ✅ Ready for Testing  
**Last Updated**: 2026-06-08  
**Sandbox Ready**: Yes  
**Production Ready**: Pending credentials from Fiuu
