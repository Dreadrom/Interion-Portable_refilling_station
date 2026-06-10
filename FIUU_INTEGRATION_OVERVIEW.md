# Fiuu Payment Integration - User Journey & Issues

**Project**: MY Diesel Mobile App  
**Date**: May 31, 2026  
**Payment Provider**: Fiuu (formerly MOLPay)  
**Payment Model**: Wallet Top-Up System

---

## User Journey Overview

### 1. **Initial Setup**
```
User Downloads App → Creates Account → Wallet Balance: RM 0.00
```

### 2. **Wallet Top-Up Journey**

**Step 1: User Needs to Add Funds**
- User opens app and sees "Wallet Balance: RM 0.00"
- Taps "Top-Up Wallet"
- Selects amount: RM 50 / RM 100 / RM 200 / Custom
- Taps "Proceed to Payment"

**Step 2: QR Payment Screen**
- App displays QR code from Fiuu
- Shows: "Scan with DuitNow, Touch 'n Go, or any banking app"
- Timer: "Expires in 15:00 minutes"
- User scans QR with their banking app

**Step 3: Payment Confirmation**
- User completes payment in banking app
- App polls Fiuu for payment status
- Success: Wallet updated immediately
- User sees: "Wallet Balance: RM 100.00"

### 3. **Adblue Purchase Journey**

**Step 1: Find Station**
- User opens "Find Stations" map
- Selects nearest MY Diesel station
- Views available fuel types and prices

**Step 2: Scan Station QR**
- User arrives at station
- Scans QR code on pump
- App shows: "Select Fuel Type"

**Step 3: Pre-Authorization**
- User selects: "RON 95 - RM 2.05/L"
- Enters desired amount: "RM 50.00"
- App checks wallet balance
- If sufficient → Shows 4-digit PIN on screen
- User enters PIN on pump keypad

**Step 4: Dispensing**
- Pump unlocks
- User dispenses fuel
- App shows real-time:
  - Volume: 15.2 L
  - Amount: RM 31.20
  - Flow rate: 5 L/min

**Step 5: Completion**
- User replaces nozzle
- Pump locks automatically
- Actual charge: RM 48.50 (23.7 L)
- Wallet updated: RM 100.00 → RM 51.50
- Digital receipt generated

---

## Key Problems Detected

### ⚠️ Problem 1: QR Code Payment Status Polling
**Issue**: App needs to continuously check if user paid

**Current Challenge**:
```
App generates QR → User scans → User pays in banking app
↓
App must poll Fiuu every 2-3 seconds: "Did user pay yet?"
↓
If user closes app during payment → Polling stops → Payment completes but wallet not updated
```

**Impact**: 
- User pays but wallet shows RM 0.00
- User confused and frustrated
- Manual reconciliation needed

**Fiuu Team Question**:
- Does Fiuu support webhooks/push notifications?
- Can Fiuu send real-time callback when payment succeeds?
- This would eliminate polling and prevent lost payments

---

### ⚠️ Problem 2: QR Code Expiration Handling
**Issue**: User scans QR but doesn't complete payment in time

**Current Behavior**:
```
QR code expires after 15 minutes
↓
User scans at 14:30 remaining
↓
User's banking app takes 2 minutes to load
↓
QR expired → Payment fails → User confused
```

**Impact**:
- Poor user experience
- User needs to start over
- Lost conversion

**Fiuu Team Question**:
- What is the default QR expiration time?
- Can we extend expiration to 10 minutes?
- Can we refresh/extend QR if user is still on payment screen?

---

### ⚠️ Problem 3: Multiple Payment Methods Confusion
**Issue**: Users don't understand which payment method to choose

**Current Options**:
- DuitNow QR
- Touch 'n Go eWallet
- Boost
- GrabPay
- ShopeePay
- Banking apps

**User Confusion**:
```
"Which one should I use?"
"I have Maybank app, do I use DuitNow or eWallet?"
"What's the difference?"
```

**Impact**:
- High drop-off rate on payment selection
- Users abandon top-up process
- Lost transactions

**Fiuu Team Question**:
- Can we use a single universal QR that works with all methods?
- Does Fiuu support "Smart QR" that detects user's app automatically?
- Can we simplify the payment method selection?

---

### ⚠️ Problem 4: Partial Refunds for Unused Pre-Authorization
**Issue**: User authorizes RM 50 but only uses RM 30

**Scenario**:
```
User authorizes: RM 50.00
Wallet hold: RM 50.00
↓
User dispenses: RM 30.00 (14.6 L)
User stops manually or tank gets full
↓
System needs to:
1. Deduct actual: RM 30.00
2. Release hold: RM 50.00
3. Return difference: RM 20.00
```

