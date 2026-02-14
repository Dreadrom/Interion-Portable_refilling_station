# AWS Deployment Issues & Checklist

## ⚠️ CRITICAL ISSUES TO FIX BEFORE DEPLOYMENT

### 1. **NO DATABASE EXISTS YET**
- The backend code expects a PostgreSQL RDS database
- Database environment variables are needed:
  - `DB_HOST` (RDS endpoint)
  - `DB_PORT` (5432)
  - `DB_USER` (admin username)
  - `DB_PASSWORD` (database password)
  - `DB_NAME` (portable_refill_db)
- **Without database**: Lambda functions will fail immediately on any database call

### 2. **Schema Not Applied**
- `backend/database/schema.sql` exists but hasn't been run
- Tables don't exist yet: Users, RefreshTokens, PasswordResetTokens
- **Result**: Register/Login endpoints will fail with "table does not exist" errors

### 3. **Lambda Environment Variables Missing**
- Current deploy script only sets:
  - `NODE_ENV`
  - `JWT_SECRET`
  - `JWT_EXPIRES_IN`
- **MISSING**: All database connection variables

### 4. **Dependencies in Package**
- Need to bundle `node_modules` with Lambda deployment
- Current package includes: bcryptjs, jsonwebtoken, pg, uuid
- Compressed size will be ~5-10 MB

---

## ✅ WHAT WORKS

1. ✅ AWS CLI configured with credentials
2. ✅ Backend TypeScript code compiles successfully
3. ✅ IAM permissions set up (Lambda, API Gateway, RDS, IoT Core, CloudWatch)
4. ✅ All dependencies installed (`node_modules` ready)
5. ✅ Build output in `dist/` folder ready for deployment

---

## 🔧 REQUIRED STEPS (IN ORDER)

### Step 1: Create RDS PostgreSQL Database (15-20 minutes)
```powershell
# Create database instance
aws rds create-db-instance `
  --db-instance-identifier portable-refill-db `
  --db-instance-class db.t3.micro `
  --engine postgres `
  --engine-version 16.4 `
  --master-username admin `
  --master-user-password "YourSecurePassword123!" `
  --allocated-storage 20 `
  --db-name portable_refill_db `
  --publicly-accessible `
  --backup-retention-period 7 `
  --region ap-southeast-1

# Wait for database to be available (10-15 min)
aws rds wait db-instance-available --db-instance-identifier portable-refill-db --region ap-southeast-1

# Get database endpoint
aws rds describe-db-instances --db-instance-identifier portable-refill-db --query 'DBInstances[0].Endpoint.Address' --output text
```

**Cost**: ~$0.018/hour (~$13/month for t3.micro)

### Step 2: Apply Database Schema
```powershell
# Install PostgreSQL client (if not installed)
# winget install PostgreSQL.PostgreSQL

# Connect and run schema
psql -h <RDS_ENDPOINT> -U admin -d portable_refill_db -f database/schema.sql
```

### Step 3: Update Lambda Environment Variables
Add to deploy script:
```json
{
  "Variables": {
    "NODE_ENV": "production",
    "JWT_SECRET": "your-secret-key",
    "JWT_EXPIRES_IN": "7d",
    "DB_HOST": "portable-refill-db.xxxxx.ap-southeast-1.rds.amazonaws.com",
    "DB_PORT": "5432",
    "DB_USER": "admin",
    "DB_PASSWORD": "YourSecurePassword123!",
    "DB_NAME": "portable_refill_db"
  }
}
```

### Step 4: Deploy Lambda Functions
```powershell
.\deploy-lambda.ps1
```

### Step 5: Create API Gateway
```powershell
# Create HTTP API
# Link Lambda functions to routes
# Test endpoints
```

### Step 6: Update Mobile App
```
# Update portable-refill-app/.env
API_BASE_URL=https://xxxxx.execute-api.ap-southeast-1.amazonaws.com
```

---

## 🚨 SECURITY WARNINGS

1. **AWS Credentials Exposed**: Rotate immediately after setup
2. **Database Password**: Change from example password
3. **JWT Secret**: Use strong random string
4. **Database Security Group**: Should only allow Lambda access (not public)
5. **API Gateway**: Add throttling and API keys for production

---

## 💡 RECOMMENDATION

**Option A: Full Setup (Recommended)**
1. Create RDS database (~20 min setup, costs ~$13/month)
2. Deploy everything properly
3. Test with real database

**Option B: Quick Test with Mock (Development Only)**
1. Deploy Lambda without database
2. Use mock data in code temporarily
3. Add database later
4. **Cons**: Can't test auth, registration, or any database operations

**Option C: Use Local PostgreSQL First**
1. Install PostgreSQL locally
2. Test backend locally
3. Deploy to AWS after local testing works
4. **Pros**: Cheaper for development, faster iteration

---

## 💰 AWS COSTS ESTIMATE

| Service | Configuration | Monthly Cost |
|---------|--------------|--------------|
| RDS PostgreSQL | db.t3.micro (20GB) | ~$13 |
| Lambda | 1M requests, 512MB, 30s avg | ~$0.20 |
| API Gateway | 1M requests | ~$1 |
| **Total** | | **~$14.20/month** |

---

## ❓ WHAT SHOULD WE DO NOW?

**Choose one:**

1. **Create RDS database** and do full deployment (~30 minutes)
2. **Test locally first** with PostgreSQL on your machine
3. **Deploy without database** for testing Lambda/API Gateway setup only
4. **Wait and review** architecture before spending money

Which option do you prefer?
