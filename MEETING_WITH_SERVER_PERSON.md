# Meeting Notes: Database & Server Setup

**Date**: February 12, 2026  
**Purpose**: Explain database architecture and setup requirements to server person

---

## 1️⃣ HOW AMAZON DATABASE WORKS IN YOUR APP

### Simple Explanation (Non-Technical)

Think of your system like a restaurant:
- **Mobile App** = Customer (orders food)
- **Backend/Lambda** = Waiter (takes orders, brings food)
- **Database** = Kitchen (stores and prepares everything)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  Mobile App │────────>│   Backend   │────────>│  Database   │
│  (Customer) │         │  (Waiter)   │         │  (Kitchen)  │
└─────────────┘         └─────────────┘         └─────────────┘
     │                         │                        │
     │ "Login please"          │ "Check user"           │
     │────────────────────────>│                        │
     │                         │ Query user table       │
     │                         │───────────────────────>│
     │                         │                        │
     │                         │ User data returned     │
     │                         │<───────────────────────│
     │ "Welcome back!"         │                        │
     │<────────────────────────│                        │
```

### Technical Architecture

```
┌────────────────────────────────────────────────────────────┐
│                         YOUR APP                           │
│                                                            │
│  ┌──────────────┐                                         │
│  │  Mobile App  │ (React Native on user's phone)          │
│  │ portable-    │                                          │
│  │ refill-app   │                                          │
│  └───────┬──────┘                                          │
│          │                                                 │
│          │ HTTPS Requests                                  │
│          │ (Login, Register, Get Stations, etc.)           │
│          ▼                                                 │
│  ┌──────────────────────────────────────────────────┐     │
│  │           AWS API Gateway                         │     │
│  │  https://xxxxx.execute-api.ap-southeast-1...     │     │
│  │  Routes:                                          │     │
│  │    POST /auth/login                               │     │
│  │    POST /auth/register                            │     │
│  │    GET  /stations                                 │     │
│  │    POST /dispense/authorize                       │     │
│  └──────────────────┬───────────────────────────────┘     │
│                     │                                      │
│                     │ Triggers Lambda functions            │
│                     ▼                                      │
│  ┌───────────────────────────────────────────────┐        │
│  │        AWS Lambda Functions                    │        │
│  │  (Your backend code running in the cloud)     │        │
│  │                                                │        │
│  │  ┌─────────────────┐  ┌──────────────────┐   │        │
│  │  │  AuthHandler    │  │ StationsHandler  │   │        │
│  │  │  - Login        │  │ - Get stations   │   │        │
│  │  │  - Register     │  │ - Get tank info  │   │        │
│  │  │  - Get user     │  │ - Update status  │   │        │
│  │  └────────┬────────┘  └────────┬─────────┘   │        │
│  │           │                     │             │        │
│  │           │ SQL Queries         │             │        │
│  │           │ (SELECT, INSERT,    │             │        │
│  │           │  UPDATE, DELETE)    │             │        │
│  │           ▼                     ▼             │        │
│  │  ┌──────────────────────────────────────┐    │        │
│  │  │    PostgreSQL Connection Pool        │    │        │
│  │  │    (Manages database connections)    │    │        │
│  │  └──────────────────┬───────────────────┘    │        │
│  └─────────────────────┼────────────────────────┘        │
│                        │                                  │
│                        │ TCP/IP Connection                │
│                        │ (Host: xxx.rds.amazonaws.com)    │
│                        │ (Port: 5432)                     │
│                        ▼                                  │
│  ┌──────────────────────────────────────────────────┐    │
│  │         AWS RDS PostgreSQL Database              │    │
│  │         (This is what needs to be set up!)       │    │
│  │                                                   │    │
│  │  Tables:                                          │    │
│  │  ┌─────────────────────────────────────────┐    │    │
│  │  │ Users Table                              │    │    │
│  │  ├──────────────┬───────────────────────────┤    │    │
│  │  │ UserID       │ email@example.com        │    │    │
│  │  │ UserEmail    │ password_hash_here       │    │    │
│  │  │ UserPassword │ John Doe                 │    │    │
│  │  │ UserName     │ DRIVER                   │    │    │
│  │  │ UserRole     │ 2026-02-12               │    │    │
│  │  └──────────────┴───────────────────────────┘    │    │
│  │                                                   │    │
│  │  ┌─────────────────────────────────────────┐    │    │
│  │  │ RefreshTokens Table                      │    │    │
│  │  │ (Stores JWT tokens for users)            │    │    │
│  │  └─────────────────────────────────────────┘    │    │
│  │                                                   │    │
│  │  ┌─────────────────────────────────────────┐    │    │
│  │  │ PasswordResetTokens Table                │    │    │
│  │  │ (For "Forgot Password" feature)          │    │    │
│  │  └─────────────────────────────────────────┘    │    │
│  │                                                   │    │
│  └──────────────────────────────────────────────────┘    │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### How Data Flows (Example: User Login)

**Step 1:** User opens app, types email and password, clicks "Login"

**Step 2:** Mobile app sends request:
```
POST https://xxxxx.execute-api.ap-southeast-1.amazonaws.com/auth/login
Body: {
  "email": "user@example.com",
  "password": "MyPassword123"
}
```

**Step 3:** API Gateway receives request, triggers AuthHandler Lambda

**Step 4:** AuthHandler Lambda code runs:
```javascript
1. Extract email and password from request
2. Query database: SELECT * FROM Users WHERE UserEmail = 'user@example.com'
3. Check if user exists
4. Compare password hash
5. If correct: Generate JWT token
6. Return token to mobile app
```

**Step 5:** Database query (PostgreSQL):
```sql
SELECT UserID, UserEmail, UserPassword, UserName, UserRole 
FROM Users 
WHERE UserEmail = 'user@example.com';
```

**Step 6:** Database returns user data to Lambda

**Step 7:** Lambda returns response to mobile app:
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "userId": "user-123",
    "name": "John Doe",
    "email": "user@example.com",
    "role": "DRIVER"
  }
}
```

**Step 8:** Mobile app stores token, shows "Welcome back, John!"

---

## 2️⃣ CURRENT PROBLEMS & WHAT SERVER PERSON NEEDS TO DO

### ❌ Problem Summary

Your backend code is **100% ready** but **cannot run** because:
1. No database exists yet
2. Database tables haven't been created
3. Lambda functions don't have database connection details

It's like having a fully trained waiter (backend) but no kitchen (database) for them to get food from!

---

### ✅ WHAT THE SERVER PERSON NEEDS TO DO

## Task 1: Create AWS RDS PostgreSQL Database (20 minutes)

**What it is:** Amazon's managed database service (you don't manage the server yourself)

**Specifications needed:**
```
Database Engine: PostgreSQL 16.4
Instance Type: db.t3.micro (enough for testing/initial launch)
Storage: 20 GB (expandable later)
Database Name: portable_refill_db
Master Username: admin
Master Password: [CREATE A STRONG PASSWORD]
Region: ap-southeast-1 (Singapore)
Public Access: Yes (for now, restrict later)
Backup: 7 days retention
```

**How to create:**

**Option A - AWS Console (Easy, visual):**
1. Go to AWS Console > RDS
2. Click "Create database"
3. Choose PostgreSQL 16.4
4. Choose db.t3.micro template
5. Fill in database name, username, password
6. Make sure "Public access" is enabled (for now)
7. Wait 15-20 minutes for creation

**Option B - AWS CLI (Command line):**
```bash
aws rds create-db-instance \
  --db-instance-identifier portable-refill-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 16.4 \
  --master-username admin \
  --master-user-password [STRONG_PASSWORD] \
  --allocated-storage 20 \
  --db-name portable_refill_db \
  --publicly-accessible \
  --backup-retention-period 7 \
  --region ap-southeast-1
