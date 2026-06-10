# Fiuu Payment Integration - Complete Implementation

## Overview

The Fiuu payment integration is now complete and ready for sandbox testing. This implementation uses the **Hosted Payment Page** approach, where users are redirected to Fiuu's payment page to complete transactions.

## What's Implemented

### Backend Components

#### 1. **Crypto Utilities** ([backend/src/utils/crypto.ts](backend/src/utils/crypto.ts))
- MD5 hashing for Fiuu signatures
- `generateFiuuVcode()` - Generate payment request signature
- `verifyFiuuSkey()` - Verify payment response signature

#### 2. **Payment Handler** ([backend/src/handlers/PaymentHandler.ts](backend/src/handlers/PaymentHandler.ts))
Complete payment flow handler with these endpoints:

**POST /payment/create**
- Creates payment record in database
- Generates Fiuu vcode signature
- Returns payment URL and form data
- Requires authentication

**GET /payment/:id**
- Returns payment status
- Maps internal status to frontend status
- Requires authentication

**POST /payment/fiuu/return**
- Handles browser redirect after payment
- Verifies skey signature
- Updates payment status
- Returns HTML page with result

**POST /payment/fiuu/notify**
- Handles Fiuu IPN notification webhook
- Verifies skey signature
- Updates payment and credits wallet
- More reliable than return URL

**POST /payment/fiuu/callback**
- Handles Fiuu callback webhook
- Verifies skey signature
- Updates payment and credits wallet
- Returns `CBTOKEN:MPSTATOK` acknowledgment

#### 3. **Wallet Credit Logic**
- Idempotent wallet crediting (prevents double crediting)
- Transaction-based updates using PostgreSQL
- Creates `WalletTransactions` audit trail
- Auto-creates wallet if doesn't exist

### Frontend Updates

#### Updated Types ([portable-refill-app/src/types/payment.ts](portable-refill-app/src/types/payment.ts))
- Added `paymentUrl?: string` - Fiuu hosted page URL
- Added `paymentData?: Record<string, string>` - Payment form data

## Configuration

### Environment Variables (.env)

Copy from [.env.example](backend/.env.example) and update:

```bash
# Fiuu Payment Gateway Configuration
FIUU_MERCHANT_ID=SB_bluediesel
FIUU_VERIFY_KEY=f90028941214219e6d815fe27efd2937
FIUU_SECRET_KEY=dc66f1d6cd273b828dace4f8ada74dd8
FIUU_SANDBOX=true

# Fiuu Webhook URLs - must be publicly accessible HTTPS
FIUU_RETURN_URL=https://your-domain.com/payment/return
FIUU_CALLBACK_URL=https://your-domain.com/api/payment/fiuu/callback
FIUU_NOTIFICATION_URL=https://your-domain.com/api/payment/fiuu/notify
FIUU_CANCEL_URL=https://your-domain.com/payment/cancel
```

### Webhook URL Requirements

⚠️ **Important**: Fiuu requires webhook URLs to be:
1. Publicly accessible over HTTPS
2. Registered in Fiuu merchant portal
3. Return proper responses (see response format below)

For local development, use ngrok or similar:
```bash
ngrok http 3000
```

Then update your `.env` with the ngrok URL:
```bash
FIUU_CALLBACK_URL=https://abc123.ngrok.io/payment/fiuu/callback
FIUU_NOTIFICATION_URL=https://abc123.ngrok.io/payment/fiuu/notify
FIUU_RETURN_URL=https://abc123.ngrok.io/payment/fiuu/return
```

## Testing with Sandbox

### Sandbox Environment

**Portal**: https://sandbox-portal.fiuu.com
**Payment URL**: https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/
**Bank Simulator**: https://bank-simulator.fiuu.com

### Test Credentials

#### FPX Online Banking
- Username: `Gaara`
- Password: `Letmepaywithsand`

#### Credit Card - Visa
- Card Number: `4229989999000012`
- Expiry: `12/31`
- CVC: `871`
- OTP: `123456`

