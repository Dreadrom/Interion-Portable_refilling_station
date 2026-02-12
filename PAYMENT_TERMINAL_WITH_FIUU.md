# Payment Terminal Integration with Fiuu

**Project**: Portable Petrol Station System  
**Date**: February 6, 2026  
**Payment Method**: Wallet-Based System with Fiuu Top-Up

---

## Executive Summary

This document outlines the integration strategy between the mobile app and Fiuu payment gateway using a **wallet-based payment model**. This approach ensures users have confirmed funds before dispensing fuel, eliminating payment fraud risks and failed transactions.

**Key Decision**: 
1. Users top up their in-app wallet using Fiuu QR payments (DuitNow/e-Wallet)
2. Wallet balance is used to pay for fuel at the pump
3. No direct payment during fueling - balance is pre-verified and deducted instantly

**Benefits**:
- Guaranteed payment before pump unlock
- No risk of payment failures during dispensing
- Prevents users from tricking the system
- Faster fueling experience (no waiting for payment confirmation)
- Simplified refund process if needed

---

## Payment Methods Selection

### Selected: QR-Based Payments (Zero Certification Cost)

1. **DuitNow QR** - Malaysia's national QR payment standard
   - Supported by all major banks
   - No certification fees
   - Instant settlement
   - Wide adoption

2. **e-Wallet QR Payments**
   - Touch 'n Go eWallet
   - Boost
   - GrabPay
   - ShopeePay
   - MAE by Maybank

### Avoided: Direct Card Payments (RM 100,000 Cost)
- Mastercard certification: ~RM 50,000
- Visa certification: ~RM 50,000
- Annual compliance costs
- Not cost-effective for initial launch

---

## System Architecture

```
WALLET TOP-UP FLOW (Using Fiuu):
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Mobile App    │         │   Backend API    │         │  Fiuu Gateway   │
│                 │         │   + Database     │         │                 │
└────────┬────────┘         └────────┬─────────┘         └────────┬────────┘
         │                           │                            │
         │  1. Request Wallet Top-Up │                            │
         │  (Amount: RM 100)         │                            │
         │─────────────────────────>│                            │
         │                           │                            │
         │                           │  2. Create Payment         │
         │                           │──────────────────────────>│
         │                           │                            │
         │                           │  3. Return QR Code         │
         │                           │<──────────────────────────│
         │                           │                            │
         │  4. Display QR Code       │                            │
         │<─────────────────────────│                            │
         │                           │                            │
         │  5. User Scans QR         │                            │
         │  (Banking App)            │                            │
         │                           │                            │
         │  6. Poll Payment Status   │                            │
         │─────────────────────────>│                            │
         │                           │  7. Query Status           │
         │                           │──────────────────────────>│
         │                           │  8. Payment Complete       │
         │                           │<──────────────────────────│
         │                           │                            │
         │                           │  9. Update User Wallet     │
         │                           │  (Balance + RM 100)        │
         │  10. Wallet Updated       │                            │
         │<─────────────────────────│                            │


FUEL DISPENSING FLOW (Using Wallet Balance):
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Mobile App    │         │   Backend API    │         │  Station/Pump   │
│                 │         │   + Database     │         │                 │
└────────┬────────┘         └────────┬─────────┘         └────────┬────────┘
         │                           │                            │
         │  1. Select Fuel Amount    │                            │
         │  (RM 50 for RON95)        │                            │
         │  Display: Wallet RM 100   │                            │
         │                           │                            │
         │  2. Request Dispense      │                            │
         │─────────────────────────>│                            │
         │                           │                            │
         │                           │  3. Verify Wallet Balance  │
         │                           │  (Balance >= RM 50)        │
         │                           │                            │
         │                           │  4. Hold Amount (RM 50)    │
         │                           │  Available: RM 100 - RM 50 │
         │                           │                            │
         │                           │  5. Unlock Pump Command    │
         │                           │──────────────────────────>│
         │  6. Pump Unlocked         │                            │
         │<─────────────────────────│  6. Pump Unlocked          │
         │                           │<──────────────────────────│
         │                           │                            │
         │  7. User Dispenses Fuel   │                            │
         │                           │  8. Real-time Volume Data  │
         │                           │<──────────────────────────│
         │  9. Dispense Progress     │                            │
         │<─────────────────────────│                            │
         │                           │                            │
         │                           │  10. Dispense Complete     │
         │                           │  Actual: RM 48.50          │
         │                           │<──────────────────────────│
         │                           │                            │
         │                           │  11. Deduct from Wallet    │
         │                           │  RM 100 - RM 48.50 = RM 51.50│
         │                           │  Release hold (RM 50)      │
         │                           │  New Balance: RM 51.50     │
         │                           │                            │
         │  12. Transaction Complete │                            │
         │  Receipt + New Balance    │                            │
         │<─────────────────────────│                            │
```