```

**What you get after creation:**
- **Database Endpoint:** `portable-refill-db.xxxxx.ap-southeast-1.rds.amazonaws.com`
- **Port:** 5432
- **Username:** admin
- **Password:** [whatever was set]
- **Database Name:** portable_refill_db

**⚠️ IMPORTANT:** Write down these 5 things! They're needed for Lambda configuration.

---

## Task 2: Apply Database Schema (5 minutes)

**What it is:** Create the tables (Users, RefreshTokens, etc.) inside the database

**File location:** `backend/database/schema.sql` (already exists in your code)

**How to do it:**

**Option A - Using pgAdmin (GUI tool):**
1. Download pgAdmin (PostgreSQL GUI tool)
2. Connect to RDS endpoint
3. Open schema.sql file
4. Click "Execute"

**Option B - Using psql (Command line):**
```bash
# Install PostgreSQL client first
psql -h portable-refill-db.xxxxx.ap-southeast-1.rds.amazonaws.com \
     -U admin \
     -d portable_refill_db \
     -f backend/database/schema.sql
```

**What happens:**
- Creates 3 tables: Users, RefreshTokens, PasswordResetTokens
- Creates indexes for faster queries
- Creates a default admin user for testing

**How to verify it worked:**
```sql
-- Connect to database, run:
\dt  -- Shows all tables (should see Users, RefreshTokens, PasswordResetTokens)

