# Questions for Fiuu Payment Integration Team

**Project**: MY Diesel Mobile App  
**Date**: June 2, 2026  
**Payment Provider**: Fiuu (formerly MOLPay)  
**Payment Model**: Wallet Top-Up System

---

## Technical Integration

### 1. Webhook Callbacks
**Question**: Do you support webhook callbacks for real-time payment notifications?

**Context**: We are currently polling your API every 2-3 seconds to check payment status. This approach is unreliable if the user closes the app during payment, resulting in completed payments not updating the wallet balance.

**Impact**: Critical for preventing lost transactions and improving user experience.

---

### 2. Polling Interval Recommendation
**Question**: What is your recommended polling interval if webhooks are not available?

**Context**: We need to balance server load against payment confirmation speed. Too frequent polling may impact your API performance, while too infrequent polling delays user confirmation.

**Impact**: Affects system performance and user experience.

---

### 3. Transaction Reconciliation API
**Question**: Do you provide a transaction reconciliation API to query missed payments?

**Context**: We need to recover transactions that were completed while the app was offline or during failed polling attempts. This is essential for maintaining accurate wallet balances.

**Impact**: Critical for preventing lost revenue and maintaining data integrity.

---

## QR Payment

### 4. Default QR Code Expiration
**Question**: What is the default QR code expiration time?

**Context**: We need to display accurate countdown timers to users and set proper expectations about how long they have to complete their payment.

**Impact**: User experience and conversion rate.

---

### 5. Extended QR Validity
**Question**: Can we extend QR validity to 30+ minutes?

**Context**: Some users scan the QR code but take additional time to complete payment in their banking app due to app loading times, authentication steps, or insufficient balance checks.

**Impact**: Higher payment success rate and better user experience.

---

### 6. Universal/Smart QR Support
**Question**: Do you support universal/smart QR codes that work with all payment methods?

**Context**: Users are confused about which payment method to select (DuitNow, Touch 'n Go, Boost, GrabPay, Banking apps). A single QR code that automatically detects the user's payment app would simplify the experience.

**Impact**: Reduced user confusion, higher conversion rate, faster checkout.

---

## Refunds

### 7. Refund API and Process
**Question**: What is your refund API endpoint and process?

**Context**: In our fuel dispensing scenario, users may authorize RM 50 but only use RM 30. We need to refund the difference (RM 20) back to the user. We require documentation on:
- API endpoint
- Request/response format
- Required parameters
- Authentication method

**Impact**: Critical for core business functionality.

---

### 8. Refund Processing Time
**Question**: How long do refunds take to reach the user's account?

**Context**: We need to set proper user expectations and communicate refund timelines. This also affects our cash flow management and financial reconciliation processes.

**Impact**: User trust and operational planning.

---

### 9. Refund Destination
**Question**: Can refunds go to the original payment method, or only to our app wallet?

**Context**: This affects our refund workflow design and user experience. Users may prefer refunds to their original payment method rather than being locked into our app wallet.

**Impact**: User experience and refund process design.

---

## Error Handling

### 10. User Closes App During Payment
**Question**: How should we handle scenarios where the user closes the app during payment?

**Context**: If a user closes the app after scanning the QR code but before completing payment in their banking app, our polling stops. The payment may complete in Fiuu, but the wallet balance doesn't update. We need guidance on:
- Best practices for detecting completed payments after app restart
- Grace period for payment completion
- How to query payment status on app restart

**Impact**: Critical for preventing lost transactions and user frustration.

---

### 11. Network Disconnection Scenarios
**Question**: How should we handle network disconnection scenarios?

**Context**: Users may lose internet connection after displaying the QR code. The user can still complete payment in their banking app (which has its own internet), but our app cannot poll for status updates. When the app reconnects, we need to:
- Query all pending transactions
- Update wallet balances for completed payments
- Handle potential duplicates

**Impact**: Critical for reliability at fuel stations with poor connectivity.

---

### 12. Duplicate Payment Prevention
**Question**: How can we prevent duplicate payments if a user retries?

**Context**: If a user thinks their first payment failed (due to slow confirmation or network issues), they may attempt to pay again. We need to:
- Detect duplicate attempts
- Use idempotency keys or transaction IDs
- Return the status of the original transaction

**Impact**: Prevents charging users multiple times and maintains trust.

---

## Transaction Tracking

### 13. Transaction ID Mapping Best Practices
**Question**: What is the best practice for transaction ID mapping between our app, Fiuu, and our backend?

**Context**: Our workflow:
1. App generates transaction ID: "TOPUP-20260602-12345"
2. Backend creates Fiuu payment request
3. Fiuu returns payment ID: "FP-67890-XYZ"
4. Webhook callback includes Fiuu payment ID
5. We need to map all three IDs together for support and reconciliation

**Specific Questions**:
- Can we pass our custom transaction ID in the payment creation request?
- Will this custom ID be included in webhook callbacks?
- What field should we use for our reference ID?

**Impact**: Essential for customer support and financial reconciliation.

---

### 14. Custom Transaction Reference in Webhook
**Question**: Does the webhook callback include our custom transaction reference?

**Context**: When we receive a webhook notification, we need to match it to the original app transaction. Without our custom reference ID in the webhook payload, we can only match by amount and timestamp, which is unreliable.

**Impact**: Reliable transaction matching and automated reconciliation.

---

### 15. Webhook Retry Mechanism
**Question**: How do you handle webhook retries if our server is down?

**Context**: We need to understand:
- How many retry attempts do you make?
- What is the retry interval/backoff strategy?
- How long do you keep retrying?
- Do you provide a webhook history or missed webhook query API?
- What HTTP status codes trigger a retry?

**Impact**: Ensures reliable payment notification delivery during server maintenance or outages.

---

## Summary

**Total Questions**: 15 questions across 5 categories

**Priority Breakdown**:
- **Critical** (Must Address): Questions 1, 3, 7, 10, 11
  - These affect core functionality and prevent lost transactions
  
- **High** (Should Address): Questions 2, 4, 6, 13, 14
  - These significantly impact user experience and operational efficiency
  
- **Medium** (Nice to Have): Questions 5, 8, 9, 12, 15
  - These optimize edge cases and improve overall system robustness

---

## Requested Actions

1. **Technical Discussion**: Schedule a call with Fiuu integration engineers to discuss these questions
2. **API Documentation**: Provide detailed API documentation for webhooks, refunds, and reconciliation
3. **Sandbox Access**: Grant access to sandbox environment for testing edge cases
4. **Test Cases**: Share example payloads for webhooks, refunds, and error scenarios
5. **Support Contact**: Provide dedicated technical support contact for integration issues

---

## Success Criteria

After receiving answers to these questions, we aim to achieve:
- QR payment completion in under 30 seconds
- Wallet updates immediately after payment
- Zero failed top-ups due to timeout
- Zero double payments
- 95%+ payment success rate
- Refunds processed within 24 hours
- 100% transaction reconciliation accuracy

---

**Contact Information**:  
Development Team: MY Diesel  
Email: [Your Email]  
Phone: [Your Phone]  
Integration Timeline: Q2 2026
