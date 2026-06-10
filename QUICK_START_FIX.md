# QUICK START - Fix Backend in 15 Minutes

## ✅ DONE: Alpha Testers CSV
**File:** `portable-refill-app/playstore/alpha-testers.csv` (9 testers)

---

## 🚀 Option 1: Easiest Method (DBeaver)

### Step 1: Install DBeaver (2 minutes)
Download: https://dbeaver.io/download/

### Step 2: Connect to Database (1 minute)
1. Open DBeaver
2. New Connection → PostgreSQL
3. Enter:
   - **Host:** cp1.interion.com.sg
   - **Port:** 5432
   - **Database:** gasapp
   - **User:** gasapp
   - **Password:** GasApp2026!
4. Click "Test Connection" → Should succeed
5. Click "Finish"

### Step 3: Update Database (5 minutes)
1. Right-click connection → SQL Editor → New SQL Script
2. Open this file: `backend/database/complete-schema.sql`
3. Copy ALL content and paste into DBeaver
4. Press **F5** or click **Execute**
5. Wait for completion (should see success messages)
6. Done! ✅

### Step 4: Fix API Gateway (5 minutes)
1. AWS Console → API Gateway
2. Your API → Resources → Actions → Create Resource
3. ✅ Check "Configure as proxy resource"
4. Integration: Lambda Function Proxy → Select `AuthHandler`
5. Actions → Deploy API
6. Done! ✅

### Step 5: Test (2 minutes)
```powershell
cd portable-refill-app\playstore
node backend-test.js
```
Should show: **7/7 tests passed** ✅

---

## 🚀 Option 2: If You Have SSH Access

### Step 1: SSH and Run Script
```bash
ssh user@cp1.interion.com.sg
cd /path/to/backend
node update-database.js
```

### Step 2: Fix API Gateway (same as above)

### Step 3: Test (same as above)

---

## 🚀 Option 3: Copy-Paste SQL

If DBeaver connection works but you want to run SQL directly:

1. Open DBeaver SQL Editor
2. Copy-paste this entire file: `backend/database/complete-schema.sql`
3. Execute (F5)
4. Verify:
```sql
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
```
Should show: Stations, Tanks, Payments, etc. ✅

---

## ✅ Success Criteria

After all fixes:
- [ ] DBeaver connects successfully
- [ ] SQL execution completes without errors  
- [ ] Can see tables: Stations, Tanks, Payments, etc.
- [ ] API Gateway has catch-all route
- [ ] API Gateway deployed
- [ ] `node backend-test.js` shows 7/7 passed
- [ ] Can register/login/update profile in app

---

## 📧 Upload to Play Store

**Alpha testers CSV:** `portable-refill-app/playstore/alpha-testers.csv`
**Beta testers CSV:** `portable-refill-app/playstore/beta-testers.csv`

In Google Play Console:
1. Go to Testing → Internal testing (Alpha) or Closed testing (Beta)
2. Create new release or manage testers
3. Upload the CSV file
4. Save

---

## Need Help?

**Can't connect with DBeaver?**
- Check if port 5432 is open on server
- Verify PostgreSQL allows remote connections
- Try SSH option instead

**API Gateway not working?**
- Make sure you deployed after creating resource
- Check CloudWatch logs for errors
- Verify Lambda function name matches

**Still getting 404 errors?**
- Clear app cache and retry
- Check API endpoint URL in app matches deployed URL
- Verify catch-all route is at root level

---

## ⏱️ Total Time: ~15 minutes

**I recommend using Option 1 (DBeaver) - it's the easiest!**

Start here: https://dbeaver.io/download/
