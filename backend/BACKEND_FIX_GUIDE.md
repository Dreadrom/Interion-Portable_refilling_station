# BACKEND FIX GUIDE
## Fix All Backend Issues - No App Rebuild Required! 🎉

**Good News:** Backend fixes are server-side only. Your beta users will automatically get the fixes without downloading a new app version!

---

## Step 1: Update Database Schema ⭐ CRITICAL

Your database is missing critical tables. Run this script to add them:

### Windows (PowerShell):
```powershell
cd backend

# Connect to your RDS database and run the complete schema
$env:PGPASSWORD="your-database-password"
psql -h your-rds-endpoint.region.rds.amazonaws.com -U postgres -d portable_refill -f database/complete-schema.sql
```

### Alternative: Using DBeaver or pgAdmin:
1. Open DBeaver/pgAdmin
2. Connect to your RDS database
3. Open `backend/database/complete-schema.sql`
4. Execute the entire file (F5 in DBeaver)

**What this adds:**
- ✅ Stations table
- ✅ Tanks table
- ✅ Pricing table
- ✅ Pumps table
- ✅ Payments table
- ✅ Wallets table
- ✅ Wallet Transactions table
- ✅ Transactions table (dispense records)
- ✅ Alarms table
- ✅ Sample data (1 test station)

---

## Step 2: Fix API Gateway Routing ⭐ CRITICAL

Your API Gateway is not routing all paths to your Lambda function. This is why you're getting 404 errors.

### Option A: Use Catch-All Route (Recommended - 5 minutes)

1. **Go to AWS Console** → API Gateway
2. **Find your API** (probably named "PortableRefillAPI" or similar)
3. **Click "Resources"** in left sidebar
4. **Click "Actions"** dropdown → **"Create Resource"**
5. Configure:
   - ✅ Check "Configure as proxy resource"
   - Resource Name: `proxy`
   - Resource Path: `{proxy+}`
   - ✅ Check "Enable API Gateway CORS"
6. **Click "Create Resource"**
7. When prompted to add ANY method:
   - Integration type: **Lambda Function Proxy**
   - Lambda Function: `AuthHandler` (select your auth lambda)
   - Click **Save**
8. **Click "Actions"** → **"Deploy API"**
9. Select your stage (e.g., "prod" or "staging")
10. Click **Deploy**

### Option B: Add Individual Routes (30 minutes)

If you prefer explicit routing, add each route manually:

**Auth Routes:**
- `POST /auth/register` → AuthHandler
- `POST /auth/login` → AuthHandler
- `GET /auth/me` → AuthHandler
- `POST /auth/forgot-password` → AuthHandler
- `POST /auth/reset-password` → AuthHandler
- `POST /auth/logout` → AuthHandler

**User Routes:**
- `POST /user/update` → AuthHandler
- `POST /user/change-password` → AuthHandler

**Station Routes:**
- `GET /stations` → StationsHandler
- `GET /stations/{id}` → StationsHandler

**For each route:**
1. Create Resource
2. Create Method
3. Integration type: Lambda Function Proxy
4. Select appropriate handler
5. Enable CORS

Then **Deploy** the API.

---

## Step 3: Test the Fixes

After deploying, run the test script:

```powershell
cd portable-refill-app/playstore
node backend-test.js
```

**Expected Output:**
```
✅ Passed: 7
❌ Failed: 0
Success Rate: 100%
🎉 All tests passed! Your backend is working correctly.
```

---

## Step 4: Test in the App

Do this immediately on your phone:

1. **Open the app**
2. **Create a new account**
   - Use your real email
   - Password: Test@123456
3. **Log out**
4. **Log in again**
5. **Go to Profile** → Update your name
6. **Go to Settings** → Change password
7. **View Stations list** (should show "AceRev AdBlue Station KL")

If all of these work, you're 100% ready! ✅

---

## Step 5: Add More Stations (Optional)

To add your real stations to the database:

