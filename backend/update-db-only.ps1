# Simplified Database Update Script
# Run this to update database on server

$ErrorActionPreference = "Stop"

$SERVER_HOST = "54.179.159.196"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"
$BACKEND_DIR = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Database Schema Update" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Upload SQL file
Write-Host "[1/3] Uploading schema file..." -ForegroundColor Yellow
scp -i $SSH_KEY "$BACKEND_DIR\database\complete-schema.sql" "${SERVER_USER}@${SERVER_HOST}:/tmp/complete-schema.sql"

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Failed to upload" -ForegroundColor Red
    exit 1
}

Write-Host "- Uploaded" -ForegroundColor Green
Write-Host ""

# Upload update script
Write-Host "[2/3] Uploading update script..." -ForegroundColor Yellow
scp -i $SSH_KEY "$BACKEND_DIR\database\update-db-server.sh" "${SERVER_USER}@${SERVER_HOST}:/tmp/update-db-server.sh"

if ($LASTEXITCODE -ne 0) {
    Write-Host "X Failed to upload" -ForegroundColor Red
    exit 1
}

Write-Host "- Uploaded" -ForegroundColor Green
Write-Host ""

# Execute on server
Write-Host "[3/3] Executing database update..." -ForegroundColor Yellow
Write-Host ""

ssh -i $SSH_KEY "${SERVER_USER}@${SERVER_HOST}" "chmod +x /tmp/update-db-server.sh && /tmp/update-db-server.sh"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "X Update failed" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Try manually:" -ForegroundColor Yellow
    Write-Host "  ssh -i $SSH_KEY ${SERVER_USER}@${SERVER_HOST}" -ForegroundColor White
    Write-Host "  PGPASSWORD='GasApp2026!' psql -U gasapp -d gasapp -f /tmp/complete-schema.sql" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Database Update Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next step: Test the API" -ForegroundColor Yellow
Write-Host "  cd ..\portable-refill-app\playstore" -ForegroundColor White
Write-Host "  node backend-test.js" -ForegroundColor White
Write-Host ""
