# Quick Test Script for Fiuu Integration
# This script helps you test the payment flow with sandbox

Write-Host "=== Fiuu Payment Integration Quick Test ===" -ForegroundColor Cyan
Write-Host ""

# Configuration
$API_BASE = "http://localhost:3000"
$JWT_TOKEN = $env:TEST_JWT_TOKEN  # Set this in your environment or replace with actual token

if (-not $JWT_TOKEN) {
    Write-Host "ERROR: No JWT token found. Please set TEST_JWT_TOKEN environment variable or update this script." -ForegroundColor Red
    Write-Host "Example: `$env:TEST_JWT_TOKEN = 'your-jwt-token-here'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To get a token, first register/login:" -ForegroundColor Yellow
    Write-Host "  curl -X POST $API_BASE/auth/login -H 'Content-Type: application/json' -d '{""email"":""test@example.com"",""password"":""password123""}'" -ForegroundColor Gray
    exit 1
}

Write-Host "Step 1: Creating payment..." -ForegroundColor Green

# Create payment
$createPaymentBody = @{
    amount = "10.00"
    currency = "MYR"
    paymentMethod = "FIUU_FPX"
    channel = "fpx"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$API_BASE/payment/create" `
        -Method POST `
        -Headers @{
            "Authorization" = "Bearer $JWT_TOKEN"
            "Content-Type" = "application/json"
        } `
        -Body $createPaymentBody

    Write-Host "✓ Payment created successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Payment ID: $($response.paymentId)" -ForegroundColor Cyan
    Write-Host "Status: $($response.status)" -ForegroundColor Cyan
    Write-Host "Amount: $($response.currency) $($response.amount)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Payment URL:" -ForegroundColor Yellow
    Write-Host $response.paymentUrl -ForegroundColor White
    Write-Host ""

    # Save payment ID for status check
    $paymentId = $response.paymentId

    # Generate HTML form for easy testing
    $htmlForm = @"
<!DOCTYPE html>
<html>
<head>
    <title>Fiuu Payment Test</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; }
        h1 { color: #333; }
        .info { background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 20px 0; }
        button { background: #007bff; color: white; padding: 15px 30px; font-size: 16px; border: none; border-radius: 5px; cursor: pointer; }
        button:hover { background: #0056b3; }
        .note { color: #666; font-size: 14px; margin-top: 20px; }
    </style>
</head>
<body>
    <h1>Fiuu Payment Test</h1>
    <div class="info">
        <strong>Payment ID:</strong> $($response.paymentId)<br>
        <strong>Amount:</strong> MYR $($response.amount)<br>
        <strong>Channel:</strong> FPX Online Banking
    </div>
    
    <form method="POST" action="$($response.paymentUrl)">
"@

    foreach ($key in $response.paymentData.PSObject.Properties.Name) {
        $value = $response.paymentData.$key
        $htmlForm += "        <input type=`"hidden`" name=`"$key`" value=`"$value`">`n"
    }

    $htmlForm += @"
        <button type="submit">Proceed to Payment</button>
    </form>
    
    <div class="note">
        <h3>Test Credentials</h3>
        <strong>FPX Banking:</strong><br>
        Username: Gaara<br>
        Password: Letmepaywithsand<br><br>
        
        <strong>After Payment:</strong><br>
        Check payment status: <a href="$API_BASE/payment/$paymentId" target="_blank">$API_BASE/payment/$paymentId</a>
    </div>
</body>
</html>
"@

    $htmlFile = "fiuu_payment_test.html"
    $htmlForm | Out-File -FilePath $htmlFile -Encoding UTF8
    Write-Host "✓ HTML test form generated: $htmlFile" -ForegroundColor Green
    Write-Host ""

    Write-Host "Step 2: Open the HTML file in your browser and complete payment" -ForegroundColor Green
    Write-Host ""
    Write-Host "Test Credentials:" -ForegroundColor Yellow
    Write-Host "  FPX Username: Gaara" -ForegroundColor White
    Write-Host "  FPX Password: Letmepaywithsand" -ForegroundColor White
    Write-Host ""

    # Open the HTML file
    Start-Process $htmlFile

    Write-Host "Step 3: Wait for payment completion..." -ForegroundColor Green
    Write-Host "Press Enter after completing payment to check status, or Ctrl+C to exit" -ForegroundColor Yellow
    Read-Host

    Write-Host ""
    Write-Host "Checking payment status..." -ForegroundColor Green

    $statusResponse = Invoke-RestMethod -Uri "$API_BASE/payment/$paymentId" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $JWT_TOKEN"
        }

    Write-Host ""
    Write-Host "Payment Status:" -ForegroundColor Cyan
    Write-Host "  Status: $($statusResponse.status)" -ForegroundColor $(if ($statusResponse.status -eq "SUCCESS") { "Green" } elseif ($statusResponse.status -eq "FAILED") { "Red" } else { "Yellow" })
    Write-Host "  Gateway Transaction ID: $($statusResponse.gatewayTransactionId)" -ForegroundColor White
    Write-Host "  Completed At: $($statusResponse.completedAt)" -ForegroundColor White
    Write-Host ""

    if ($statusResponse.status -eq "SUCCESS") {
        Write-Host "✓ Payment successful! Wallet should be credited." -ForegroundColor Green
    } elseif ($statusResponse.status -eq "PENDING") {
        Write-Host "⚠ Payment still pending. Check backend logs for webhook calls." -ForegroundColor Yellow
    } elseif ($statusResponse.status -eq "FAILED") {
        Write-Host "✗ Payment failed." -ForegroundColor Red
    }

} catch {
    Write-Host ""
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Common issues:" -ForegroundColor Yellow
    Write-Host "  1. Backend not running? Start with: cd backend && npm run dev" -ForegroundColor Gray
    Write-Host "  2. Invalid token? Login first or update TEST_JWT_TOKEN" -ForegroundColor Gray
    Write-Host "  3. Database not set up? Run schema: psql -f backend/database/complete-schema.sql" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