```sql
-- Example: Add a new station
INSERT INTO Stations (
    StationID, 
    StationName, 
    Address, 
    Latitude, 
    Longitude,
    Status,
    PTSHost,
    PTSPort,
    Enabled
)
VALUES (
    'station-002',
    'AceRev Station Petaling Jaya',
    '45 Jalan SS2/24, Petaling Jaya',
    3.1150,
    101.6228,
    'IDLE',
    'your-pts-controller-ip',  -- Your PTS-2 controller IP
    8080,
    TRUE
);

-- Add tank for the station
INSERT INTO Tanks (TankID, StationID, Product, CapacityLitres, LevelLitres)
VALUES ('tank-002', 'station-002', 'AdBlue', 10000.00, 8000.00);

-- Add pricing
INSERT INTO Pricing (PricingID, StationID, Product, UnitPrice, Currency)
VALUES ('price-002', 'station-002', 'AdBlue', 2.50, 'MYR', CURRENT_TIMESTAMP);

-- Add pump
INSERT INTO Pumps (PumpID, StationID, PumpNumber, Status)
VALUES ('pump-002', 'station-002', 1, 'IDLE');
```

---

## Troubleshooting

### Issue: Still getting 404 errors
**Solution:** 
1. Verify API Gateway deployment completed
2. Check the API endpoint URL matches your app's `.env` file
3. Try adding `OPTIONS` method for CORS

### Issue: "No token provided" error
**Solution:**
1. Make sure API Gateway has **Lambda Proxy Integration** enabled
2. Check that Authorization header is being passed through

### Issue: Database connection timeout
**Solution:**
1. Check RDS security group allows inbound on port 5432
2. Verify Lambda is in same VPC as RDS (if using VPC)
3. Check DB credentials in Lambda environment variables

### Issue: Sample station not appearing
**Solution:**
```sql
-- Check if station exists
SELECT * FROM Stations;

-- If empty, run the complete-schema.sql again
```

---

## Do I Need to Upload a New App Release? ❌ NO!

**You do NOT need to upload a new app version because:**
- ✅ These are backend/server-side fixes
- ✅ Your app code hasn't changed
- ✅ The API URL is the same
- ✅ Beta users will automatically get the fixes

**When you DO need a new release:**
- If you change app features
- If you fix UI bugs
- If you update the app icon
- If you change the app version number

---

## Verification Checklist

Before announcing to beta testers:

- [ ] Database schema updated (all tables created)
- [ ] API Gateway routes configured (catch-all or individual)
- [ ] API Gateway deployed to stage
- [ ] Automated tests pass (7/7)
- [ ] Manual test in app successful:
  - [ ] Registration works
  - [ ] Login works
  - [ ] Profile update works
  - [ ] Change password works
  - [ ] Stations list shows data
- [ ] Sample station visible in app
- [ ] No 404 errors in testing

---

## Quick Command Reference

```powershell
# Test backend
cd portable-refill-app/playstore
node backend-test.js

# Update database schema
cd backend
psql -h your-endpoint -U postgres -d portable_refill -f database/complete-schema.sql

# Rebuild backend (if code changes)
cd backend
npm run build

# Re-deploy Lambda (if code changes)
cd dist
Compress-Archive -Path * -DestinationPath ../lambda-deploy.zip -Force
aws lambda update-function-code --function-name AuthHandler --zip-file fileb://lambda-deploy.zip
```

---

## Need Help?

Check these files:
- **TESTING_REPORT.md** - Detailed test results
- **BACKEND_STATUS.md** - Quick status summary
- **complete-schema.sql** - Full database schema
- **backend-test.js** - Automated testing script

---

## Summary

**3 Critical Steps:**
1. ⚡ Run `complete-schema.sql` on your database
2. ⚡ Add catch-all route in API Gateway
3. ⚡ Deploy API Gateway

**Time Required:** 15-30 minutes

**App Release Required:** ❌ NO - Backend only!

**After fixes, your beta users can:**
- ✅ Register and login
- ✅ Update their profile
- ✅ Change password
- ✅ View stations
- ✅ See their wallet balance
- ✅ Start using the full app!

🎯 **Let's fix this now!**