#### Credit Card - Mastercard
- Card Number: `5567630009904309`
- Expiry: `12/49`
- CVC: `433`
- OTP: `123456`

### Payment Channels

Append channel to payment URL or pass in `channel` parameter:

| Channel Code | Payment Method |
|--------------|----------------|
| `fpx` | FPX Online Banking (all banks) |
| `credit` | Visa/Mastercard |
| `RPP_DuitNowQR` | DuitNow QR |
| `TNG-EWALLET` | Touch 'n Go eWallet |
| `BOOST` | Boost |
| `GrabPay` | GrabPay |
| `ShopeePay` | ShopeePay |

Example:
```
https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/fpx.php
https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/credit.php
```

### Test Flow

#### 1. Start Backend Server

```bash
cd backend
npm run dev
```

Backend will run on http://localhost:3000

#### 2. Create a Payment

**Request:**
```bash
curl -X POST http://localhost:3000/payment/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": "10.00",
    "currency": "MYR",
    "paymentMethod": "FIUU_FPX",
    "channel": "fpx"
  }'
```

**Response:**
```json
{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "amount": "10.00",
  "currency": "MYR",
  "paymentUrl": "https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/fpx.php",
  "paymentData": {
    "amount": "10.00",
    "orderid": "550e8400-e29b-41d4-a716-446655440000",
    "bill_name": "John Doe",
    "bill_email": "john@example.com",
    "bill_mobile": "60123456789",
    "bill_desc": "Wallet Top Up",
    "country": "MY",
    "currency": "MYR",
    "vcode": "a1b2c3d4e5f6...",
    "returnurl": "https://your-domain.com/payment/return",
    "callbackurl": "https://your-domain.com/api/payment/fiuu/callback",
    "notificationurl": "https://your-domain.com/api/payment/fiuu/notify",
    "cancelurl": "https://your-domain.com/payment/cancel",
    "channel": "fpx"
  }
}
```

#### 3. Submit Payment Form

Open the `paymentUrl` in browser and POST the `paymentData` as form fields, or construct an HTML form:

```html
<form method="POST" action="https://sandbox-payment.fiuu.com/RMS/pay/SB_bluediesel/fpx.php">
  <input type="hidden" name="amount" value="10.00">
  <input type="hidden" name="orderid" value="550e8400-e29b-41d4-a716-446655440000">
  <input type="hidden" name="bill_name" value="John Doe">
  <input type="hidden" name="bill_email" value="john@example.com">
  <input type="hidden" name="bill_mobile" value="60123456789">
  <input type="hidden" name="bill_desc" value="Wallet Top Up">
  <input type="hidden" name="country" value="MY">
  <input type="hidden" name="currency" value="MYR">
  <input type="hidden" name="vcode" value="a1b2c3d4e5f6...">
  <input type="hidden" name="returnurl" value="https://your-domain.com/payment/return">
  <input type="hidden" name="callbackurl" value="https://your-domain.com/api/payment/fiuu/callback">
  <input type="hidden" name="notificationurl" value="https://your-domain.com/api/payment/fiuu/notify">
  <input type="hidden" name="cancelurl" value="https://your-domain.com/payment/cancel">
  <input type="hidden" name="channel" value="fpx">
  <button type="submit">Pay Now</button>
</form>
```

#### 4. Complete Payment in Fiuu Simulator

- Select FPX bank (e.g., Maybank2u)
- Enter test credentials:
  - Username: `Gaara`
  - Password: `Letmepaywithsand`
- Confirm payment

#### 5. Verify Webhook Calls

Check backend logs for webhook hits:

```
[PaymentHandler] Callback URL hit: { orderid: '550e8400...', tranID: 'FIUU123456', status: '00' }
[PaymentHandler] Payment updated: 550e8400... COMPLETED
[PaymentHandler] Wallet credited: { userId: 'abc123', amount: '10.00', newBalance: '110.00' }
```

#### 6. Check Payment Status

