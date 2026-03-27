# Database Setup Script for Portable Refill Station
# This script creates the database tables on cp1.interion.com.sg

$ErrorActionPreference = "Stop"

# Configuration
$DB_HOST = "54.179.159.196"
$DB_USER = "gasapp"
$DB_PASSWORD = "!KP@ABjiKEs&Gbv@bpUhk4tq"
$DB_NAME = "gasapp"
$SCHEMA_FILE = "C:\Users\songj\Interion\Interion-Portable_refilling_station\backend\database\schema.sql"
$SERVER_USER = "ubuntu"
$SSH_KEY = "c:\Users\songj\Downloads\gasapp-shared-key"

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "  Database Setup" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Check if schema file exists
if (-not (Test-Path $SCHEMA_FILE)) {
    Write-Host "ERROR: Schema file not found at $SCHEMA_FILE" -ForegroundColor Red
    exit 1
}

Write-Host "[1/3] Uploading schema file to server..." -ForegroundColor Yellow

# Upload schema file to server
scp -i $SSH_KEY $SCHEMA_FILE "${SERVER_USER}@${DB_HOST}:/home/ubuntu/schema.sql"

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to upload schema file!" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Schema file uploaded" -ForegroundColor Green

Write-Host ""
Write-Host "[2/3] Creating database tables..." -ForegroundColor Yellow

# Execute schema on server
$PSQL_COMMAND = "psql 'postgresql://${DB_USER}:${DB_PASSWORD}@127.0.0.1:5432/${DB_NAME}' -f /home/ubuntu/schema.sql"

ssh -i $SSH_KEY "${SERVER_USER}@${DB_HOST}" $PSQL_COMMAND

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to create tables!" -ForegroundColor Red
    Write-Host "Check if the database exists and credentials are correct." -ForegroundColor Yellow
    exit 1
}

Write-Host "✓ Tables created successfully" -ForegroundColor Green

Write-Host ""
Write-Host "[3/3] Verifying tables..." -ForegroundColor Yellow

# Verify tables were created
$VERIFY_COMMAND = "psql 'postgresql://${DB_USER}:${DB_PASSWORD}@127.0.0.1:5432/${DB_NAME}' -c '\dt'"

$output = ssh -i $SSH_KEY "${SERVER_USER}@${DB_HOST}" $VERIFY_COMMAND

Write-Host ""
Write-Host "Database Tables:" -ForegroundColor Cyan
Write-Host $output
Write-Host ""

# Check if all expected tables exist
if ($output -match "users" -and $output -match "refreshtokens" -and $output -match "passwordresettokens") {
    Write-Host "✓ All tables verified successfully" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "====================================" -ForegroundColor Green
    Write-Host "  Database Setup Complete!" -ForegroundColor Green
    Write-Host "====================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Tables created:" -ForegroundColor Cyan
    Write-Host "  • Users" -ForegroundColor White
    Write-Host "  • RefreshTokens" -ForegroundColor White
    Write-Host "  • PasswordResetTokens" -ForegroundColor White
    Write-Host ""
    Write-Host "Default admin account:" -ForegroundColor Cyan
    Write-Host "  Email: admin@portable-refill.com" -ForegroundColor White
    Write-Host "  Password: Admin123!" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "WARNING: Some tables may not have been created correctly." -ForegroundColor Yellow
    Write-Host "Please check the output above." -ForegroundColor Yellow
}
