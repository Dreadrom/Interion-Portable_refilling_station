# Complete Backend Fix and Deployment Script
# This will fix database and deploy to your server

param(
    [switch]$DatabaseOnly,
    [switch]$DeployOnly
)

$ErrorActionPreference = "Stop"

# Configuration
$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$DEPLOY_DIR = "/home/ubuntu/app"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Complete Backend Fix & Deployment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Update Database Schema on Server
if (-not $DeployOnly) {
    Write-Host "[1/3] Updating Database Schema on Server..." -ForegroundColor Yellow
    Write-Host ""
    
    # Upload schema file to server
    Write-Host "  Uploading schema file..." -ForegroundColor Gray
    scp -i $SSH_KEY "$BACKEND_DIR\database\complete-schema.sql" "${SERVER_USER}@${SERVER_HOST}:/tmp/complete-schema.sql"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "X Failed to upload schema file" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "  - Schema file uploaded" -ForegroundColor Green
    
    # Execute schema on database
    Write-Host "  Executing schema update..." -ForegroundColor Gray
    $sshCommand = @"
export PGPASSWORD='GasApp2026!'
psql -U gasapp -d gasapp -f /tmp/complete-schema.sql
echo ''
echo 'Verifying tables...'
psql -U gasapp -d gasapp -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name;"
echo ''
echo 'Checking stations...'
psql -U gasapp -d gasapp -c "SELECT COUNT(*) as station_count FROM Stations;"
"@
    
    ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $sshCommand
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "X Database update failed" -ForegroundColor Red
        Write-Host ""
        Write-Host "Try manually:" -ForegroundColor Yellow
        Write-Host "  ssh -i $SSH_KEY ${SERVER_USER}@${SERVER_HOST}" -ForegroundColor White
        Write-Host "  cd $DEPLOY_DIR" -ForegroundColor White
        Write-Host "  PGPASSWORD='GasApp2026!' psql -U gasapp -d gasapp -f /tmp/complete-schema.sql" -ForegroundColor White
        exit 1
    }
    
    Write-Host ""
    Write-Host "  - Database schema updated successfully!" -ForegroundColor Green
    Write-Host ""
}

if ($DatabaseOnly) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Database Update Complete!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Next: Test the API" -ForegroundColor Yellow
    Write-Host "  cd ..\portable-refill-app\playstore" -ForegroundColor White
    Write-Host "  node backend-test.js" -ForegroundColor White
    exit 0
}

# Step 2: Build Backend (if not DeployOnly)
Write-Host "[2/3] Building Backend..." -ForegroundColor Yellow

Push-Location $BACKEND_DIR

# Install dependencies if needed
if (-not (Test-Path "node_modules")) {
    Write-Host "  Installing dependencies..." -ForegroundColor Gray
    npm install
}

# Build
Write-Host "  Compiling TypeScript..." -ForegroundColor Gray
npm run build

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Build failed!" -ForegroundColor Red
    Pop-Location
    exit 1
}

Write-Host "  - Build completed" -ForegroundColor Green
Pop-Location

# Step 3: Deploy to Server
Write-Host ""
Write-Host "[3/3] Deploying to Server..." -ForegroundColor Yellow
Write-Host ""

# Create deployment package
Write-Host "  Creating deployment package..." -ForegroundColor Gray
Push-Location "$BACKEND_DIR\dist"

if (Test-Path "$BACKEND_DIR\deploy.zip") {
    Remove-Item "$BACKEND_DIR\deploy.zip" -Force
}

# Copy node_modules and package files
Copy-Item -Path "$BACKEND_DIR\node_modules" -Destination ".\node_modules" -Recurse -Force
Copy-Item -Path "$BACKEND_DIR\package.json" -Destination ".\package.json" -Force
Copy-Item -Path "$BACKEND_DIR\.env" -Destination ".\.env" -Force

# Create zip
Compress-Archive -Path * -DestinationPath "$BACKEND_DIR\deploy.zip" -Force

# Cleanup
Remove-Item ".\node_modules" -Recurse -Force
Remove-Item ".\package.json" -Force
Remove-Item ".\.env" -Force

Pop-Location

Write-Host "  - Package created" -ForegroundColor Green

# Upload to server
Write-Host "  Uploading to server..." -ForegroundColor Gray
scp -i $SSH_KEY "$BACKEND_DIR\deploy.zip" "${SERVER_USER}@${SERVER_HOST}:/tmp/deploy.zip"

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Upload failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Uploaded" -ForegroundColor Green

# Deploy on server
Write-Host "  Deploying on server..." -ForegroundColor Gray
$deployCommand = @"
sudo systemctl stop gasapp 2>/dev/null || true
if [ -d '$DEPLOY_DIR' ]; then
    sudo cp -r $DEPLOY_DIR ${DEPLOY_DIR}_backup_`date +%Y%m%d_%H%M%S` 2>/dev/null || true
fi
sudo mkdir -p $DEPLOY_DIR
sudo unzip -o /tmp/deploy.zip -d $DEPLOY_DIR
sudo chown -R ubuntu:ubuntu $DEPLOY_DIR
sudo systemctl start gasapp
sudo systemctl status gasapp --no-pager
rm /tmp/deploy.zip
echo ''
echo 'Deployment complete!'
"@

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $deployCommand

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Deployment failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Deployed successfully" -ForegroundColor Green

# Cleanup local files
Remove-Item "$BACKEND_DIR\deploy.zip" -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "API URL: https://cp1.interion.com.sg" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test the API:" -ForegroundColor Yellow
Write-Host "  cd ..\portable-refill-app\playstore" -ForegroundColor White
Write-Host "  node backend-test.js" -ForegroundColor White
Write-Host ""
Write-Host "Or test directly:" -ForegroundColor Yellow
Write-Host "  curl https://cp1.interion.com.sg/health" -ForegroundColor White
Write-Host ""