```bash
curl -X GET http://localhost:3000/payment/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SUCCESS",
  "amount": "10.00",
  "currency": "MYR",
  "gatewayTransactionId": "FIUU123456",
  "paymentMethod": "FIUU_FPX",
  "createdAt": "2026-06-08T10:00:00Z",
  "completedAt": "2026-06-08T10:05:00Z",
  "metadata": {
    "fiuuResponse": {
      "tranID": "FIUU123456",
      "status": "00",
      "channel": "fpx",
      ...
    }
  }
}
```

## Status Mapping

### Fiuu Status Codes
- `00` = Successful payment
- `11` = Failed payment
- `22` = Pending payment

### Internal Status (Database)
- `PENDING` - Payment created, awaiting completion
- `PROCESSING` - Payment in progress
- `COMPLETED` - Payment successful
- `FAILED` - Payment failed
- `CANCELLED` - Payment cancelled
- `REFUNDED` - Payment refunded

### Frontend Status (API Response)
- `PENDING` - Waiting for payment
- `SUCCESS` - Payment successful
- `FAILED` - Payment failed
- `CANCELLED` - Payment cancelled
- `EXPIRED` - Payment expired
- `REFUNDED` - Payment refunded

## Database Schema

The integration uses these tables (already in [complete-schema.sql](backend/database/complete-schema.sql)):

### Payments
```sql
CREATE TABLE Payments (
    PaymentID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) NOT NULL,
    Amount NUMERIC(10, 2) NOT NULL,
    Currency VARCHAR(3) DEFAULT 'MYR',
    GatewayTransactionID VARCHAR(255),
    GatewayName VARCHAR(50) DEFAULT 'FIUU',
    Status VARCHAR(20) DEFAULT 'PENDING',
    PaymentMethod VARCHAR(50),
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CompletedAt TIMESTAMP,
    Metadata JSONB
);
```

