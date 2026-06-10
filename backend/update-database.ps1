# Quick Database Update Script
# Run this to add all missing tables to your database

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Portable Refill - Database Update" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Database configuration
Write-Host "Enter your database connection details:" -ForegroundColor Yellow
$DB_HOST = Read-Host "Database Host (e.g., your-instance.region.rds.amazonaws.com)"
$DB_PORT = Read-Host "Port (default: 5432)" 
if ([string]::IsNullOrWhiteSpace($DB_PORT)) { $DB_PORT = "5432" }
$DB_NAME = Read-Host "Database Name (default: portable_refill)"
if ([string]::IsNullOrWhiteSpace($DB_NAME)) { $DB_NAME = "portable_refill" }
$DB_USER = Read-Host "Database User (default: postgres)"
if ([string]::IsNullOrWhiteSpace($DB_USER)) { $DB_USER = "postgres" }
$DB_PASSWORD = Read-Host "Database Password" -AsSecureString
$DB_PASSWORD_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($DB_PASSWORD))

Write-Host ""
Write-Host "Connecting to database..." -ForegroundColor Yellow

# Set password environment variable for psql
$env:PGPASSWORD = $DB_PASSWORD_PLAIN

# Check if psql is installed
$psqlPath = Get-Command psql -ErrorAction SilentlyContinue

if (-not $psqlPath) {
    Write-Host "❌ psql command not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install PostgreSQL client:" -ForegroundColor Yellow
    Write-Host "  Download from: https://www.postgresql.org/download/windows/" -ForegroundColor Cyan
    Write-Host "  Or use DBeaver/pgAdmin to run the SQL file manually" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "SQL file location: .\database\complete-schema.sql" -ForegroundColor Gray
    exit 1
}

Write-Host "✓ PostgreSQL client found" -ForegroundColor Green

# Test connection
Write-Host "Testing database connection..." -ForegroundColor Yellow
$testResult = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT version();" 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Connection failed!" -ForegroundColor Red
    Write-Host $testResult -ForegroundColor Red
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "  - Database host and port are correct" -ForegroundColor Gray
    Write-Host "  - Database is running" -ForegroundColor Gray
    Write-Host "  - Username and password are correct" -ForegroundColor Gray
    Write-Host "  - Security group allows connection from your IP" -ForegroundColor Gray
    exit 1
}

Write-Host "✓ Connected successfully!" -ForegroundColor Green
Write-Host ""

# Run schema update
Write-Host "Updating database schema..." -ForegroundColor Yellow
Write-Host "This will add:" -ForegroundColor Gray
Write-Host "  - Stations table" -ForegroundColor Gray
Write-Host "  - Tanks table" -ForegroundColor Gray
Write-Host "  - Pricing table" -ForegroundColor Gray
Write-Host "  - Pumps table" -ForegroundColor Gray
Write-Host "  - Payments table" -ForegroundColor Gray
Write-Host "  - Wallets table" -ForegroundColor Gray
Write-Host "  - Transactions table" -ForegroundColor Gray
Write-Host "  - Alarms table" -ForegroundColor Gray
Write-Host "  - Sample data for testing" -ForegroundColor Gray
Write-Host ""

$schemaFile = ".\database\complete-schema.sql"

if (-not (Test-Path $schemaFile)) {
    Write-Host "❌ Schema file not found: $schemaFile" -ForegroundColor Red
    Write-Host "Make sure you're running this from the backend folder" -ForegroundColor Yellow
    exit 1
}

Write-Host "Running schema update..." -ForegroundColor Yellow
$updateResult = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $schemaFile 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Schema update failed!" -ForegroundColor Red
    Write-Host $updateResult -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ Database updated successfully!" -ForegroundColor Green
Write-Host ""

# Verify tables were created
Write-Host "Verifying tables..." -ForegroundColor Yellow
$verifyQuery = @"
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
"@

$tables = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c $verifyQuery 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Tables created:" -ForegroundColor Green
    $tables -split "`n" | Where-Object { $_.Trim() -ne "" } | ForEach-Object {
        Write-Host "  - $($_.Trim())" -ForegroundColor Gray
    }
} else {
    Write-Host "⚠️ Could not verify tables" -ForegroundColor Yellow
}

Write-Host ""

# Check sample data
Write-Host "Checking sample data..." -ForegroundColor Yellow
$stationCount = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM Stations;" 2>&1

if ($LASTEXITCODE -eq 0) {
    $count = $stationCount.Trim()
    Write-Host "✓ Stations in database: $count" -ForegroundColor Green
    
    if ([int]$count -gt 0) {
        Write-Host ""
        Write-Host "Sample stations:" -ForegroundColor Yellow
        $stations = & psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT StationID, StationName, Status FROM Stations LIMIT 5;" 2>&1
        Write-Host $stations -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Database Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Fix API Gateway routing (see BACKEND_FIX_GUIDE.md)" -ForegroundColor White
Write-Host "2. Test the backend:" -ForegroundColor White
Write-Host "   cd ..\portable-refill-app\playstore" -ForegroundColor Gray
Write-Host "   node backend-test.js" -ForegroundColor Gray
Write-Host "3. Test in the mobile app" -ForegroundColor White
Write-Host ""

# Clean up password
$env:PGPASSWORD = ""
