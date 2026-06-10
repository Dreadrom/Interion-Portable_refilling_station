# BlueDiesel Refill Kiosk - Beta Testing Checklist
**Version:** 1.0.0 (Beta)
**Test Date:** _____________
**Tester Name:** _____________
**Device:** _____________

---

## Pre-Test Setup
- [ ] App installed from Google Play Store (Beta track)
- [ ] Internet connection available
- [ ] Location permission granted
- [ ] Camera permission granted (for QR scanning)

---

## 1. User Registration ⭐ CRITICAL

### Test Steps:
1. [ ] Open the app for the first time
2. [ ] Tap "Create Account" or "Sign Up"
3. [ ] Fill in registration form:
   - [ ] Email: ___________________
   - [ ] Password: ___________________
   - [ ] Name: ___________________
   - [ ] Phone: ___________________
4. [ ] Tap "Register" or "Create Account"

### Expected Results:
- [ ] Registration succeeds
- [ ] No error messages appear
- [ ] Automatically logged in
- [ ] Redirected to home screen or tutorial

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 2. User Login ⭐ CRITICAL

### Test Steps:
1. [ ] Log out (if logged in)
2. [ ] Return to login screen
3. [ ] Enter email: ___________________
4. [ ] Enter password: ___________________
5. [ ] Tap "Login"

### Expected Results:
- [ ] Login succeeds
- [ ] Token stored
- [ ] Redirected to home screen
- [ ] User information displayed correctly

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 3. Profile Management

### Test Steps:
1. [ ] Navigate to Profile/Settings
2. [ ] View current profile information
3. [ ] Tap "Edit Profile"
4. [ ] Update name to: ___________________
5. [ ] Update phone to: ___________________
6. [ ] Save changes

### Expected Results:
- [ ] Profile information displays correctly
- [ ] Can edit profile fields
- [ ] Changes save successfully
- [ ] Updated information displays immediately

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 4. Change Password

### Test Steps:
1. [ ] Go to Profile/Settings
2. [ ] Tap "Change Password"
3. [ ] Enter current password: ___________________
4. [ ] Enter new password: ___________________
5. [ ] Confirm new password: ___________________
6. [ ] Save changes
7. [ ] Log out
8. [ ] Log back in with new password

### Expected Results:
- [ ] Password change succeeds
- [ ] Success message displayed
- [ ] Can log in with new password
- [ ] Cannot log in with old password

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 5. View Stations List

### Test Steps:
1. [ ] Navigate to home screen or stations list
2. [ ] View list of available stations
3. [ ] Check if location-based sorting works
4. [ ] Tap on a station to view details

### Expected Results:
- [ ] Stations list loads successfully
- [ ] Shows station names and addresses
- [ ] Shows distance from current location
- [ ] Shows station status (online/offline)
- [ ] Can tap to view station details

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 6. Station Details

### Test Steps:
1. [ ] Select a station from the list
2. [ ] View station details page
3. [ ] Check displayed information:
   - [ ] Station name
   - [ ] Address
   - [ ] Operating hours
   - [ ] AdBlue price
   - [ ] Tank level/availability
   - [ ] Status indicator

### Expected Results:
- [ ] All station details display correctly
- [ ] Map shows station location
- [ ] "Get Directions" button works
- [ ] Can see if station is available

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 7. QR Code Scanner ⭐ CRITICAL

### Test Steps:
1. [ ] Navigate to "Scan QR" or home screen
2. [ ] Tap "Scan QR Code"
3. [ ] Grant camera permission (if needed)
4. [ ] Point camera at station QR code
5. [ ] Wait for scan to complete

### Expected Results:
- [ ] Camera opens successfully
- [ ] QR code detected and scanned
- [ ] Station information retrieved
- [ ] Proceeds to pre-authorization screen

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 8. Wallet & Top-Up

### Test Steps:
1. [ ] Navigate to Wallet section
2. [ ] View current balance
3. [ ] Tap "Top Up" or "Add Funds"
4. [ ] Select amount: RM___________
5. [ ] Complete payment process
6. [ ] Return to app

### Expected Results:
- [ ] Wallet balance displays correctly
- [ ] Payment gateway opens
- [ ] Can select payment method
- [ ] Payment processes successfully
- [ ] Balance updates after payment
- [ ] Transaction appears in history

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 9. Pre-Authorization ⭐ CRITICAL

