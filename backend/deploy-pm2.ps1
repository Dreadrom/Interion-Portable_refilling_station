# Backend Deployment Script - PM2 Version
$ErrorActionPreference = "Stop"

$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"
$DEPLOY_DIR = "/home/ubuntu/app"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Backend Deployment (PM2)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Build first
Write-Host "[1/5] Building TypeScript..." -ForegroundColor Yellow
Push-Location $BACKEND_DIR
npm run build 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "X Build failed!" -ForegroundColor Red
    Pop-Location
    exit 1
}
Write-Host "  - Build complete" -ForegroundColor Green
Pop-Location
Write-Host ""

# Create deployment package
Write-Host "[2/5] Creating package..." -ForegroundColor Yellow
Push-Location $BACKEND_DIR

if (Test-Path "deploy-source.zip") {
    Remove-Item "deploy-source.zip" -Force
}

Compress-Archive -Path @("dist", "package.json", "package-lock.json", ".env") -DestinationPath "deploy-source.zip" -Force
$zipSize = (Get-Item "deploy-source.zip").Length / 1KB
Write-Host "  - Package created ($([math]::Round($zipSize, 2)) KB)" -ForegroundColor Green

Pop-Location
Write-Host ""

# Upload
Write-Host "[3/5] Uploading..." -ForegroundColor Yellow
scp -i $SSH_KEY "$BACKEND_DIR\deploy-source.zip" "${SERVER_USER}@${SERVER_HOST}:/tmp/deploy-source.zip" 2>&1 | Out-Null

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Upload failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  - Uploaded" -ForegroundColor Green
Write-Host ""

# Deploy on server using bash script
Write-Host "[4/5] Deploying on server..." -ForegroundColor Yellow
Write-Host "  (Installing dependencies...)" -ForegroundColor Gray

# Create a proper bash script file to avoid line ending issues
$bashScript = "cd /home/ubuntu/app && unzip -o /tmp/deploy-source.zip && npm install --production && rm /tmp/deploy-source.zip && pm2 restart gasapp || pm2 start dist/index.js --name gasapp"

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $bashScript

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Deployment failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Deployed" -ForegroundColor Green
Write-Host ""

# Check status
Write-Host "[5/5] Checking status..." -ForegroundColor Yellow
ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" "pm2 list; pm2 logs gasapp --lines 10 --nostream"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Server: https://cp1.interion.com.sg" -ForegroundColor White
Write-Host ""
Write-Host "Test with: cd ..\portable-refill-app\playstore; node backend-test.js" -ForegroundColor Yellow
Write-Host ""
