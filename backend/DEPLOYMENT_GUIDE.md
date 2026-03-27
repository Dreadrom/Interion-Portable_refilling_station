# Deployment Instructions for Portable Refill Station Backend

## Server Information
- **Host:** 54.179.159.196 (cp1.interion.com.sg)
- **User:** ubuntu
- **Database:** gasapp
- **App Directory:** /home/ubuntu/app/
- **Port:** 3000 (proxied through Nginx)

---

## Step 1: Create Database Tables

### Option A: Using SSH and psql (Recommended)

1. Copy the schema file to the server:
```powershell
scp -i c:\Users\songj\Downloads\gasapp-shared-key backend/database/schema.sql ubuntu@54.179.159.196:/home/ubuntu/
```

2. SSH into the server:
```powershell
ssh -i c:\Users\songj\Downloads\gasapp-shared-key ubuntu@54.179.159.196
```

3. Run the schema file:
```bash
psql postgresql://gasapp:!KP@ABjiKEs&Gbv@bpUhk4tq@127.0.0.1:5432/gasapp -f /home/ubuntu/schema.sql
```

4. Verify tables were created:
```bash
psql postgresql://gasapp:!KP@ABjiKEs&Gbv@bpUhk4tq@127.0.0.1:5432/gasapp -c "\dt"
```

Expected output:
```
             List of relations
 Schema |        Name         | Type  | Owner  
--------+---------------------+-------+--------
 public | passwordresettokens | table | gasapp
 public | refreshtokens       | table | gasapp
 public | users               | table | gasapp
```

### Option B: Using Local PostgreSQL Client

1. Install PostgreSQL client on Windows:
```powershell
winget install PostgreSQL.PostgreSQL
```

2. Run from your local machine:
```powershell
$env:PGPASSWORD="!KP@ABjiKEs&Gbv@bpUhk4tq"
psql -h 54.179.159.196 -U gasapp -d gasapp -f backend/database/schema.sql
```

---

## Step 2: Build the Backend

1. Install dependencies:
```powershell
cd backend
npm install
```

2. Build TypeScript to JavaScript:
```powershell
npm run build
```

This creates a `dist/` folder with compiled JavaScript.

---

## Step 3: Deploy to Server

### Option A: Manual Deployment (First Time)

1. Create a zip file of the built code:
```powershell
cd backend
Compress-Archive -Path dist/*,.env,package.json -DestinationPath deploy.zip -Force
```

2. Copy to server:
```powershell
scp -i c:\Users\songj\Downloads\gasapp-shared-key deploy.zip ubuntu@54.179.159.196:/home/ubuntu/
```

3. SSH into server:
```powershell
ssh -i c:\Users\songj\Downloads\gasapp-shared-key ubuntu@54.179.159.196
```

4. Extract and setup:
```bash
cd /home/ubuntu/app
unzip -o /home/ubuntu/deploy.zip
npm install --production
```

5. Create .env file (if not included):
```bash
nano /home/ubuntu/app/.env
```

Paste:
```
NODE_ENV=production
PORT=3000
DB_HOST=127.0.0.1
DB_PORT=5432
DB_USER=gasapp
DB_PASSWORD=!KP@ABjiKEs&Gbv@bpUhk4tq
DB_NAME=gasapp
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-32-chars
JWT_REFRESH_SECRET=your-super-secret-refresh-key-change-this-in-production-min-32-chars
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d
CORS_ORIGIN=*
```

Save (Ctrl+X, Y, Enter)

6. Start with PM2:
```bash
pm2 delete gasapp  # Remove old process if exists
pm2 start /home/ubuntu/app/index.js --name gasapp
pm2 save
```

### Option B: Using Rsync (Quick Updates)

After initial setup, for code updates:

```powershell
cd backend
npm run build
rsync -avz -e "ssh -i c:\Users\songj\Downloads\gasapp-shared-key" dist/ ubuntu@54.179.159.196:/home/ubuntu/app/
```

