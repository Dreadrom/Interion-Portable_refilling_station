# Quick Reference - Commands to Run

**For Server Person - Copy & Paste These Commands**

---

## 🎯 STEP 1: Connect to Database

```bash
psql -h portable-refill-db.XXXXX.ap-southeast-1.rds.amazonaws.com -U admin -d portable_refill_db -p 5432
```

*Replace XXXXX with your actual endpoint*

---

## 🎯 STEP 2: Run Schema File

**Option A - From Command Line:**
```bash
psql -h YOUR_ENDPOINT -U admin -d portable_refill_db -f C:/Users/songj/Interion/Interion-Portable_refilling_station/backend/database/schema.sql
```

**Option B - After Connected:**
```sql
\i C:/Users/songj/Interion/Interion-Portable_refilling_station/backend/database/schema.sql
```

---

## 🎯 STEP 3: Verify Setup

Run these verification queries:

```sql
-- 1. List all tables (should show 3)
\dt

-- 2. Check admin user exists
SELECT UserID, UserEmail, UserName, UserRole FROM Users WHERE UserRole = 'ADMIN';

-- 3. Count tables (should return 3)
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';

-- 4. Check indexes (should return 5)
SELECT COUNT(*) FROM pg_indexes WHERE schemaname = 'public';

-- 5. List all tables with row counts
SELECT 
    schemaname,
    tablename,
    (SELECT COUNT(*) FROM Users) as users_count,
    (SELECT COUNT(*) FROM RefreshTokens) as tokens_count,
    (SELECT COUNT(*) FROM PasswordResetTokens) as reset_tokens_count
FROM pg_tables 
WHERE schemaname = 'public' 
LIMIT 1;
```

---

## ✅ EXPECTED RESULTS

### After \dt command:
```
           List of relations
 Schema |        Name          | Type  | Owner
--------+----------------------+-------+-------
 public | passwordresettokens  | table | admin
 public | refreshtokens        | table | admin
 public | users                | table | admin
```

### After admin user query:
```
  userid   |          useremail           |   username    | userrole
-----------+------------------------------+---------------+----------
 admin-001 | admin@portable-refill.com    | System Admin  | ADMIN
```

---

## 🔑 LOGIN CREDENTIALS TO TEST

**Email:** admin@portable-refill.com  
**Password:** Admin123!

*(This is the default admin account created by the schema)*

---

## 📋 INFORMATION TO SEND BACK

After completing setup, send these:

```
Database Endpoint: [COPY FROM AWS CONSOLE]
Port: 5432
Username: admin
Password: [YOUR CHOSEN PASSWORD]
Database Name: portable_refill_db
Region: ap-southeast-1

✅ Tables Created (3): Users, RefreshTokens, PasswordResetTokens
✅ Admin User Created: admin@portable-refill.com
✅ All Indexes Created (5)
✅ All Triggers Created (1)
```

---

## 🆘 IF YOU GET ERRORS

### Error: "could not connect"
```bash
# Check if database is available in AWS Console
# Status should show "Available" (green)
# Check security group allows port 5432
```

### Error: "database does not exist"
```sql
-- Create database first
CREATE DATABASE portable_refill_db;
-- Then reconnect and run schema
```

### Error: "permission denied"
```bash
# Check username and password are correct
# Master username should be: admin
```

### Error: "relation already exists"
```
# This is OK - means tables already created
# You can skip and continue with verification
```

---

## 📞 CONTACT INFO

After completion, message back with:
1. Database endpoint
2. Password (via secure channel)
3. Confirmation: "All 3 tables created successfully"
4. Screenshot of \dt command output (optional)

---

**That's it! These are all the commands needed.**
