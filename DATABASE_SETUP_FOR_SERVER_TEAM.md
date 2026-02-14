# Database Setup Instructions for Server Team

**Project:** Portable Refill Station  
**Database Type:** PostgreSQL 16.4  
**Region:** ap-southeast-1 (Singapore)

---

## DATABASE CONFIGURATION

### RDS Instance Settings
```
DB Instance Identifier: portable-refill-db
Engine: PostgreSQL 16.4
Instance Class: db.t3.micro
Storage: 20 GB (General Purpose SSD gp3)
Master Username: admin
Master Password: [SET SECURE PASSWORD]
Initial Database Name: portable_refill_db
Port: 5432
Public Access: Yes (for testing)
Backup Retention: 7 days
Multi-AZ: No (for cost saving)
```

---

## DATABASE SCHEMA DETAILS

### Custom Types Required

#### 1. Enum Type: `user_role`
```sql
CREATE TYPE user_role AS ENUM ('ADMIN', 'DRIVER');
```

**Purpose:** Defines user roles in the system  
**Values:**
- `ADMIN` - System administrator with full access
- `DRIVER` - Regular user who refuels vehicles

---

## TABLES TO CREATE

### Table 1: `Users`

**Purpose:** Store user accounts and authentication data

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `UserID` | VARCHAR(36) | PRIMARY KEY | Unique user identifier (UUID format) |
| `UserEmail` | VARCHAR(255) | UNIQUE NOT NULL | User's email address (login) |
| `UserPassword` | VARCHAR(255) | NOT NULL | Bcrypt hashed password |
| `UserName` | VARCHAR(255) | NOT NULL | User's full name |
| `UserPhone` | VARCHAR(50) | NULL | Phone number (optional) |
| `UserRole` | user_role | DEFAULT 'DRIVER' | User role (ADMIN or DRIVER) |
| `CreatedAt` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |
| `UpdatedAt` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update time |

**Indexes:**
- `idx_users_email` ON `UserEmail` - Fast email lookup for login

**Triggers:**
- `update_users_updated_at` - Automatically updates `UpdatedAt` on row update

**Sample Data:**
```sql
-- Default admin user
UserID: 'admin-001'
Email: 'admin@portable-refill.com'
Password: 'Admin123!' (hashed)
Name: 'System Admin'
Role: 'ADMIN'
```

---

### Table 2: `RefreshTokens`

**Purpose:** Store JWT refresh tokens for authentication

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `TokenID` | VARCHAR(36) | PRIMARY KEY | Unique token identifier |
| `UserID` | VARCHAR(36) | NOT NULL, FOREIGN KEY | References Users(UserID) |
| `Token` | TEXT | NOT NULL | JWT refresh token string |
| `ExpiresAt` | TIMESTAMP | NOT NULL | Token expiration timestamp |
| `CreatedAt` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Token creation time |

**Foreign Keys:**
- `UserID` REFERENCES `Users(UserID)` ON DELETE CASCADE

**Indexes:**
- `idx_refreshtokens_user` ON `UserID` - Fast lookup by user
- `idx_refreshtokens_expires` ON `ExpiresAt` - Fast cleanup of expired tokens

---

### Table 3: `PasswordResetTokens`

**Purpose:** Store one-time tokens for password reset flow

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `TokenID` | VARCHAR(36) | PRIMARY KEY | Unique token identifier |
| `UserID` | VARCHAR(36) | NOT NULL, FOREIGN KEY | References Users(UserID) |
| `Token` | VARCHAR(255) | UNIQUE NOT NULL | Reset token (sent via email) |
| `ExpiresAt` | TIMESTAMP | NOT NULL | Token expiration (typically 1 hour) |
| `Used` | BOOLEAN | DEFAULT FALSE | Whether token has been used |
| `CreatedAt` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Token creation time |

**Foreign Keys:**
- `UserID` REFERENCES `Users(UserID)` ON DELETE CASCADE

**Indexes:**
- `idx_passwordreset_token` ON `Token` - Fast lookup by token
- `idx_passwordreset_user` ON `UserID` - Fast lookup by user

---

## FUNCTIONS TO CREATE

### Function: `update_updated_at_column()`

**Purpose:** Automatically update the `UpdatedAt` column when a row is modified

**Returns:** TRIGGER

**Language:** plpgsql

**Function Body:**
```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.UpdatedAt = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';
```

**Usage:** Called automatically by triggers on UPDATE

---

## TRIGGERS TO CREATE

### Trigger: `update_users_updated_at`

**Table:** Users  
**Event:** BEFORE UPDATE  
**For Each:** ROW  
**Execute:** `update_updated_at_column()`

