# COMPLETE BACKEND FIX - Step by Step

## ✅ COMPLETED: Alpha Testers CSV
Created: `portable-refill-app/playstore/alpha-testers.csv` with 9 email addresses

---

## 🔧 FIX 1: Update Database Schema

Your database is on your server (cp1.interion.com.sg), not locally.

### Option A: SSH into Server (Recommended)

1. **SSH into your server:**
```bash
ssh user@cp1.interion.com.sg
```

2. **Navigate to backend folder:**
```bash
cd /path/to/backend
```

3. **Run the update script:**
```bash
node update-database.js
```

### Option B: Use Remote Database Connection

1. **Update backend/.env file:**
   Change `DB_HOST` from `127.0.0.1` to your server's IP or domain:
   ```
   DB_HOST=cp1.interion.com.sg
   ```

2. **Make sure PostgreSQL allows remote connections:**
   - Check `postgresql.conf`: `listen_addresses = '*'`
   - Check `pg_hba.conf`: Add your IP to allowed hosts
   - Restart PostgreSQL

3. **Run the update:**
```powershell
cd backend
node update-database.js
```

### Option C: Run SQL Directly on Server

1. **SSH into your server**
2. **Run SQL directly:**
```bash
psql -U gasapp -d gasapp -f /path/to/backend/database/complete-schema.sql
```

### Option D: Use GUI Tool (DBeaver/pgAdmin)

1. **Install DBeaver** (https://dbeaver.io/download/)
2. **Connect to database:**
   - Host: cp1.interion.com.sg
   - Port: 5432
   - Database: gasapp
   - User: gasapp
   - Password: GasApp2026!
3. **Open SQL Editor**
4. **Copy content from** `backend/database/complete-schema.sql`
5. **Execute** (F5 or Execute button)

---

## 🔧 FIX 2: API Gateway Routing

### Step 1: Access AWS Console
1. Go to **AWS Console** (https://console.aws.amazon.com)
2. Navigate to **API Gateway**

### Step 2: Find Your API
1. Look for an API named something like:
   - "PortableRefillAPI"
   - "AuthAPI"
   - Or check which API is serving `cp1.interion.com.sg`

### Step 3: Add Catch-All Route
1. Click on your API
2. Click **"Resources"** in left sidebar
3. Click **"Actions"** dropdown → **"Create Resource"**
4. In the dialog:
   - ✅ Check **"Configure as proxy resource"**
   - Resource Name: `proxy`
   - Resource Path: `{proxy+}` (should auto-fill)
   - ✅ Check **"Enable API Gateway CORS"**
5. Click **"Create Resource"**

### Step 4: Configure Integration
1. When prompted to add method:
   - Integration type: **Lambda Function Proxy**
   - ✅ Check **"Use Lambda Proxy integration"**
   - Lambda Function: Select `AuthHandler` (or your auth lambda name)
   - Click **"Save"**
   - Click **"OK"** to grant permissions

### Step 5: Deploy API
1. Click **"Actions"** → **"Deploy API"**
2. Deployment stage: Select your stage (e.g., "prod" or "staging")
3. Click **"Deploy"**

### Step 6: Verify Endpoint
Your API endpoint should remain: `https://cp1.interion.com.sg`

---

## 🔧 FIX 3: Test Everything

### Test 1: Automated Backend Tests
```powershell
cd portable-refill-app/playstore
node backend-test.js
```

**Expected Result:**
```
✅ Passed: 7
❌ Failed: 0
Success Rate: 100%
```

### Test 2: Manual App Test
1. Open your beta app on phone
2. Create new account
3. Log out and log in
4. Go to Profile → Update name
5. Go to Settings → Change password
6. View Stations list

All should work! ✅

---

## 📊 What Each Fix Does

### Fix 1: Database Schema
**Adds these tables:**
- ✅ Stations (store refilling stations)
- ✅ Tanks (track AdBlue tank levels)
- ✅ Pricing (AdBlue prices per station)
- ✅ Pumps (pump hardware info)
- ✅ Payments (payment transactions)
- ✅ Wallets (user wallet balances)
- ✅ WalletTransactions (wallet history)
- ✅ Transactions (dispense records)
- ✅ Alarms (system alerts)

**Adds sample data:**
- 1 test station in Kuala Lumpur
- 1 tank with AdBlue
- Pricing (RM 2.50/liter)
- 1 pump

### Fix 2: API Gateway
**Fixes these endpoints:**
- ✅ POST /user/update (profile updates)
- ✅ POST /user/change-password (password changes)
- ✅ GET /transactions (transaction history)
- ✅ All other endpoints that were returning 404

### Fix 3: Testing
**Verifies:**
- ✅ All 7 API endpoints working
- ✅ User can register, login, update profile
- ✅ Stations data visible
- ✅ Full app functionality

---

## 🆘 Troubleshooting

### Issue: Can't connect to database
**Solutions:**
- If database is on server: Use Option A (SSH) or Option D (DBeaver)
- If using remote connection: Check firewall/security groups
- Make sure PostgreSQL service is running

### Issue: Don't have SSH access
**Solution:** Use Option D (DBeaver) - it's a free GUI tool that can connect remotely

### Issue: Still getting 404 after API Gateway fix
**Solutions:**
1. Verify the deployment completed
2. Try clearing your app cache
3. Check API Gateway logs in CloudWatch
4. Make sure the catch-all route was created at the root level

### Issue: Sample station not showing
**Solution:** The database update might not have run. Check:
```sql
SELECT COUNT(*) FROM Stations;
```
Should return at least 1.

---

## ⏱️ Time Estimate

- **Fix 1 (Database):** 5-10 minutes
- **Fix 2 (API Gateway):** 5-10 minutes
- **Fix 3 (Testing):** 5 minutes
- **Total:** 15-25 minutes

---

## ✅ Verification Checklist

After completing all fixes:

- [ ] Database schema updated (all tables exist)
- [ ] Sample station data inserted
- [ ] API Gateway catch-all route added
- [ ] API Gateway deployed
- [ ] Automated tests pass (7/7)
- [ ] Can create account in app
- [ ] Can login in app
- [ ] Can update profile in app
- [ ] Can view stations in app
- [ ] Alpha testers CSV created

---

## 📧 Alpha Testers

CSV file created at: `portable-refill-app/playstore/alpha-testers.csv`

**9 alpha testers:**
1. ryanpeh@gmail.com
2. aceplexsg@gmail.com
3. interion.sg@gmail.com
4. ryanpeh@interion.com.sg
5. jianhui@interion.com.sg
6. ryanpeh@bluediesel.com.my
7. yeeloh@bluediesel.com.my
8. lchong@hoorfatt.com
9. foonyee@hoorfatt.com

Upload this file to Google Play Console when setting up your alpha testing track.

---

## 🎯 Need Help?

**I recommend:**
1. Start with **Option D (DBeaver)** for the database - it's the easiest
2. For API Gateway, follow the Step-by-Step guide above
3. Then run the tests

**Which option should you use?**
- Have SSH access? → Option A
- Want GUI tool? → Option D (DBeaver) - **EASIEST**
- Have DBA experience? → Option C

Let me know which option you'll use and I can provide more specific guidance!