---

## Payment Flow Sequence

### PART A: Wallet Top-Up Flow (One-Time or When Balance Low)

### Step 1: User Initiates Wallet Top-Up
**Location**: Mobile App - Home Screen / Wallet Screen

```
User Actions:
1. Opens app and sees current wallet balance (e.g., RM 10.50)
2. Taps "Top-Up Wallet"
3. Selects top-up amount:
   - RM 50
   - RM 100
   - RM 200
   - Custom amount
4. Taps "Proceed to Payment"
```

### Step 2: Create Top-Up Payment Request
**Location**: Mobile App → Backend API

```typescript
// Mobile app sends to backend
POST /api/wallet/topup
Authorization: Bearer {user_jwt_token}
{
  "amount": 100.00,
  "currency": "MYR",
  "paymentMethod": "qr_duitnow" // or "qr_ewallet"
}
```

### Step 3: Backend Creates Fiuu Payment
**Location**: Backend API → Fiuu Gateway

```typescript
// Backend calls Fiuu API
POST https://sandbox.merchant.fiuu.com/api/payment/create

Body:
{
  "merchant_id": "MERCHANT_001",
  "order_id": "TOPUP-20260206-001",
  "amount": "100.00",
  "currency": "MYR",
  "payment_method": "duitnow_qr",
  "callback_url": "https://api.your-domain.com/webhooks/fiuu/topup",
  "description": "Wallet Top-Up - 100.00 MYR",
  "customer_email": "user@example.com"
}
```

### Step 4: Display QR Code for Top-Up
**Location**: Mobile App - QR Payment Screen

```
Display Elements:
1. Large QR code
2. Amount: RM 100.00
3. Purpose: "Wallet Top-Up"
4. Timer: "Expires in 14:32"
5. Instructions: "Scan with your banking app"
6. Current Balance: RM 10.50
7. New Balance After Top-Up: RM 110.50
8. Loading indicator "Waiting for payment..."
```

### Step 5: User Completes Payment
**Location**: User's Banking/e-Wallet App

```
User scans QR → Confirms payment → Payment successful
```

### Step 6: Payment Confirmation & Wallet Update
**Location**: Backend receives webhook from Fiuu

```typescript
// Fiuu webhook
POST /webhooks/fiuu/topup
{
  "payment_id": "FPM20260206001",
  "status": "completed",
  "amount": "100.00",
  "transaction_id": "TXN20260206001"
}

// Backend actions:
1. Verify webhook signature
2. Verify payment matches pending top-up
3. Update user wallet:
   - Previous: RM 10.50
   - Top-up: + RM 100.00
   - New: RM 110.50
4. Create wallet transaction record
5. Send push notification to user
6. Return success to mobile app
```

---

### PART B: Fuel Purchase Flow (Using Wallet Balance)

### Step 1: User Selects Fuel Amount
**Location**: Mobile App - Station Info Screen

```
User Actions:
1. Opens station detail screen
2. Sees wallet balance: RM 110.50
3. Selects product (RON95 @ RM 2.05/L)
4. Chooses amount:
   - RM 20 (~9.7L)
   - RM 50 (~24.4L)
   - RM 100 (~48.8L)
   - Custom amount
5. App validates: Amount <= Wallet Balance
6. If balance insufficient, show "Top-Up Wallet" button
7. Taps "Start Fueling"
```

### Step 2: Create Fuel Authorization Request
**Location**: Mobile App → Backend API

```typescript
// Mobile app sends to backend
POST /api/dispense/authorize
Authorization: Bearer {user_jwt_token}
{
  "stationId": "STN001",
  "productType": "RON95",
  "authorizedAmount": 50.00,
  "nozzleId": "A1"
}
```

### Step 3: Backend Validates & Holds Funds
**Location**: Backend API

