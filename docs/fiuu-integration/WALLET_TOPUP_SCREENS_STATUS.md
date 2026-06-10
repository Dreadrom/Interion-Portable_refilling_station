# Wallet Top-Up Screens Status Report
**Date**: June 2, 2026  
**Purpose**: Document current implementation status of wallet top-up flow for Fiuu integration

---

## 🎯 Executive Summary

The wallet top-up UI has a **basic amount selection screen** implemented, but **critical Fiuu QR payment screens are missing**. The current implementation simulates payment with a 2-second delay instead of integrating with Fiuu's QR payment gateway.

---

## ✅ Implemented Screens

### 1. Amount Selection Screen (`top-up-wallet.tsx`)
**Status**: ✅ **Fully Implemented**  
**Route**: `/top-up-wallet`  
**File**: `portable-refill-app/app/(tabs)/top-up-wallet.tsx`

**Features**:
- Current wallet balance display (MYR 0.00)
- Quick select buttons: MYR 10, 20, 50, 100, 200, 500
- Custom amount input (Min: MYR 1, Max: MYR 5,000)
- Payment method selection (Touch 'n Go eWallet)
- Top-up summary showing:
  - Top-Up Amount
  - Current Balance
  - New Balance (calculated)

**Screenshot Captured**: ✅ Yes (2 variants captured in browser)

---

## ❌ Missing Screens (Critical for Fiuu Integration)

### 2. QR Payment Display Screen
**Status**: ❌ **NOT IMPLEMENTED**  
**Expected Route**: `/payment-qr` or modal overlay  
**Required After**: User clicks "Top-Up MYR X.XX" button

**Should Display**:
- QR code image (from `payment.qrCodeData` or `payment.qrCodeImageUrl`)
- Payment amount: MYR X.XX
- Countdown timer showing QR expiration (from `payment.expiresAt`)
- Instructions: "Scan QR code with your e-wallet app"
- Supported payment methods: DuitNow, Touch 'n Go, Boost, GrabPay, ShopeePay
- Cancel button

**Technical Requirements**:
```typescript
// API Call (already exists in payments.ts)
const { payment } = await createPayment({
  stationId: 'WALLET_TOPUP',
  amount: selectedAmount,
  currency: 'MYR',
  method: 'FIUU_EWALLET',
  description: 'Wallet Top-Up'
});

// Display QR
<QRCode value={payment.qrCodeData} size={250} />
// OR
<Image source={{ uri: payment.qrCodeImageUrl }} />

// Show expiration countdown
const expiresIn = new Date(payment.expiresAt).getTime() - Date.now();
```

---

### 3. Payment Waiting Screen
**Status**: ❌ **NOT IMPLEMENTED**  
**Expected Route**: Same as QR screen, updated state  
**Required During**: Polling for payment status

**Should Display**:
- Spinner/loading indicator
- "Waiting for payment confirmation..."
- QR code (faded or smaller)
- Payment amount
- Cancel option

**Technical Requirements**:
```typescript
// Polling (already exists in payments.ts)
await pollPaymentStatus(payment.id, {
  interval: 2000,
  maxAttempts: 60, // 2 minutes
  onUpdate: (updatedPayment) => {
    // Update UI with latest status
    setPaymentStatus(updatedPayment.status);
  }
});
```

---

### 4. Payment Success Screen
**Status**: ⚠️ **PARTIALLY IMPLEMENTED** (as Alert dialog only)  
**Expected Route**: `/payment-success` or modal overlay  
**Required After**: Payment status becomes 'SUCCESS'

**Current Implementation**:
```typescript
// Current: Just an Alert dialog
Alert.alert(
  'Top-Up Successful!',
  `MYR ${amount.toFixed(2)} added via ${method?.label}.\n\nNew Balance: MYR ${newBalance.toFixed(2)}`,
  [{ text: 'Done', onPress: () => router.back() }]
);
```

**Should Be**:
- Full-screen success page with:
  - ✅ Success icon (green checkmark)
  - "Payment Successful!"
  - Amount topped up: MYR X.XX
  - New wallet balance: MYR Y.YY
  - Transaction ID
  - Timestamp
  - "Done" button → Navigate to home or wallet history

---

### 5. Payment Failed Screen
**Status**: ❌ **NOT IMPLEMENTED**  
**Expected Route**: `/payment-failed` or modal overlay  
**Required When**: Payment status becomes 'FAILED'

**Should Display**:
- ❌ Error icon (red X)
- "Payment Failed"
- Error reason (from `payment.error_desc`)
- Suggested actions:
  - "Try Again" button → Return to amount selection
  - "Contact Support" button
  - "Cancel" button → Return to home

---

### 6. Payment Expired Screen
**Status**: ❌ **NOT IMPLEMENTED**  
**Expected Route**: Same as failed screen, different message  
**Required When**: QR code expires (usually 5 minutes)

**Should Display**:
- ⏱️ Timer expired icon
- "QR Code Expired"
- "The payment QR code has expired. Please try again."
- "Try Again" button → Return to amount selection
- "Cancel" button → Return to home

---

### 7. Updated Wallet Balance Screen
**Status**: ✅ **EXISTS** (home screen shows balance)  
**Expected After**: Successful payment  
**Route**: `/home` or `/profile`

**Should Display**:
- Updated wallet balance reflecting the new amount
- Recent transaction in transaction history

---

## 📊 Implementation Summary

| Screen | Status | Priority | Blocking Fiuu Integration? |
|--------|--------|----------|---------------------------|
| Amount Selection | ✅ Implemented | - | No |
| QR Display | ❌ Missing | 🔴 **CRITICAL** | **YES** |
| Payment Waiting | ❌ Missing | 🔴 **CRITICAL** | **YES** |
| Payment Success | ⚠️ Partial (Alert only) | 🟡 High | Partial |
| Payment Failed | ❌ Missing | 🟡 High | Partial |
| Payment Expired | ❌ Missing | 🟡 High | Partial |
| Updated Balance | ✅ Implemented | - | No |

---

## 🔍 Current Implementation Analysis

### File: `top-up-wallet.tsx`

**Current `handleTopUp()` function**:
```typescript
const handleTopUp = async () => {
  // ... validation ...
  
  setProcessing(true);
  try {
    // ⚠️ PROBLEM: Just simulates with 2-second delay
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // ⚠️ PROBLEM: Directly updates balance without payment gateway
    await topUpBalance(amount);
    
    // ⚠️ PROBLEM: Shows Alert instead of proper success screen
    Alert.alert(
      'Top-Up Successful!',
      `${currency} ${amount.toFixed(2)} added...`,
      [{ text: 'Done', onPress: () => router.back() }]
    );
  } catch (error: any) {
    Alert.alert('Top-Up Failed', error.message);
  } finally {
    setProcessing(false);
  }
};
```

**Required Implementation**:
```typescript
const handleTopUp = async () => {
  // ... validation ...
  
  setProcessing(true);
  try {
    // 1️⃣ Create payment with Fiuu
    const { payment } = await createPayment({
      stationId: 'WALLET_TOPUP',
      amount: selectedAmount,
      currency: 'MYR',
      method: 'FIUU_EWALLET',
      description: 'Wallet Top-Up'
    });
    
    // 2️⃣ Navigate to QR display screen
    router.push({
      pathname: '/payment-qr',
      params: {
        paymentId: payment.id,
        qrCodeData: payment.qrCodeData,
        amount: payment.amount,
        expiresAt: payment.expiresAt
      }
    });
    
    // 3️⃣ In QR screen: Start polling for payment status
    await pollPaymentStatus(payment.id, {
      interval: 2000,
      maxAttempts: 60,
      onUpdate: (updatedPayment) => {
        if (updatedPayment.status === 'SUCCESS') {
          router.replace('/payment-success');
        } else if (updatedPayment.status === 'FAILED') {
          router.replace('/payment-failed');
        } else if (updatedPayment.status === 'EXPIRED') {
          router.replace('/payment-expired');
        }
      }
    });
  } catch (error: any) {
    Alert.alert('Payment Creation Failed', error.message);
  } finally {
    setProcessing(false);
  }
};
```

---

## 🚨 Blocker Issues for Fiuu Team

### Issue #1: No QR Code Display
**Impact**: Users cannot complete payment  
**Fiuu Requirement**: App must display QR code from `payment.qrCodeData`  
**Status**: ❌ **Not implemented**

### Issue #2: No Payment Polling
**Impact**: App doesn't know when payment succeeds  
**Fiuu Requirement**: App must poll `/api/payments/:id` every 2 seconds  
**Status**: ❌ **Not implemented in UI** (API exists but unused)

### Issue #3: No QR Expiration Handling
**Impact**: Users don't know when QR expires  
**Fiuu Requirement**: Show countdown timer, handle expiration gracefully  
**Status**: ❌ **Not implemented**

### Issue #4: No Payment Method Selection
**Impact**: Currently hardcoded to Touch 'n Go only  
**Fiuu Requirement**: Support DuitNow QR, Touch 'n Go, Boost, GrabPay, ShopeePay  
**Status**: ⚠️ **Partial** (UI exists but only shows 1 option)

---

## 📝 Action Items

### For Development Team
1. [ ] Create `/payment-qr` screen with QR code display
2. [ ] Implement QR expiration countdown timer
3. [ ] Integrate `createPayment()` API call in `handleTopUp()`
4. [ ] Implement `pollPaymentStatus()` in QR screen
5. [ ] Create `/payment-success` full-screen component
6. [ ] Create `/payment-failed` error screen
7. [ ] Create `/payment-expired` timeout screen
8. [ ] Add multiple payment method support (DuitNow, Boost, GrabPay, ShopeePay)
9. [ ] Test with Fiuu sandbox environment

### For Fiuu Team
1. [ ] Review this document and FIUU_VISUAL_USER_JOURNEY.md
2. [ ] Confirm QR code format (string vs. image URL)
3. [ ] Confirm QR expiration time (default 5 minutes?)
4. [ ] Provide test QR codes for development
5. [ ] Clarify polling interval recommendations
6. [ ] Provide error codes and messages documentation

---

## 📸 Screenshots Captured

| Screen | Status | Filename | Notes |
|--------|--------|----------|-------|
| Login | ✅ Captured | N/A | Shows "BlueDiesel" branding |
| Amount Selection (Empty) | ✅ Captured | wallet-topup-amount-selection.png | Shows MYR 0.00 balance, quick select buttons |
| Amount Selected (MYR 100) | ✅ Captured | wallet-topup-summary.png | Shows top-up summary with new balance calculation |
| QR Payment Display | ❌ **Cannot capture** | - | **Screen doesn't exist** |
| Payment Waiting | ❌ **Cannot capture** | - | **Screen doesn't exist** |
| Payment Success | ❌ **Cannot capture** | - | Only exists as Alert dialog |
| Payment Failed | ❌ **Cannot capture** | - | **Screen doesn't exist** |
| Payment Expired | ❌ **Cannot capture** | - | **Screen doesn't exist** |

---

## 🔗 Related Documents

1. **FIUU_INTEGRATION_OVERVIEW.md** - Technical overview of Fiuu integration issues
2. **FIUU_VISUAL_USER_JOURNEY.md** - Visual walkthrough with screenshots (incomplete)
3. **PAYMENT_TERMINAL_WITH_FIUU.md** - Original payment integration plan
4. **portable-refill-app/src/api/payments.ts** - Payment API implementation (exists but unused in UI)
5. **portable-refill-app/src/types/payment.ts** - Payment type definitions

---

## ✅ Conclusion

**The wallet top-up UI is at ~20% completion**:
- ✅ Amount selection works
- ❌ **Critical Fiuu QR payment flow is completely missing**
- ⚠️ Current implementation bypasses payment gateway entirely

**Recommendation**: Implement QR payment screens before engaging with Fiuu team, or provide this document to Fiuu team to explain why we cannot provide complete screenshots yet.

---

**Next Steps**:
1. Share this document with Fiuu team explaining the gap
2. Develop the missing 5 screens (QR display, waiting, success, failed, expired)
3. Integrate with Fiuu sandbox API
4. Capture complete screenshot set for FIUU_VISUAL_USER_JOURNEY.md
