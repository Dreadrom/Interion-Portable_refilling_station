# Fast Backend Deployment Script
# Deploys only source code, installs dependencies on server

$ErrorActionPreference = "Stop"

$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"
$DEPLOY_DIR = "/home/ubuntu/app"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fast Backend Deployment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create deployment package (WITHOUT node_modules)
Write-Host "[1/5] Creating deployment package (source only)..." -ForegroundColor Yellow
Push-Location $BACKEND_DIR

# Remove old deploy.zip if exists
if (Test-Path "deploy-source.zip") {
    Remove-Item "deploy-source.zip" -Force
}

# Create zip with only source files (NO node_modules)
Write-Host "  - Packaging dist/, package.json, .env..." -ForegroundColor Gray
$filesToZip = @(
    "dist",
    "package.json",
    "package-lock.json",
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

# Create zip
Compress-Archive -Path $filesToZip -DestinationPath "deploy-source.zip" -Force

$zipSize = (Get-Item "deploy-source.zip").Length / 1MB
Write-Host "  - Package created (deploy-source.zip - $([math]::Round($zipSize, 2)) MB)" -ForegroundColor Green

Pop-Location
Write-Host ""

# Upload to server
Write-Host "[2/5] Uploading to server..." -ForegroundColor Yellow
scp -i $SSH_KEY "$BACKEND_DIR\deploy-source.zip" "${SERVER_USER}@${SERVER_HOST}:/tmp/deploy-source.zip"

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Upload failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Uploaded" -ForegroundColor Green
Write-Host ""

# Stop service
Write-Host "[3/5] Stopping service..." -ForegroundColor Yellow
ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" "sudo systemctl stop gasapp"

Write-Host "  - Service stopped" -ForegroundColor Green
Write-Host ""

# Extract and install dependencies on server
Write-Host "[4/5] Extracting and installing dependencies on server..." -ForegroundColor Yellow
Write-Host "  (This may take 1-2 minutes...)" -ForegroundColor Gray

$deployScript = @"
echo 'Extracting...'
cd $DEPLOY_DIR
sudo unzip -o /tmp/deploy-source.zip
sudo chown -R ubuntu:ubuntu $DEPLOY_DIR

echo 'Installing dependencies...'
npm install --production --quiet

echo 'Cleaning up...'
rm /tmp/deploy-source.zip

echo 'Extraction and installation complete!'
"@

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" $deployScript

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "X Deployment failed!" -ForegroundColor Red
    exit 1
}

Write-Host "  - Dependencies installed" -ForegroundColor Green
Write-Host ""

# Start service
Write-Host "[5/5] Starting service..." -ForegroundColor Yellow
ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" "sudo systemctl start gasapp; sudo systemctl status gasapp --no-pager --lines=10"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "X Service start failed!" -ForegroundColor Red
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