```typescript
// Backend validation:
1. Check user wallet balance: RM 110.50
2. Check authorized amount: RM 50.00
3. Verify balance sufficient: 110.50 >= 50.00 ✓
4. Create hold transaction:
   - Available balance: RM 110.50 - RM 50.00 = RM 60.50
   - Held amount: RM 50.00
5. Verify station is IDLE and available
6. Create transaction record (status: AUTHORIZED)

Response:
{
  "transactionId": "TXN20260206002",
  "authorizedAmount": 50.00,
  "walletBalance": 110.50,
  "availableBalance": 60.50,
  "status": "AUTHORIZED"
}
```

### Step 4: Unlock Pump
**Location**: Backend API → Station Hardware

```typescript
// Backend sends MQTT command via AWS IoT Core
MQTT Publish to: stations/STN001/commands
{
  "command": "UNLOCK_PUMP",
  "transactionId": "TXN20260206002",
  "nozzleId": "A1",
  "authorizedAmount": 50.00,
  "productType": "RON95",
  "unitPrice": 2.05
}

// Station acknowledges
MQTT Publish to: stations/STN001/status
{
  "status": "PUMP_UNLOCKED",
  "nozzleId": "A1",
  "transactionId": "TXN20260206002"
}
```

### Step 5: User Dispenses Fuel
**Location**: Physical Station

```
1. Mobile app shows: "Pump Unlocked - Please lift nozzle A1"
2. User lifts nozzle
3. User squeezes trigger to dispense fuel
4. Station sends real-time data via MQTT every 500ms:
   - Volume dispensed
   - Amount charged
   - Flow rate
```

### Step 6: Real-Time Progress Updates
**Location**: Mobile App - Live Dispensing Screen

```typescript
// Mobile app subscribes to MQTT updates
MQTT Subscribe to: stations/STN001/transactions/TXN20260206002

// Receives updates every 500ms:
{
  "volume": 23.5, // liters
  "amount": 48.18, // RM
  "flowRate": 0.45, // L/s
  "status": "DISPENSING"
}

// Display:
- Progress bar
- Volume: 23.5 L
- Amount: RM 48.18 / RM 50.00
- Remaining: RM 1.82
- [Emergency Stop] button
```

### Step 7: Dispensing Complete
**Location**: Station Hardware → Backend

```typescript
// Station publishes completion
MQTT Publish to: stations/STN001/transactions/TXN20260206002
{
  "status": "COMPLETED",
  "finalVolume": 23.66, // liters
  "finalAmount": 48.50, // RM (23.66L × RM 2.05/L)
  "startTime": "2026-02-06T14:30:00Z",
  "endTime": "2026-02-06T14:31:45Z"
}
```

### Step 8: Wallet Settlement
**Location**: Backend API

```typescript
// Backend processes completion:
1. Release hold: RM 50.00
2. Deduct actual amount: RM 48.50
3. Update wallet:
   - Previous: RM 110.50
   - Deducted: - RM 48.50
   - New: RM 62.00
4. Update transaction record (status: COMPLETED)
5. Generate receipt

Response to mobile app:
{
  "transactionId": "TXN20260206002",
  "status": "COMPLETED",
  "volumeDispensed": 23.66,
  "amountCharged": 48.50,
  "previousBalance": 110.50,
  "newBalance": 62.00,
  "receipt": {
    "date": "2026-02-06T14:31:45Z",
    "station": "Station A",
    "product": "RON95",
    "volume": "23.66 L",
    "unitPrice": "RM 2.05/L",
    "totalAmount": "RM 48.50"
  }
}
```

### Step 9: Show Receipt
**Location**: Mobile App - Transaction Complete Screen

```
Display:
- "Refueling Complete!"
- Volume: 23.66 L
- Amount Paid: RM 48.50
- Wallet Balance: RM 62.00
- Receipt details
- [Done] button returns to home
```

---

## Mobile App Screens Flow

### Screen 1: Home Screen (Wallet Visible)
```
┌─────────────────────────────┐
│  Interion Portable Refill   │
│                             │
│  ┌───────────────────────┐ │
│  │ Wallet Balance        │ │
│  │ RM 62.00              │ │
│  │ [Top-Up]              │ │
│  └───────────────────────┘ │
│                             │
│  [Find Station]             │
│  [Transaction History]      │
│  [Profile]                  │
└─────────────────────────────┘
```