### Wallets
```sql
CREATE TABLE Wallets (
    WalletID VARCHAR(36) PRIMARY KEY,
    UserID VARCHAR(36) UNIQUE NOT NULL,
    Balance NUMERIC(10, 2) DEFAULT 0.00,
    Currency VARCHAR(3) DEFAULT 'MYR',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### WalletTransactions
```sql
CREATE TABLE WalletTransactions (
    WalletTxID VARCHAR(36) PRIMARY KEY,
    WalletID VARCHAR(36) NOT NULL,
    Type VARCHAR(20) NOT NULL, -- 'TOP_UP', 'DEBIT', 'REFUND', 'ADJUSTMENT'
    Amount NUMERIC(10, 2) NOT NULL,
    BalanceBefore NUMERIC(10, 2) NOT NULL,
    BalanceAfter NUMERIC(10, 2) NOT NULL,
    Reference VARCHAR(255), -- PaymentID
    Description TEXT,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Security Features

### 1. Signature Verification
- All webhook responses verify `skey` using secret key
- Invalid signatures are rejected with 400 error
- Prevents payment manipulation

### 2. Idempotency
- Wallet crediting checks existing payment status
- Already-completed payments skip wallet update
- Prevents double-crediting from duplicate webhooks

### 3. Transaction Safety
- Database transactions ensure atomic wallet updates
- Rollback on any error prevents partial updates
- Audit trail in `WalletTransactions`

### 4. Authentication
- `/payment/create` requires valid JWT token
- `/payment/:id` requires valid JWT token + user ownership
- Webhooks verify using Fiuu secret key

## Mobile App Integration

The mobile app should:

1. Call `POST /payment/create` with amount and channel
2. Receive `paymentUrl` and `paymentData`
3. Open payment URL in WebView or external browser with form submission
4. Poll `GET /payment/:id` every 2 seconds (already implemented in [usePaymentStore.ts](portable-refill-app/src/stores/usePaymentStore.ts))
5. Stop polling when status is `SUCCESS`, `FAILED`, or `CANCELLED`
6. Show appropriate UI based on status

Example WebView integration (React Native):

```typescript
import { WebView } from 'react-native-webview';

// After getting paymentUrl and paymentData from backend
const htmlForm = `
  <html>
    <body onload="document.forms[0].submit()">
      <form method="POST" action="${paymentUrl}">
        ${Object.entries(paymentData).map(([key, value]) => 
          `<input type="hidden" name="${key}" value="${value}">`
        ).join('')}
      </form>
    </body>
  </html>
`;

<WebView 
  source={{ html: htmlForm }}
  onNavigationStateChange={(navState) => {
    // Handle return URL
    if (navState.url.includes('/payment/return')) {
      // Close WebView and start polling
      closeWebView();
      startPollingPaymentStatus();
    }
  }}
/>
```

## Production Deployment Checklist

Before going live with production credentials:

- [ ] Replace sandbox credentials with production keys in `.env`
- [ ] Set `FIUU_SANDBOX=false` in `.env`
- [ ] Update webhook URLs to production HTTPS endpoints
- [ ] Register webhook URLs in Fiuu production merchant portal
- [ ] Test with small real payment first
- [ ] Enable all required payment channels in Fiuu portal
- [ ] Set up monitoring/alerting for webhook failures
- [ ] Test refund flow if needed
- [ ] Implement payment reconciliation job
- [ ] Set up status requery for pending payments
- [ ] Add rate limiting to webhook endpoints
- [ ] Review and increase database connection pool if needed

## Troubleshooting

### Webhook Not Called
- Ensure URLs are publicly accessible over HTTPS
- Check URLs are registered in Fiuu portal
- Verify URL domain matches registered domain
- Check firewall/security group rules
- Test webhook URL manually with curl

### Invalid Signature Error
- Verify `FIUU_SECRET_KEY` matches portal value
- Check signature calculation order
- Ensure amount format is `10.00` (2 decimals)
- Verify extended vcode setting matches portal

### Wallet Not Credited
- Check backend logs for webhook hits
- Verify payment status is `COMPLETED` in database
- Check `WalletTransactions` table for audit trail
- Ensure transaction was committed (no rollback)
- Verify user has wallet record

### Payment Stuck in Pending
- Check if webhook was received
- Manually verify payment in Fiuu portal
- Consider implementing status requery
- Check payment expiry time

## Next Steps

1. **Test Integration**: Follow test flow above with sandbox
2. **Mobile App Update**: Implement WebView payment flow
3. **Webhook Testing**: Set up ngrok and test all three webhooks
4. **Monitoring**: Add logging/monitoring for production
5. **Refunds**: Implement refund endpoint if needed
6. **Reconciliation**: Add daily reconciliation job
7. **Production Setup**: Get production credentials from Fiuu
8. **Go Live**: Deploy with production configuration

## API Reference

### Create Payment

**POST /payment/create**

Request:
```json
{
  "amount": "10.00",
  "currency": "MYR",
  "paymentMethod": "FIUU_FPX",
  "channel": "fpx"
}
```

Response:
```json
{
  "paymentId": "uuid",
  "status": "PENDING",
  "amount": "10.00",
  "currency": "MYR",
  "paymentUrl": "https://sandbox-payment.fiuu.com/...",
  "paymentData": { ... }
}
```

### Get Payment Status

**GET /payment/:id**

Response:
```json
{
  "paymentId": "uuid",
  "status": "SUCCESS",
  "amount": "10.00",
  "currency": "MYR",
  "gatewayTransactionId": "FIUU123456",
  "paymentMethod": "FIUU_FPX",
  "createdAt": "2026-06-08T10:00:00Z",
  "completedAt": "2026-06-08T10:05:00Z"
}
```

## Support

For Fiuu support:
- Technical Support: technical@fiuu.com
- Merchant Portal: https://sandbox-portal.fiuu.com (sandbox) or https://portal.fiuu.com (production)
- API Documentation: Provided by Fiuu during merchant onboarding

---

**Implementation Complete**: 2026-06-08  
**Sandbox Ready**: ✅  
**Production Ready**: Pending production credentials from Fiuu