**SQL:**
```sql
DROP TRIGGER IF EXISTS update_users_updated_at ON Users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON Users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

**Purpose:** Sets `UpdatedAt` to current timestamp whenever a user record is updated

---

## INDEXES SUMMARY

| Index Name | Table | Column(s) | Purpose |
|------------|-------|-----------|---------|
| `idx_users_email` | Users | UserEmail | Speed up login queries |
| `idx_refreshtokens_user` | RefreshTokens | UserID | Speed up token lookups by user |
| `idx_refreshtokens_expires` | RefreshTokens | ExpiresAt | Speed up expired token cleanup |
| `idx_passwordreset_token` | PasswordResetTokens | Token | Speed up token validation |
| `idx_passwordreset_user` | PasswordResetTokens | UserID | Speed up user password reset lookups |

---

## SECURITY GROUP RULES

### Inbound Rules Required

| Type | Protocol | Port | Source | Description |
|------|----------|------|--------|-------------|
| PostgreSQL | TCP | 5432 | 0.0.0.0/0 | Allow from anywhere (testing) |
| PostgreSQL | TCP | 5432 | Lambda Security Group | Allow from Lambda (production) |

**Note:** After testing, restrict `0.0.0.0/0` to specific IP ranges or remove entirely

---

## EXECUTION ORDER

Execute SQL in this exact order:

```sql
-- 1. Create custom types
CREATE TYPE user_role AS ENUM ('ADMIN', 'DRIVER');

-- 2. Create Users table
CREATE TABLE IF NOT EXISTS Users (...);

-- 3. Create Users indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON Users(UserEmail);

-- 4. Create update function
CREATE OR REPLACE FUNCTION update_updated_at_column() ...;

-- 5. Create trigger
CREATE TRIGGER update_users_updated_at ...;

-- 6. Create RefreshTokens table
CREATE TABLE IF NOT EXISTS RefreshTokens (...);

-- 7. Create RefreshTokens indexes
CREATE INDEX IF NOT EXISTS idx_refreshtokens_user ON RefreshTokens(UserID);
CREATE INDEX IF NOT EXISTS idx_refreshtokens_expires ON RefreshTokens(ExpiresAt);

-- 8. Create PasswordResetTokens table
CREATE TABLE IF NOT EXISTS PasswordResetTokens (...);

-- 9. Create PasswordResetTokens indexes
CREATE INDEX IF NOT EXISTS idx_passwordreset_token ON PasswordResetTokens(Token);
CREATE INDEX IF NOT EXISTS idx_passwordreset_user ON PasswordResetTokens(UserID);

-- 10. Insert default admin user
INSERT INTO Users (UserID, UserEmail, UserPassword, UserName, UserRole) VALUES (...);
```

---

## VERIFICATION QUERIES

After setup, run these to verify:

### 1. Check Tables Exist
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```
**Expected Output:** 3 tables (users, refreshtokens, passwordresettokens)

### 2. Check Custom Types
```sql
SELECT typname 
FROM pg_type 
WHERE typname = 'user_role';
```
**Expected Output:** 1 row (user_role)

### 3. Check Indexes
```sql
SELECT indexname 
FROM pg_indexes 
WHERE schemaname = 'public';
```
**Expected Output:** 5 indexes listed above

### 4. Check Functions
```sql
SELECT proname 
FROM pg_proc 
WHERE proname = 'update_updated_at_column';
```
**Expected Output:** 1 row (update_updated_at_column)

### 5. Check Triggers
```sql
SELECT trigger_name, event_object_table 
FROM information_schema.triggers 
WHERE trigger_schema = 'public';
```
**Expected Output:** 1 trigger (update_users_updated_at on users)

### 6. Check Admin User Exists
```sql
SELECT UserID, UserEmail, UserName, UserRole 
FROM Users 
WHERE UserRole = 'ADMIN';
```
**Expected Output:** 1 admin user

### 7. Test Foreign Keys
```sql
SELECT
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY';
```
**Expected Output:** 2 foreign keys

---

## SCHEMA FILE LOCATION

The complete SQL file is located at:
```
C:\Users\songj\Interion\Interion-Portable_refilling_station\backend\database\schema.sql
```

**Recommended Method:** Run this file directly:
```bash
psql -h YOUR_ENDPOINT -U admin -d portable_refill_db -f schema.sql
```

---

## CONNECTION DETAILS TO RETURN

After setup is complete, provide these details back:

```
Database Endpoint: _______________________________
Port: 5432
Master Username: admin
Master Password: _______________________________
Database Name: portable_refill_db
Status: Available
Tables Created: 3 (Users, RefreshTokens, PasswordResetTokens)
Admin User Email: admin@portable-refill.com
Admin User Password: Admin123!
Security Group: Allows port 5432 from 0.0.0.0/0
```

---

## TROUBLESHOOTING

### Issue: "relation already exists"
**Solution:** Tables already created - OK to ignore

### Issue: "permission denied"
**Solution:** Wrong username or password

### Issue: "could not connect"
**Solution:** 
1. Check endpoint address
2. Check security group allows port 5432
3. Check database status is "Available"

### Issue: "database does not exist"
**Solution:** Create database first:
```sql
CREATE DATABASE portable_refill_db;
```

---

## EXPECTED COSTS

- **RDS PostgreSQL db.t3.micro:** ~$13/month
- **Storage (20 GB):** ~$2.30/month
- **Backup storage (7 days):** Included
- **Data transfer:** Minimal (~$0.50/month)
- **TOTAL:** ~$15.80/month

**Free Tier:** First 750 hours/month free for 12 months (if eligible)

---

## CONTACT AFTER COMPLETION

Please send back:
1. Database endpoint address
2. Master password (via secure channel)
3. Confirmation that all 3 tables exist
4. Confirmation that admin user can be queried
5. Any error messages encountered

---

**This document contains all technical details needed for database setup.**