### Screen 2: Wallet Top-Up
```
┌─────────────────────────────┐
│  Top-Up Wallet              │
│                             │
│  Current Balance: RM 62.00  │
│                             │
│  Select Amount:             │
│  [RM 50] [RM 100] [RM 200]  │
│  [Custom: _____]            │
│                             │
│  New Balance: RM 162.00     │
│                             │
│  [Proceed to Payment]       │
└─────────────────────────────┘
```

### Screen 3: QR Code for Top-Up
```
┌─────────────────────────────┐
│  Scan to Top-Up             │
│  RM 100.00                  │
│                             │
│  ┌─────────────────────┐   │
│  │                     │   │
│  │   [QR CODE HERE]    │   │
│  │                     │   │
│  └─────────────────────┘   │
│                             │
│  Expires in: 14:32          │
│                             │
│  Scan with banking app      │
│                             │
│  Waiting for payment...     │
│                             │
│  [Cancel]                   │
└─────────────────────────────┘
```

### Screen 4: Top-Up Success
```
┌─────────────────────────────┐
│  Top-Up Successful          │
│                             │
│  Amount: RM 100.00          │
│  Method: DuitNow QR         │
│                             │
│  Previous: RM 62.00         │
│  New Balance: RM 162.00     │
│                             │
│  [Continue]                 │
└─────────────────────────────┘
```

### Screen 5: Station Selection
```
┌─────────────────────────────┐
│  Select Station             │
│  Wallet: RM 162.00          │
│                             │
│  Station A - 0.5 km         │
│  Status: Available          │
│  RON95: RM 2.05/L           │
│  [Select]                   │
│                             │
│  Station B - 1.2 km         │
│  Status: Available          │
│  RON95: RM 2.10/L           │
│  [Select]                   │
└─────────────────────────────┘
```

### Screen 6: Fuel Selection
```
┌─────────────────────────────┐
│  Station A - RON95          │
│  RM 2.05 per liter          │
│  Wallet: RM 162.00          │
│                             │
│  Select Amount:             │
│  [RM 20] [RM 50] [RM 100]   │
│  [Custom: _____]            │
│                             │
│  Balance after: RM 112.00   │
│                             │
│  [Start Fueling]            │
└─────────────────────────────┘
```

### Screen 7: Pump Unlocked
```
┌─────────────────────────────┐
│  Pump Unlocked              │
│                             │
│  Please lift nozzle A1      │
│  and begin fueling          │
│                             │
│  Authorized: RM 50.00       │
│  (~24.4 L)                  │
│                             │
│  Waiting for fueling...     │
│                             │
│  [Emergency Stop]           │
└─────────────────────────────┘
```

### Screen 8: Live Dispensing
```
┌─────────────────────────────┐
│  Fueling in Progress        │
│                             │
│  Volume: 18.5 L             │
│  Amount: RM 37.93           │
│                             │
│  [████████░░░] 75%          │
│                             │
│  Remaining: RM 12.07        │
│  Flow: 0.45 L/s             │
│                             │
│  [Emergency Stop]           │
└─────────────────────────────┘
```

### Screen 9: Fueling Complete
```
┌─────────────────────────────┐
│  Fueling Complete           │
│                             │
│  Volume: 23.66 L            │
│  Amount: RM 48.50           │
│                             │
│  Previous: RM 162.00        │
│  Deducted: RM 48.50         │
│  New Balance: RM 113.50     │
│                             │
│  [View Receipt]             │
│  [Done]                     │
└─────────────────────────────┘
```

---

## Security Considerations

### 1. Payment Authentication
```
Backend API Signature:
- All requests to Fiuu signed with HMAC-SHA256
- Secret key stored in AWS Secrets Manager
- Prevents tampering and replay attacks

Signature Generation:
hash = HMAC-SHA256(merchant_id + order_id + amount + currency + secret_key)
```

### 2. Payment Verification
```
Before Unlocking Pump:
1. Verify payment status from Fiuu (not just mobile app claim)
2. Check payment amount matches authorized amount
3. Verify payment timestamp (within 10 minutes)
4. Check payment not already used (prevent double-spend)
5. Validate station ID matches payment
```

### 3. QR Code Security
```
- QR codes expire after 15 minutes
- One-time use only
- Payment ID unique per transaction
- Cannot be reused after completion/expiry
```