Then SSH and restart:
```bash
ssh -i c:\Users\songj\Downloads\gasapp-shared-key ubuntu@54.179.159.196
pm2 restart gasapp
```

---

## Step 4: Verify Deployment

### Check Server Status

1. SSH into server:
```bash
ssh -i c:\Users\songj\Downloads\gasapp-shared-key ubuntu@54.179.159.196
```

2. Check PM2 status:
```bash
pm2 status
```

3. View logs:
```bash
pm2 logs gasapp --lines 50
```

4. Check if server is responding:
```bash
curl http://localhost:3000/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2026-03-12T...",
  "uptime": 123.45
}
```

### Test from Your Computer

1. Test health endpoint:
```powershell
curl https://cp1.interion.com.sg/health
```

2. Test user registration:
```powershell
$body = @{
    email = "test@example.com"
    password = "Test123!"
    name = "Test User"
    phone = "0123456789"
} | ConvertTo-Json

Invoke-WebRequest -Uri "https://cp1.interion.com.sg/auth/register" -Method POST -Body $body -ContentType "application/json"
```

3. Test login:
```powershell
$body = @{
    email = "admin@portable-refill.com"
    password = "Admin123!"
} | ConvertTo-Json

Invoke-WebRequest -Uri "https://cp1.interion.com.sg/auth/login" -Method POST -Body $body -ContentType "application/json"
```

---

## Troubleshooting

### Issue: "Cannot connect to database"
**Solution:** Check database credentials in .env file

### Issue: "Port 3000 already in use"
**Solution:** 
```bash
pm2 delete gasapp
pm2 start /home/ubuntu/app/index.js --name gasapp
```

### Issue: "Module not found"
**Solution:** 
```bash
cd /home/ubuntu/app
npm install --production
pm2 restart gasapp
```

### Issue: "Permission denied"
**Solution:** Check file permissions:
```bash
sudo chown -R ubuntu:ubuntu /home/ubuntu/app
```

### View detailed logs:
```bash
pm2 logs gasapp --lines 200
```

---

## Quick Reference Commands

```bash
# SSH into server
ssh -i c:\Users\songj\Downloads\gasapp-shared-key ubuntu@54.179.159.196

# Check app status
pm2 status

# View logs
pm2 logs gasapp

# Restart app
pm2 restart gasapp

# Stop app
pm2 stop gasapp

# Check database connection
psql postgresql://gasapp:!KP@ABjiKEs&Gbv@bpUhk4tq@127.0.0.1:5432/gasapp -c "\dt"

# Test health endpoint
curl http://localhost:3000/health
```

---

## API Endpoints

Once deployed, your API will be available at:

- **Base URL:** https://cp1.interion.com.sg

### Authentication Endpoints
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login user
- `GET /auth/me` - Get current user
- `POST /auth/forgot-password` - Request password reset
- `POST /auth/reset-password` - Reset password
- `POST /auth/refresh` - Refresh access token
- `POST /auth/logout` - Logout user

### Station Endpoints
- `GET /stations` - List all stations
- `GET /stations/:id` - Get station details
- `GET /stations/:id/tanks` - Get tank levels
- `GET /stations/:id/prices` - Get product prices
- `GET /stations/:id/totalizers` - Get totalizers

---

## Environment Variables

Make sure these are set in `/home/ubuntu/app/.env`:

```env
NODE_ENV=production
PORT=3000
DB_HOST=127.0.0.1
DB_PORT=5432
DB_USER=gasapp
DB_PASSWORD=!KP@ABjiKEs&Gbv@bpUhk4tq
DB_NAME=gasapp
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-32-chars
JWT_REFRESH_SECRET=your-super-secret-refresh-key-change-this-in-production-min-32-chars
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d
CORS_ORIGIN=*
```

**IMPORTANT:** Generate strong random secrets for JWT_SECRET and JWT_REFRESH_SECRET in production!