### Test Steps:
1. [ ] After scanning QR code
2. [ ] View pre-authorization screen
3. [ ] Check wallet balance
4. [ ] Enter desired amount or liters
5. [ ] Review authorization details
6. [ ] Confirm authorization

### Expected Results:
- [ ] Pre-authorization screen displays
- [ ] Shows available balance
- [ ] Can input amount/liters
- [ ] Shows estimated cost
- [ ] Authorization succeeds
- [ ] Proceeds to pump unlock screen

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 10. Pump Control ⭐ CRITICAL

### Test Steps:
1. [ ] After authorization
2. [ ] View "Pump Unlocked" screen
3. [ ] Start dispensing at pump
4. [ ] Monitor live dispensing screen
5. [ ] Wait for dispensing to complete
6. [ ] View completion screen

### Expected Results:
- [ ] Pump unlocks successfully
- [ ] Instructions clear and visible
- [ ] Live dispensing shows real-time data:
  - [ ] Liters dispensed
  - [ ] Current flow rate
  - [ ] Total cost
  - [ ] Remaining authorized amount
- [ ] Can tap "Stop" to end early
- [ ] Completion screen shows final totals
- [ ] Wallet balance updates correctly

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 11. Transaction History

### Test Steps:
1. [ ] Navigate to Transactions or History
2. [ ] View list of past transactions
3. [ ] Tap on a transaction to view details
4. [ ] Check transaction information

### Expected Results:
- [ ] All transactions displayed
- [ ] Shows date, time, station, amount
- [ ] Can view full transaction details
- [ ] Can see receipt/invoice
- [ ] Can share or download receipt

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 12. Forgot Password

### Test Steps:
1. [ ] Log out
2. [ ] Go to login screen
3. [ ] Tap "Forgot Password"
4. [ ] Enter email: ___________________
5. [ ] Submit request
6. [ ] Check email for reset link/code
7. [ ] Complete password reset

### Expected Results:
- [ ] Email sent successfully
- [ ] Receives reset email
- [ ] Reset link/code works
- [ ] Can set new password
- [ ] Can log in with new password

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 13. App Navigation & UI

### Test Steps:
1. [ ] Navigate through all screens
2. [ ] Test back button on each screen
3. [ ] Test tab navigation (if applicable)
4. [ ] Test menu/drawer (if applicable)

### Expected Results:
- [ ] All screens accessible
- [ ] Navigation smooth and intuitive
- [ ] Back button works correctly
- [ ] No broken links or dead ends
- [ ] UI elements properly aligned
- [ ] Text readable on all screens

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 14. Error Handling

### Test Steps:
1. [ ] Turn off internet connection
2. [ ] Try to perform actions
3. [ ] Check error messages
4. [ ] Turn internet back on
5. [ ] Try invalid inputs (wrong password, etc.)

### Expected Results:
- [ ] Shows "No internet" message
- [ ] Graceful error handling
- [ ] Error messages are clear and helpful
- [ ] App doesn't crash
- [ ] Can recover from errors

### Actual Results:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## 15. Performance & Stability

### Observations:
- [ ] App launches quickly (< 3 seconds)
- [ ] Screens load without lag
- [ ] Smooth animations
- [ ] No crashes during testing
- [ ] Battery usage acceptable
- [ ] App size acceptable

### Issues Encountered:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

**Status:** ❌ Failed / ⚠️ Issues / ✅ Passed

---

## Overall Test Summary

### Critical Issues (Must Fix Before Release):
```
1. _________________________________________________________
2. _________________________________________________________
3. _________________________________________________________
```

### Major Issues (Should Fix):
```
1. _________________________________________________________
2. _________________________________________________________
3. _________________________________________________________
```

### Minor Issues (Nice to Fix):
```
1. _________________________________________________________
2. _________________________________________________________
3. _________________________________________________________
```

### Positive Feedback:
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

### Overall Rating: ⭐⭐⭐⭐⭐

**Would you recommend this app to other drivers?**
- [ ] Yes, definitely
- [ ] Yes, with improvements
- [ ] Undecided
- [ ] No, needs major work

---

## Additional Comments
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```

---

**Tester Signature:** _______________ **Date:** _______________

---

## For Development Team

### Follow-up Actions Required:
- [ ] Review all failed tests
- [ ] Prioritize critical issues
- [ ] Create bug tickets
- [ ] Schedule fixes
- [ ] Retest after fixes

**Developer Notes:**
```
_______________________________________________________________
_______________________________________________________________
_______________________________________________________________
```
