# Backend Deployment Script
# Deploys built backend to EC2 server

$ErrorActionPreference = "Stop"

$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"
$DEPLOY_DIR = "/home/ubuntu/app"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Backend Deployment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create deployment package
Write-Host "[1/4] Creating deployment package..." -ForegroundColor Yellow
Push-Location $BACKEND_DIR

# Remove old deploy.zip if exists
if (Test-Path "deploy.zip") {
    Remove-Item "deploy.zip" -Force
}

# Create zip with required files
Write-Host "  - Packaging dist/, node_modules/, package.json, .env..." -ForegroundColor Gray
$filesToZip = @(
    "dist",
    "node_modules",
    "package.json",
    ".env"
)

# Check if all files exist
$allExist = $true
foreach ($file in $filesToZip) {
    if (-not (Test-Path $file)) {
        Write-Host "  X Missing: $file" -ForegroundColor Red
        $allExist = $false
    }
}

if (-not $allExist) {
    Write-Host ""
    Write-Host "X Missing required files for deployment!" -ForegroundColor Red
    Write-Host "  Run 'npm run build' first" -ForegroundColor Yellow
    Pop-Location
    exit 1
}

# Create zip using PowerShell Compress-Archive
Compress-Archive -Path $filesToZip -DestinationPath "deploy.zip" -Force

Pop-Location

Write-Host "  - Package created (deploy.zip)" -ForegroundColor Green
Write-Host ""

# Upload to server
Write-Host "[2/4] Uploading to server..." -ForegroundColor Yellow
scp -i $SSH_KEY "$BACKEND_DIR\deploy.zip" "${SERVER_USER}@${SERVER_HOST}:/tmp/deploy.zip"

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Upload failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Uploaded" -ForegroundColor Green
Write-Host ""

# Stop service
Write-Host "[3/4] Stopping service..." -ForegroundColor Yellow
ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" "sudo systemctl stop gasapp"

Write-Host "  - Service stopped" -ForegroundColor Green
Write-Host ""

# Extract and deploy
Write-Host "[4/4] Deploying..." -ForegroundColor Yellow
$deployScript = @"
cd $DEPLOY_DIR
sudo unzip -o /tmp/deploy.zip
sudo chown -R ubuntu:ubuntu $DEPLOY_DIR
sudo systemctl start gasapp
sudo systemctl status gasapp --no-pager --lines=5
rm /tmp/deploy.zip
echo ''
echo 'Deployment complete!'
"@

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $deployScript

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "X Deployment failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Server: https://cp1.interion.com.sg" -ForegroundColor White
Write-Host ""
Write-Host "Next step: Test the API" -ForegroundColor Yellow
Write-Host "  cd ..\portable-refill-app\playstore" -ForegroundColor White
Write-Host "  node backend-test.js" -ForegroundColor White
Write-Host ""