### 4. Webhook Verification
```
Fiuu sends webhook to backend:
POST /webhooks/fiuu/payment
Headers:
  X-Fiuu-Signature: {signature}

Backend verifies:
1. Signature matches expected HMAC
2. Payment ID exists in database
3. Status transition valid (PENDING → COMPLETED)
4. Amount matches original request
```

---

## Backend API Endpoints

### 1. Get Wallet Balance
```
GET /api/wallet/balance
Authorization: Bearer {user_jwt_token}

Response:
{
  "balance": 162.00,
  "currency": "MYR",
  "lastUpdated": "2026-02-06T14:20:00Z"
}
```

### 2. Create Wallet Top-Up
```
POST /api/wallet/topup
Authorization: Bearer {user_jwt_token}

Request:
{
  "amount": 100.00,
  "paymentMethod": "qr_duitnow" // or "qr_ewallet"
}

Response:
{
  "topupId": "TOPUP20260206001",
  "paymentId": "FPM20260206001",
  "qrCodeUrl": "https://fiuu.com/qr/FPM20260206001",
  "qrCodeData": "00020101021...",
  "amount": 100.00,
  "expiresAt": "2026-02-06T15:30:00Z",
  "status": "PENDING"
}
```

### 3. Check Top-Up Status
```
GET /api/wallet/topup/{topupId}/status
Authorization: Bearer {user_jwt_token}

Response:
{
  "topupId": "TOPUP20260206001",
  "status": "COMPLETED", // PENDING | COMPLETED | FAILED | EXPIRED
  "amount": 100.00,
  "paidAt": "2026-02-06T14:18:23Z",
  "previousBalance": 62.00,
  "newBalance": 162.00
}
```

### 4. Authorize Fuel Dispense
```
POST /api/dispense/authorize
Authorization: Bearer {user_jwt_token}

Request:
{
  "stationId": "STN001",
  "productType": "RON95",
  "authorizedAmount": 50.00,
  "nozzleId": "A1"
}

Response:
{
  "transactionId": "TXN20260206002",
  "authorizedAmount": 50.00,
  "walletBalance": 162.00,
  "availableBalance": 112.00, // Balance after hold
  "heldAmount": 50.00,
  "status": "AUTHORIZED"
}

Error Response (Insufficient Balance):
{
  "error": "INSUFFICIENT_BALANCE",
  "message": "Wallet balance insufficient",
  "required": 50.00,
  "available": 30.00,
  "shortfall": 20.00
}
```

### 5. Get Dispense Progress
```
GET /api/dispense/{transactionId}/progress
Authorization: Bearer {user_jwt_token}

Response:
{
  "transactionId": "TXN20260206002",
  "status": "DISPENSING", // AUTHORIZED | DISPENSING | COMPLETED | STOPPED
  "volumeDispensed": 18.5,
  "amountCharged": 37.93,
  "authorizedAmount": 50.00,
  "remainingAmount": 12.07,
  "startTime": "2026-02-06T14:30:00Z"
}
```

### 6. Stop Dispense (Emergency)
```
POST /api/dispense/{transactionId}/stop
Authorization: Bearer {user_jwt_token}

Response:
{
  "transactionId": "TXN20260206002",
  "status": "STOPPED",
  "finalVolume": 18.5,
  "finalAmount": 37.93,
  "refundedToWallet": true,
  "newBalance": 124.07
}
```

### 7. Get Transaction History
```
GET /api/wallet/transactions
Authorization: Bearer {user_jwt_token}
Query: ?page=1&limit=20&type=all // all | topup | fuel

Response:
{
  "transactions": [
    {
      "id": "TXN20260206002",
      "type": "FUEL_PURCHASE",
      "amount": -48.50,
      "description": "RON95 - 23.66L",
      "balanceBefore": 162.00,
      "balanceAfter": 113.50,
      "timestamp": "2026-02-06T14:31:45Z"
    },
    {
      "id": "TOPUP20260206001",
      "type": "WALLET_TOPUP",
      "amount": +100.00,
      "description": "Wallet Top-Up via DuitNow",
      "balanceBefore": 62.00,
      "balanceAfter": 162.00,
      "timestamp": "2026-02-06T14:18:23Z"
    }
  ],
  "currentBalance": 113.50,
  "page": 1,
  "totalPages": 5
}
```

