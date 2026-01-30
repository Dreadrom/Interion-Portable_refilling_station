# AWS Lambda Authentication Backend - Setup & Deployment

## Overview

Single Lambda function (`AuthHandler`) that handles all authentication endpoints for the Portable Refill Station mobile app.

## Prerequisites

1. **AWS Account** with:
   - AWS RDS MySQL instance created
   - IAM permissions to create Lambda functions and API Gateway

2. **Node.js 18+** installed locally

3. **AWS CLI** configured:
   ```bash
   aws configure
   ```

---

## Step 1: Setup Database (AWS RDS)

### 1.1 Create RDS PostgreSQL Instance

In AWS Console → RDS:
- Engine: **PostgreSQL 15** (or latest)
- Template: Free tier (or your preference)
- DB Instance Identifier: `portable-refill-db`
- Master username: `postgres`
- Master password: (choose secure password)
- Public access: Yes (for development) or use VPC
- Initial database name: `portable_refill`

### 1.2 Note Connection Details

After creation, note:
- Endpoint: `your-instance.region.rds.amazonaws.com`
- Port: `5432`
- Database name: `portable_refill`

### 1.3 Create Database Schema

Connect to your RDS instance and run the schema:

```bash
psql -h your-instance.region.rds.amazonaws.com -U postgres -d portable_refill -f database/schema.sql
```

Or use pgAdmin/DBeaver to execute `database/schema.sql`.

---

## Step 2: Setup Backend Project

### 2.1 Install Dependencies

```bash
cd backend
npm install
```

### 2.2 Build TypeScript

```bash
npm run build
```

This compiles TypeScript to JavaScript in the `dist/` folder.

---

## Step 3: Create Lambda Function

### 3.1 Create ZIP Package

**On Windows (PowerShell):**
```powershell
cd dist
Compress-Archive -Path * -DestinationPath ..\AuthHandler.zip -Force
cd ..
```

**On macOS/Linux:**
```bash
cd dist
zip -r ../AuthHandler.zip .
cd ..
```

### 3.2 Create Lambda Function in AWS Console

1. Go to **AWS Lambda Console**
2. Click **Create function**
3. Choose **Author from scratch**
4. Configuration:
   - Function name: `AuthHandler`
   - Runtime: **Node.js 18.x** (or 20.x)
   - Architecture: x86_64
   - Permissions: Create new role with basic Lambda permissions

5. Click **Create function**

### 3.3 Upload Code

1. In the Lambda function page, under **Code** tab
2. Click **Upload from** → **.zip file**
3. Upload `AuthHandler.zip`
4. Click **Save**

### 3.4 Configure Handler

Under **Runtime settings**, set:
- Handler: `handlers/AuthHandler.handler`

---

## Step 4: Configure Environment Variables

In Lambda Console → Configuration → Environment variables:

Add these variables:

```
DB_HOST = your-instance.region.rds.amazonaws.com
DB_PORT = 5432
DB_USER = postgres
DB_PASSWORD = your-database-password
DB_NAME = portable_refill
JWT_SECRET = your-super-secret-jwt-key-change-in-production
JWT_EXPIRES_IN = 7d
REFRESH_TOKEN_EXPIRES_IN = 30d
```

**Important**: Use AWS Secrets Manager for production instead of environment variables.

---

## Step 5: Configure VPC (if RDS is in VPC)

If your RDS is in a VPC (recommended for production):

1. Go to **Configuration** → **VPC**
2. Edit VPC settings:
   - Choose same VPC as your RDS
   - Select subnets
   - Select security group that allows access to RDS

3. Add **NAT Gateway** to VPC for Lambda internet access (if needed)

---

## Step 6: Create API Gateway

### 6.1 Create REST API

1. Go to **API Gateway Console**
2. Click **Create API** → **REST API** (not private)
3. Choose **New API**
4. API name: `PortableRefillAPI`
5. Click **Create API**

### 6.2 Create Resources and Methods

Create the following structure:

```
/auth
  /register (POST)
  /login (POST)
  /me (GET)
  /forgot-password (POST)
  /reset-password (POST)
  /logout (POST)

/user
  /update (POST)
  /change-password (POST)
```

**For each endpoint:**

1. Click **Create Resource**
2. Resource Name: e.g., `auth`
3. Click **Create Method** → Choose `POST` or `GET`
4. Integration type: **Lambda Function**
5. Lambda Function: `AuthHandler`
6. Click **Save** and confirm

### 6.3 Enable CORS

For each method:
1. Select the method
2. Click **Actions** → **Enable CORS**
3. Confirm

### 6.4 Deploy API

1. Click **Actions** → **Deploy API**
2. Stage: **New Stage**
3. Stage name: `prod` (or `dev`)
4. Click **Deploy**

### 6.5 Get API Endpoint

After deployment, you'll see:
```
Invoke URL: https://xxxxxx.execute-api.region.amazonaws.com/prod
```

**Copy this URL** - this is your `API_BASE_URL`.

---

## Step 7: Update Mobile App Configuration

In your mobile app, update the `.env` file:

```bash
# portable-refill-app/.env
API_BASE_URL=https://xxxxxx.execute-api.region.amazonaws.com/prod
```

---

## Step 8: Test the API

### Test Register

```bash
curl -X POST https://your-api-url/prod/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "name": "Test User",
    "phone": "+60123456789"
  }'
```

### Test Login

```bash
curl -X POST https://your-api-url/prod/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

### Test Get Profile

```bash
curl -X GET https://your-api-url/prod/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## Troubleshooting

### Lambda times out
- **Cause**: Cannot connect to RDS
- **Solution**: Ensure Lambda is in same VPC as RDS, security groups allow connection

### CORS errors
- **Cause**: CORS not enabled on API Gateway
- **Solution**: Enable CORS for all methods and redeploy

### Database connection errors
- **Cause**: Wrong credentials or security group
- **Solution**: Check environment variables, RDS security group allows Lambda IP

### Cannot find handler
- **Cause**: Wrong handler path
- **Solution**: Ensure handler is set to `handlers/AuthHandler.handler`

---

## Production Recommendations

1. **Security**:
   - Move credentials to AWS Secrets Manager
   - Use AWS Systems Manager Parameter Store
   - Enable AWS WAF on API Gateway
   - Implement rate limiting

2. **Monitoring**:
   - Enable CloudWatch logs
   - Set up CloudWatch alarms
   - Use AWS X-Ray for tracing

3. **Performance**:
   - Increase Lambda memory (512MB - 1024MB)
   - Enable RDS connection pooling
   - Use Lambda provisioned concurrency

4. **Database**:
   - Enable automated backups
   - Use Multi-AZ deployment
   - Implement read replicas for scaling

---

## API Endpoints Summary

All endpoints handled by single `AuthHandler` Lambda:

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | User login |
| GET | `/auth/me` | Get current user profile |
| POST | `/auth/forgot-password` | Request password reset |
| POST | `/auth/reset-password` | Reset password with token |
| POST | `/auth/logout` | Logout user |
| POST | `/user/update` | Update user profile |
| POST | `/user/change-password` | Change password |

---

## Next Steps

After auth is working:
1. Create additional Lambda functions for stations, payments, dispense
2. Implement real-time updates with AWS IoT Core
3. Set up Fiuu payment integration
4. Add monitoring and logging