-- Check if tables have data:
SELECT * FROM Users;  -- Should show 1 admin user
```

---

## Task 3: Configure Lambda Environment Variables (3 minutes)

**What it is:** Tell Lambda functions how to connect to the database

**Required variables:**
```
DB_HOST=portable-refill-db.xxxxx.ap-southeast-1.rds.amazonaws.com
DB_PORT=5432
DB_USER=admin
DB_PASSWORD=[the password from Task 1]
DB_NAME=portable_refill_db
JWT_SECRET=create-a-long-random-string-here-abc123xyz789
JWT_EXPIRES_IN=7d
NODE_ENV=production
```

**How to do it:**

**Option A - AWS Console:**
1. Go to AWS Lambda
2. Select "AuthHandler" function
3. Go to "Configuration" > "Environment variables"
4. Click "Edit"
5. Add all 8 variables above
6. Click "Save"
7. Repeat for "StationsHandler" function

**Option B - AWS CLI:**
```bash
# Update AuthHandler
aws lambda update-function-configuration \
  --function-name AuthHandler \
  --environment Variables="{
    DB_HOST=portable-refill-db.xxxxx.rds.amazonaws.com,
    DB_PORT=5432,
    DB_USER=admin,
    DB_PASSWORD=YourPassword123,
    DB_NAME=portable_refill_db,
    JWT_SECRET=your-secret-key-here,
    JWT_EXPIRES_IN=7d,
    NODE_ENV=production
  }" \
  --region ap-southeast-1

# Update StationsHandler (same command, change function name)
aws lambda update-function-configuration \
  --function-name StationsHandler \
  ...same variables...
```

---

## Task 4: Configure Security Group (5 minutes)

**What it is:** Firewall rules that control who can access the database

**Problem right now:** Lambda functions are in AWS but can't reach database due to firewall

**How to fix:**

1. Go to RDS > portable-refill-db > Connectivity & Security
2. Click on the Security Group link (looks like: sg-xxxxxxxxx)
3. Click "Edit inbound rules"
4. Add rule:
   - **Type:** PostgreSQL
   - **Protocol:** TCP
   - **Port:** 5432
   - **Source:** Custom - Enter Lambda security group ID
   - **Description:** Allow Lambda access
5. Click "Save rules"

**Easier but less secure option (for testing only):**
- Source: Anywhere IPv4 (0.0.0.0/0)
- ⚠️ **MUST RESTRICT THIS LATER IN PRODUCTION!**

---

## Task 5: Test Database Connection (2 minutes)

**Option A - Test with psql:**
```bash
psql -h portable-refill-db.xxxxx.rds.amazonaws.com -U admin -d portable_refill_db -c "SELECT version();"
```
Should show PostgreSQL version if connection works.

**Option B - Test Lambda function:**
1. Go to AWS Lambda > AuthHandler
2. Click "Test" tab
3. Create test event:
```json
{
  "httpMethod": "POST",
  "path": "/auth/login",
  "body": "{\"email\":\"admin@portable-refill.com\",\"password\":\"Admin123!\"}"
}
```
4. Click "Test"
5. Should return success with JWT token

---

## 📋 CHECKLIST FOR SERVER PERSON

Print this and check off each task:

```
☐ Task 1: Create RDS PostgreSQL database
   ☐ Database created
   ☐ Database is "Available" status
   ☐ Write down endpoint: _______________________________
   ☐ Write down password: _______________________________