### 8. Fiuu Webhook Handler (Top-Up)
```
POST /webhooks/fiuu/topup
Headers:
  X-Fiuu-Signature: {signature}

Body:
{
  "payment_id": "FPM20260206001",
  "order_id": "TOPUP20260206001",
  "status": "completed",
  "amount": "100.00",
  "transaction_id": "TXN20260206001",
  "paid_at": "2026-02-06T14:18:23Z"
}

Backend Actions:
1. Verify signature
2. Find pending top-up record
3. Update user wallet balance
4. Create wallet transaction record
5. Send push notification to mobile app
6. Return 200 OK to Fiuu
```

---

## Payment & Wallet Status Flow

```
WALLET TOP-UP:
PENDING ──────> COMPLETED ──────> WALLET_UPDATED
   │
   ├─────> EXPIRED (QR timeout)
   │
   └─────> FAILED (Bank rejection)


FUEL TRANSACTION:
CHECK_BALANCE ──> AUTHORIZED ──> DISPENSING ──> COMPLETED ──> WALLET_SETTLED
                      │              │              │
                      │              │              └──> PARTIAL_REFUND (if under-fueled)
                      │              │
                      │              └──────> STOPPED ──> FULL_REFUND
                      │
                      └─────> INSUFFICIENT_BALANCE ──> TOP_UP_REQUIRED
```

### Status Definitions

| Status | Description | Next Actions |
|--------|-------------|--------------|
| **Wallet Top-Up** |||
| PENDING | QR code displayed, awaiting payment | Poll for status updates |
| COMPLETED | Payment confirmed by Fiuu | Update wallet balance |
| EXPIRED | QR code expired (15 min) | Show retry option |
| FAILED | Payment rejected by bank | Show error, offer retry |
| **Fuel Transaction** |||
| CHECK_BALANCE | Verifying sufficient funds | Continue or prompt top-up |
| AUTHORIZED | Funds held, pump ready | Unlock pump |
| DISPENSING | Fuel being dispensed | Monitor progress |
| STOPPED | User stopped early | Refund unused amount |
| COMPLETED | Fueling finished | Settle wallet, show receipt |
| INSUFFICIENT_BALANCE | Not enough funds | Prompt wallet top-up |
   ├─────> EXPIRED                       └─────> REFUNDED
   │
   └─────> FAILED
```

### Status Definitions

| Status | Description | Next Actions |
|--------|-------------|--------------|
| PENDING | QR code displayed, awaiting payment | Poll for status updates |
| COMPLETED | Payment confirmed by Fiuu | Unlock pump, start dispensing |
| EXPIRED | QR code expired (15 min) | Show retry option |
| FAILED | Payment rejected by bank | Show error, offer retry |
| DISPENSING | Fuel being dispensed | Monitor volume/amount |
| REFUNDED | Payment refunded (if dispense failed) | Update user balance |

---

## Error Handling

### Scenario 1: Payment Timeout (User doesn't pay within 15 min)
```
1. QR code expires
2. Mobile app shows: "Payment expired"
3. User can:
   - Try again (generate new QR)
   - Cancel and return
4. No charge to user
```

### Scenario 2: Payment Failed
```
1. User's bank rejects payment (insufficient funds, etc.)
2. Fiuu returns status: "failed"
3. Mobile app shows: "Payment failed: {reason}"
4. User can:
   - Try different payment method
   - Top up account and retry
   - Cancel
```

### Scenario 3: Paid but Pump Doesn't Unlock
```
1. Payment completed successfully
2. Hardware communication fails
3. Backend detects unlock failure
4. Mobile app shows: "Payment received, unlocking pump..."
5. Backend retries unlock command (3 attempts)
6. If still fails:
   - Log incident
   - Notify operations team
   - Initiate automatic refund
   - Show customer support contact
```

### Scenario 4: Network Lost During Payment
```
1. Mobile app polls for status
2. Network disconnects
3. User sees: "Checking payment status..."
4. When reconnected:
   - Resume polling
   - Retrieve current payment status
   - Continue from where left off
5. Payment state preserved on backend
```

---

## Fiuu API Integration Details

### Authentication
```
Sandbox: https://sandbox.merchant.fiuu.com
Production: https://api.merchant.fiuu.com

