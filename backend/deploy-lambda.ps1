# Quick AWS Lambda Deployment Script
# Deploy Lambda functions step by step

$ErrorActionPreference = "Stop"
$Region = "ap-southeast-1"

Write-Host "Creating Lambda deployment package..." -ForegroundColor Yellow

# Create deployment package
cd dist
if (Test-Path "../lambda-deploy.zip") { Remove-Item "../lambda-deploy.zip" }

# Copy node_modules to dist for deployment
Copy-Item -Path "../node_modules" -Destination "node_modules" -Recurse -Force

Compress-Archive -Path * -DestinationPath ../lambda-deploy.zip -Force

# Clean up
Remove-Item -Path "node_modules" -Recurse -Force

cd ..

Write-Host "✓ Deployment package created" -ForegroundColor Green
Write-Host "Size: $((Get-Item lambda-deploy.zip).Length / 1MB) MB" -ForegroundColor Gray

# Create IAM role
Write-Host ""
Write-Host "Creating IAM role..." -ForegroundColor Yellow

$trustPolicy = '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}'

$trustPolicyFile = "trust-policy.json"
Set-Content -Path $trustPolicyFile -Value $trustPolicy

$roleArn = ""
try {
    $roleResult = aws iam create-role `
        --role-name PortableRefillLambdaRole `
        --assume-role-policy-document "file://$trustPolicyFile" | ConvertFrom-Json
    
    $roleArn = $roleResult.Role.Arn
    Write-Host "✓ Role created: $roleArn" -ForegroundColor Green
    
    # Attach policies
    aws iam attach-role-policy `
        --role-name PortableRefillLambdaRole `
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole
    
    Write-Host "✓ Attached Lambda execution policy" -ForegroundColor Green
    
    Write-Host "  Waiting 10 seconds for IAM role propagation..." -ForegroundColor Gray
    Start-Sleep -Seconds 10
    
} catch {
    Write-Host "  Role may already exist, fetching..." -ForegroundColor Gray
    $existingRole = aws iam get-role --role-name PortableRefillLambdaRole | ConvertFrom-Json
    $roleArn = $existingRole.Role.Arn
    Write-Host "✓ Using existing role: $roleArn" -ForegroundColor Green
}

Remove-Item -Path $trustPolicyFile -ErrorAction SilentlyContinue

# Environment variables (using RDS instance that will be created separately)
$envVars = '{
    "Variables": {
        "NODE_ENV": "production",
        "JWT_SECRET": "PortableRefillJWT-SecretKey",
        "JWT_EXPIRES_IN": "7d"
    }
}'

# Deploy AuthHandler
Write-Host ""
Write-Host "Deploying AuthHandler Lambda..." -ForegroundColor Yellow

try {
    $authLambda = aws lambda create-function `
        --function-name AuthHandler `
        --runtime nodejs20.x `
        --role $roleArn `
        --handler handlers/AuthHandler.handler `
        --zip-file fileb://lambda-deploy.zip `
        --timeout 30 `
        --memory-size 512 `
        --environment $envVars `
        --region $Region | ConvertFrom-Json
    
    Write-Host "✓ AuthHandler created!" -ForegroundColor Green
} catch {
    Write-Host "  Updating existing AuthHandler..." -ForegroundColor Gray
    aws lambda update-function-code `
        --function-name AuthHandler `
        --zip-file fileb://lambda-deploy.zip `
        --region $Region | Out-Null
    
    aws lambda update-function-configuration `
        --function-name AuthHandler `
        --environment $envVars `
        --region $Region | Out-Null
    
    Write-Host "✓ AuthHandler updated!" -ForegroundColor Green
}

# Deploy StationsHandler
Write-Host ""
Write-Host "Deploying StationsHandler Lambda..." -ForegroundColor Yellow

try {
    $stationsLambda = aws lambda create-function `
        --function-name StationsHandler `
        --runtime nodejs20.x `
        --role $roleArn `
        --handler handlers/StationsHandler.handler `
        --zip-file fileb://lambda-deploy.zip `
        --timeout 30 `
        --memory-size 512 `
        --environment $envVars `
        --region $Region | ConvertFrom-Json
    
    Write-Host "✓ StationsHandler created!" -ForegroundColor Green
} catch {
    Write-Host "  Updating existing StationsHandler..." -ForegroundColor Gray
    aws lambda update-function-code `
        --function-name StationsHandler `
        --zip-file fileb://lambda-deploy.zip `
        --region $Region | Out-Null
    
    aws lambda update-function-configuration `
        --function-name StationsHandler `
        --environment $envVars `
        --region $Region | Out-Null
    
    Write-Host "✓ StationsHandler updated!" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Lambda Functions Deployed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next: Run .\deploy-api-gateway.ps1 to create API Gateway" -ForegroundColor Cyan