☐ Task 2: Apply database schema
   ☐ Connected to database successfully
   ☐ Ran schema.sql file
   ☐ Verified tables exist (Users, RefreshTokens, PasswordResetTokens)
   ☐ Verified admin user exists

☐ Task 3: Configure Lambda environment variables
   ☐ Updated AuthHandler with 8 variables
   ☐ Updated StationsHandler with 8 variables
   ☐ Double-checked DB_HOST endpoint is correct
   ☐ Double-checked DB_PASSWORD is correct

☐ Task 4: Configure security group
   ☐ Added PostgreSQL rule to RDS security group
   ☐ Lambda can connect to database

☐ Task 5: Test everything
   ☐ Can connect to database from terminal
   ☐ Lambda test returns success (not error)
   ☐ Tables have data
```

---

## 💰 COST BREAKDOWN

| Item | Configuration | Monthly Cost (USD) |
|------|--------------|-------------------|
| RDS PostgreSQL | db.t3.micro, 20GB | ~$13 |
| Lambda | 1M requests, 512MB | ~$0.20 |
| API Gateway | 1M requests | ~$1.00 |
| Data Transfer | Minimal | ~$0.50 |
| **TOTAL** | | **~$14.70/month** |

**For testing:** Can use AWS Free Tier for first 12 months:
- 750 hours/month RDS db.t3.micro (enough to run 24/7)
- 1M Lambda requests/month
- Result: **Almost FREE for first year!**

---

## 🔧 TROUBLESHOOTING GUIDE

### Problem 1: "Could not connect to database"
**Causes:**
- Wrong endpoint in DB_HOST
- Wrong password in DB_PASSWORD
- Security group blocking connection

**Solution:**
1. Double-check endpoint spelling
2. Try connecting with psql command from terminal
3. Check security group has PostgreSQL rule

---

### Problem 2: "Table does not exist"
**Cause:** Schema not applied

**Solution:**
1. Connect to database
2. Run: `\dt` to list tables
3. If no tables, run schema.sql file again

---

### Problem 3: "Authentication failed"
**Causes:**
- Wrong username
- Wrong password
- User doesn't exist

**Solution:**
1. Check master username (should be "admin")
2. Reset password in RDS console if forgotten
3. Update DB_USER and DB_PASSWORD in Lambda

---

## 📞 WHAT TO TELL ME AFTER MEETING

Please get these 5 things from server person:

1. **Database Endpoint:** `portable-refill-db.xxxxx.ap-southeast-1.rds.amazonaws.com`
2. **Database Password:** (keep secret)
3. **Database Status:** "Available" or still creating?
4. **Schema Applied:** Yes/No (are tables created?)
5. **Lambda Connected:** Yes/No (can Lambda access database?)

Once you have these, I can:
- Update mobile app with API endpoint
- Test login/register from mobile app
- Finish connecting frontend to backend

---

## 🎯 SUCCESS CRITERIA

You'll know everything works when:
1. ✅ Can login to database with psql
2. ✅ Can see 3 tables (Users, RefreshTokens, PasswordResetTokens)
3. ✅ Lambda test returns JWT token (not error)
4. ✅ No "connection failed" or "table not found" errors

---

## ❓ QUESTIONS TO ASK SERVER PERSON

1. **Experience with AWS RDS?** (If no, suggest using AWS Console instead of CLI)
2. **Prefers GUI or command line?** (Choose pgAdmin vs psql accordingly)
3. **How long to complete?** (Should be ~30-40 minutes total)
4. **Can provide database credentials securely?** (Use password manager or secure chat)
5. **Familiar with PostgreSQL?** (If no, I can provide more detailed steps)

---

**Good luck with your meeting! 🚀**