Headers:
  Content-Type: application/json
  X-Fiuu-Merchant-Id: {merchant_id}
  X-Fiuu-Signature: {hmac_signature}
```

### Signature Generation
```javascript
// Example in Node.js
const crypto = require('crypto');

function generateSignature(merchantId, orderId, amount, secretKey) {
  const signatureString = `${merchantId}|${orderId}|${amount}|${secretKey}`;
  return crypto
    .createHash('sha256')
    .update(signatureString)
    .digest('hex');
}
```

### DuitNow QR Request
```json
POST /api/payment/qr/create
{
  "merchant_id": "MERCHANT_001",
  "order_id": "ORD-20260206-001",
  "amount": "50.00",
  "currency": "MYR",
  "qr_type": "duitnow", // or "dynamic_qr" for e-wallets
  "expiry_minutes": 15,
  "callback_url": "https://api.your-domain.com/webhooks/fiuu",
  "description": "RON95 Fuel Purchase"
}
```

### Response Format
```json
{
  "status": "success",
  "payment_id": "FPM20260206001",
  "qr_code": "00020101021...", // EMVCo QR code data
  "qr_image_url": "https://fiuu.com/qr/FPM20260206001.png",
  "amount": "50.00",
  "expires_at": "2026-02-06T15:30:00Z"
}
```

---

## Cost Analysis

### QR Payment vs Card Payment

| Payment Method | Setup Cost | Transaction Fee | Certification | Annual Fee |
|----------------|------------|-----------------|---------------|------------|
| DuitNow QR | RM 0 | 0.5% - 1.0% | Not required | RM 0 |
| e-Wallet QR | RM 0 | 1.0% - 1.5% | Not required | RM 0 |
| Mastercard | RM 50,000 | 1.5% - 2.5% | Required | RM 10,000 |
| Visa | RM 50,000 | 1.5% - 2.5% | Required | RM 10,000 |

**Recommendation**: Start with QR payments, add card payments after achieving profitability.

---

## Implementation Checklist

### Phase 1: Backend Integration
- [ ] Register merchant account with Fiuu
- [ ] Obtain API credentials (merchant_id, secret_key)
- [ ] Store credentials in AWS Secrets Manager
- [ ] Implement signature generation
- [ ] Create payment creation endpoint
- [ ] Create payment status endpoint
- [ ] Implement webhook handler
- [ ] Test in Fiuu sandbox environment

### Phase 2: Mobile App Integration
- [ ] Install QR code library (`react-native-qrcode-svg`)
- [ ] Create QR payment screen
- [ ] Implement QR code display
- [ ] Add payment status polling (2-second interval)
- [ ] Add expiry timer countdown
- [ ] Implement error handling
- [ ] Add payment success/failure screens
- [ ] Test with test QR codes

### Phase 3: Testing
- [ ] Test QR code generation
- [ ] Test payment flow end-to-end
- [ ] Test payment expiry
- [ ] Test payment cancellation
- [ ] Test network disconnection scenarios
- [ ] Test webhook delivery
- [ ] Test pump unlock integration
- [ ] Load testing (concurrent payments)

### Phase 4: Production Deployment
- [ ] Switch to Fiuu production API
- [ ] Update merchant credentials
- [ ] Enable webhook endpoint
- [ ] Monitor first transactions
- [ ] Set up payment analytics
- [ ] Configure alerts for failed payments

---

## Fiuu Support

**Technical Support**:
- Email: support@fiuu.com
- Phone: +60 3-xxxx-xxxx
- Documentation: https://docs.fiuu.com

**Account Manager**:
- For certification questions
- For transaction limits
- For fee negotiations

---

## Next Steps

1. **Register with Fiuu**
   - Complete merchant onboarding
   - Submit business documents
   - Get sandbox credentials

2. **Backend Development**
   - Implement payment creation API
   - Implement webhook handler
   - Set up database tables for payments

3. **Mobile App Development**
   - Build QR display screen
   - Implement status polling
   - Add error handling

4. **Testing**
   - Test in sandbox environment
   - Simulate all payment scenarios
   - Verify pump unlock integration

5. **Go Live**
   - Switch to production credentials
   - Monitor first transactions
   - Gather user feedback

---

**Document Version**: 1.0  
**Last Updated**: February 6, 2026  
**Status**: Ready for Implementation