**Current Question**:
- With Fiuu QR payments, the full amount is already paid (RM 50)
- If user only uses RM 30, we need to refund RM 20
- What is Fiuu's refund API/process?
- How long does refund take to reach user?
- Can refunds go back to original payment method or only to wallet?

**Impact**:
- Affects user trust
- Slow refunds = poor experience
- May need separate refund process

---

### ⚠️ Problem 5: Network Disconnection During Payment
**Issue**: User loses internet connection during top-up

**Scenario**:
```
User at station with weak signal
↓
Starts wallet top-up → QR displayed
↓
Connection drops
↓
User scans QR with banking app (banking app has own internet)
↓
Payment succeeds in Fiuu
↓
App still offline → Cannot poll status → Wallet not updated
```

**Impact**:
- User paid but wallet shows RM 0.00
- User tries to top-up again
- Double payment risk
- Poor user experience

**Fiuu Team Question**:
- How to handle offline scenarios?
- When app reconnects, can it query missed transactions?
- Does Fiuu provide transaction history API?
- Can we reconcile based on user ID + timestamp?

---

### ⚠️ Problem 6: Transaction ID Mismatch
**Issue**: Linking mobile app transaction with Fiuu payment

**Challenge**:
```
App generates: Transaction ID "TOPUP-20260531-12345"
↓
Sends to backend → Backend creates Fiuu payment
↓
Fiuu returns: Payment ID "FP-67890-XYZ"
↓
Webhook callback includes: Fiuu Payment ID
↓
Need to match: Mobile App Transaction ↔ Fiuu Payment ↔ Backend Record
```

**Current Concern**:
- If webhook arrives before app polls
- If transaction IDs don't match
- If user contacts support

**Fiuu Team Question**:
- Best practice for transaction ID mapping?
- Can we pass custom reference in QR creation?
- Does webhook callback include our original transaction ID?
- How to handle duplicate/retry scenarios?

---

## Proposed Solutions

### Solution 1: Real-Time Webhooks
**Request**: Fiuu implements push notifications instead of polling

**Benefit**:
- Instant wallet update
- No polling overhead
- Works even if user closes app
- Better reliability

### Solution 2: Universal QR Code
**Request**: Single QR that works with all payment methods

**Benefit**:
- Simplified user experience
- Higher conversion rate
- Less confusion
- Faster top-up

### Solution 3: Transaction Reconciliation API
**Request**: Backend can query Fiuu for transaction status by date range

**Benefit**:
- Can recover missed transactions
- Works after offline period
- Prevents double payment
- Audit trail

### Solution 4: Extended QR Expiration
**Request**: Increase QR validity to 30 minutes

**Benefit**:
- User has more time
- Less failed payments
- Better experience
- Higher success rate

---

## Questions for Fiuu Team

### Technical Integration
1. Do you support **webhook callbacks** for real-time payment notifications?
2. What is your recommended **polling interval** if webhooks not available?
3. Do you provide a **transaction reconciliation API** to query missed payments?

### QR Payment
4. What is the **default QR code expiration** time?
5. Can we **extend QR validity** to 30+ minutes?
6. Do you support **universal/smart QR** that works with all payment methods?

### Refunds
7. What is your **refund API endpoint** and process?
8. How long do refunds take to reach user's account?
9. Can refunds go to **original payment method** or only to our app wallet?

### Error Handling
10. How to handle **user closes app during payment**?
11. How to handle **network disconnection** scenarios?
12. How to prevent **duplicate payments** if user retries?

### Transaction Tracking
13. Best practice for **transaction ID mapping** (App ↔ Fiuu ↔ Backend)?
14. Does webhook include our **custom transaction reference**?
15. How to handle **webhook retry** if our server is down?

---

## Success Metrics

### What Success Looks Like
- ✅ QR payment completes in < 30 seconds
- ✅ Wallet updates immediately after payment
- ✅ Zero failed top-ups due to timeout
- ✅ Zero double payments
- ✅ 95%+ payment success rate
- ✅ Refunds processed within 24 hours

### Current Challenges
- ⚠️ Polling-based status check is unreliable
- ⚠️ Users confused by multiple payment options
- ⚠️ App must stay open during entire payment process
- ⚠️ No recovery mechanism for offline scenarios

---

## Next Steps

1. **Schedule Technical Discussion** with Fiuu integration team
2. **Review Fiuu API Documentation** for webhooks and reconciliation
3. **Implement Test Integration** in sandbox environment
4. **Test Edge Cases**: offline, timeout, duplicate, refund scenarios
5. **Prepare Fallback Strategy** if webhooks not available

---

**Contact**:  
Development Team: MY Diesel  
Integration Date: TBD  
Expected Launch: Q2 2026
