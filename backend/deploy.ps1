# Deployment Script for Portable Refill Station Backend
# This script deploys the built backend to cp1.interion.com.sg

param(
    [switch]$BuildOnly,
    [switch]$DeployOnly
)

$ErrorActionPreference = "Stop"

# Configuration
$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$DEPLOY_DIR = "/home/ubuntu/app"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "  Portable Refill Station Deployment" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Build the backend (unless DeployOnly)
if (-not $DeployOnly) {
    Write-Host "[1/4] Building TypeScript code..." -ForegroundColor Yellow
    
    Push-Location $BACKEND_DIR
    
    # Install dependencies if needed
    if (-not (Test-Path "node_modules")) {
        Write-Host "Installing dependencies..." -ForegroundColor Gray
        npm install
    }
    
    # Build
    npm run build
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed!" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    
    Write-Host "✓ Build completed successfully" -ForegroundColor Green
    Pop-Location
}

if ($BuildOnly) {
    Write-Host ""
    Write-Host "Build-only mode complete. Skipping deployment." -ForegroundColor Cyan
    exit 0
}

# Step 2: Create deployment package
Write-Host ""
Write-Host "[2/4] Creating deployment package..." -ForegroundColor Yellow

Push-Location $BACKEND_DIR

# Remove old deploy.zip if exists
if (Test-Path "deploy.zip") {
    Remove-Item "deploy.zip" -Force
}

# Create zip with dist, .env, and package.json
Compress-Archive -Path "dist/*",".env","package.json" -DestinationPath "deploy.zip" -Force

Write-Host "✓ Deployment package created (deploy.zip)" -ForegroundColor Green

# Step 3: Upload to server
Write-Host ""
Write-Host "[3/4] Uploading to server..." -ForegroundColor Yellow

# Check if SSH key exists
if (-not (Test-Path $SSH_KEY)) {
    Write-Host "ERROR: SSH key not found at $SSH_KEY" -ForegroundColor Red
    Write-Host "Please ensure the key file exists." -ForegroundColor Red
    Pop-Location
    exit 1
}

# Upload using SCP
scp -i $SSH_KEY deploy.zip "${SERVER_USER}@${SERVER_HOST}:/home/ubuntu/"

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to upload to server!" -ForegroundColor Red
    Pop-Location
    exit 1
}

Write-Host "✓ Uploaded to server" -ForegroundColor Green

# Step 4: Extract and restart on server
Write-Host ""
Write-Host "[4/4] Installing and restarting app on server..." -ForegroundColor Yellow

$REMOTE_COMMANDS = @"
cd $DEPLOY_DIR && \
unzip -o /home/ubuntu/deploy.zip && \
npm install --production && \
pm2 restart gasapp || pm2 start index.js --name gasapp && \
pm2 save
"@

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $REMOTE_COMMANDS

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to restart app on server!" -ForegroundColor Red
    Pop-Location
    exit 1
}

Write-Host "✓ App restarted successfully" -ForegroundColor Green

Pop-Location

# Display success message
Write-Host ""
Write-Host "====================================" -ForegroundColor Green
Write-Host "  Deployment Completed Successfully!" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""
Write-Host "API URL: https://cp1.interion.com.sg" -ForegroundColor Cyan
Write-Host ""
Write-Host "To check status, SSH into server and run:" -ForegroundColor Gray
Write-Host "  ssh -i $SSH_KEY ${SERVER_USER}@${SERVER_HOST}" -ForegroundColor White
Write-Host "  pm2 status" -ForegroundColor White
Write-Host "  pm2 logs gasapp" -ForegroundColor White
Write-Host ""
Write-Host "To test API:" -ForegroundColor Gray
Write-Host "  curl https://cp1.interion.com.sg/health" -ForegroundColor White
