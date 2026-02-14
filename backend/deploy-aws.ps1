# AWS Deployment Script for Portable Refill Station
# This script sets up the complete AWS infrastructure

$ErrorActionPreference = "Stop"
$Region = "ap-southeast-1"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Portable Refill Station - AWS Deployment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$DB_NAME = "portable_refill_db"
$DB_USERNAME = "admin"
$DB_PASSWORD = "PortableRefill2026!" # Change this to a secure password
$DB_INSTANCE_ID = "portable-refill-db"
$JWT_SECRET = "your-secret-jwt-key-change-this-in-production-$(Get-Random)"

Write-Host "Step 1: Creating RDS PostgreSQL Database..." -ForegroundColor Yellow
Write-Host "This will take 5-10 minutes..." -ForegroundColor Gray

# Check if RDS instance already exists
$existingDB = aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_ID --region $Region 2>$null | ConvertFrom-Json

if ($existingDB) {
    Write-Host "  Database already exists: $DB_INSTANCE_ID" -ForegroundColor Green
    $DB_ENDPOINT = $existingDB.DBInstances[0].Endpoint.Address
} else {
    Write-Host "  Creating new RDS instance..." -ForegroundColor Gray
    
    # Create DB subnet group first
    $subnets = (aws ec2 describe-subnets --region $Region --query 'Subnets[0:2].SubnetId' --output json | ConvertFrom-Json)
    
    try {
        aws rds create-db-subnet-group `
            --db-subnet-group-name portable-refill-subnet-group `
            --db-subnet-group-description "Subnet group for portable refill DB" `
            --subnet-ids $subnets `
            --region $Region 2>$null
    } catch {
        Write-Host "  Subnet group may already exist, continuing..." -ForegroundColor Gray
    }
    
    # Create RDS instance
    $dbResult = aws rds create-db-instance `
        --db-instance-identifier $DB_INSTANCE_ID `
        --db-instance-class db.t3.micro `
        --engine postgres `
        --engine-version "16.4" `
        --master-username $DB_USERNAME `
        --master-user-password $DB_PASSWORD `
        --allocated-storage 20 `
        --db-name $DB_NAME `
        --publicly-accessible `
        --backup-retention-period 7 `
        --region $Region `
        --output json | ConvertFrom-Json
    
    Write-Host "  Waiting for database to become available..." -ForegroundColor Gray
    aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_ID --region $Region
    
    # Get endpoint
    $dbInfo = aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_ID --region $Region | ConvertFrom-Json
    $DB_ENDPOINT = $dbInfo.DBInstances[0].Endpoint.Address
    
    Write-Host "  Database created successfully!" -ForegroundColor Green
}

Write-Host "  Database Endpoint: $DB_ENDPOINT" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 2: Installing backend dependencies..." -ForegroundColor Yellow
npm install --prefix .
Write-Host "  Dependencies installed!" -ForegroundColor Green
Write-Host ""

Write-Host "Step 3: Building TypeScript code..." -ForegroundColor Yellow
npm run build --prefix .
Write-Host "  Build completed!" -ForegroundColor Green
Write-Host ""

Write-Host "Step 4: Creating Lambda IAM Role..." -ForegroundColor Yellow

$trustPolicy = @"
{
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
}
"@

$trustPolicyFile = "trust-policy.json"
$trustPolicy | Out-File -FilePath $trustPolicyFile -Encoding utf8

try {
    $roleResult = aws iam create-role `
        --role-name PortableRefillLambdaRole `
        --assume-role-policy-document "file://$trustPolicyFile" `
        --region $Region | ConvertFrom-Json
    
    $roleArn = $roleResult.Role.Arn
    Write-Host "  Role created: $roleArn" -ForegroundColor Green
    
    # Attach policies
    aws iam attach-role-policy `
        --role-name PortableRefillLambdaRole `
        --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole `
        --region $Region
    
    aws iam attach-role-policy `
        --role-name PortableRefillLambdaRole `
        --policy-arn arn:aws:iam::aws:policy/AmazonRDSFullAccess `
        --region $Region
    
    Write-Host "  Waiting for role to be ready..." -ForegroundColor Gray
    Start-Sleep -Seconds 10
    
} catch {
    # Role may already exist
    $existingRole = aws iam get-role --role-name PortableRefillLambdaRole --region $Region | ConvertFrom-Json
    $roleArn = $existingRole.Role.Arn
    Write-Host "  Using existing role: $roleArn" -ForegroundColor Green
}

Remove-Item -Path $trustPolicyFile -ErrorAction SilentlyContinue
Write-Host ""

Write-Host "Step 5: Creating deployment packages..." -ForegroundColor Yellow

# Create deployment package for AuthHandler
Set-Location dist
Compress-Archive -Path * -DestinationPath ../AuthHandler.zip -Force
Set-Location ..

Write-Host "  Deployment packages created!" -ForegroundColor Green
Write-Host ""

Write-Host "Step 6: Deploying Lambda functions..." -ForegroundColor Yellow

$envVars = @{
    Variables = @{
        DB_HOST = $DB_ENDPOINT
        DB_PORT = "5432"
        DB_NAME = $DB_NAME
        DB_USER = $DB_USERNAME
        DB_PASSWORD = $DB_PASSWORD
        JWT_SECRET = $JWT_SECRET
        JWT_EXPIRES_IN = "7d"
        NODE_ENV = "production"
    }
} | ConvertTo-Json -Compress

# Deploy AuthHandler
try {
    Write-Host "  Creating AuthHandler Lambda..." -ForegroundColor Gray
    $authLambda = aws lambda create-function `
        --function-name AuthHandler `
        --runtime nodejs20.x `
        --role $roleArn `
        --handler handlers/AuthHandler.handler `
        --zip-file fileb://AuthHandler.zip `
        --timeout 30 `
        --memory-size 512 `
        --environment $envVars `
        --region $Region | ConvertFrom-Json
    
    Write-Host "  AuthHandler created!" -ForegroundColor Green
} catch {
    Write-Host "  AuthHandler may exist, updating..." -ForegroundColor Gray
    aws lambda update-function-code `
        --function-name AuthHandler `
        --zip-file fileb://AuthHandler.zip `
        --region $Region
    
    aws lambda update-function-configuration `
        --function-name AuthHandler `
        --environment $envVars `
        --region $Region
    
    Write-Host "  AuthHandler updated!" -ForegroundColor Green
}

# Deploy StationsHandler
try {
    Write-Host "  Creating StationsHandler Lambda..." -ForegroundColor Gray
    $stationsLambda = aws lambda create-function `
        --function-name StationsHandler `
        --runtime nodejs20.x `
        --role $roleArn `
        --handler handlers/StationsHandler.handler `
        --zip-file fileb://AuthHandler.zip `
        --timeout 30 `
        --memory-size 512 `
        --environment $envVars `
        --region $Region | ConvertFrom-Json
    
    Write-Host "  StationsHandler created!" -ForegroundColor Green
} catch {
    Write-Host "  StationsHandler may exist, updating..." -ForegroundColor Gray
    aws lambda update-function-code `
        --function-name StationsHandler `
        --zip-file fileb://AuthHandler.zip `
        --region $Region
    
    aws lambda update-function-configuration `
        --function-name StationsHandler `
        --environment $envVars `
        --region $Region
    
    Write-Host "  StationsHandler updated!" -ForegroundColor Green
}

Write-Host ""

Write-Host "Step 7: Creating API Gateway..." -ForegroundColor Yellow

try {
    # Create HTTP API
    $apiResult = aws apigatewayv2 create-api `
        --name "Portable-Refill-API" `
        --protocol-type HTTP `
        --region $Region | ConvertFrom-Json
    
    $apiId = $apiResult.ApiId
    $apiEndpoint = $apiResult.ApiEndpoint
    
    Write-Host "  API created: $apiEndpoint" -ForegroundColor Green
    
    # Create integration for AuthHandler
    $authIntegration = aws apigatewayv2 create-integration `
        --api-id $apiId `
        --integration-type AWS_PROXY `
        --integration-uri "arn:aws:lambda:$($Region):$((aws sts get-caller-identity --query Account --output text)):function:AuthHandler" `
        --payload-format-version "2.0" `
        --region $Region | ConvertFrom-Json
    
    # Create integration for StationsHandler
    $stationsIntegration = aws apigatewayv2 create-integration `
        --api-id $apiId `
        --integration-type AWS_PROXY `
        --integration-uri "arn:aws:lambda:$($Region):$((aws sts get-caller-identity --query Account --output text)):function:StationsHandler" `
        --payload-format-version "2.0" `
        --region $Region | ConvertFrom-Json
    
    # Create routes
    aws apigatewayv2 create-route `
        --api-id $apiId `
        --route-key "POST /auth/register" `
        --target "integrations/$($authIntegration.IntegrationId)" `
        --region $Region
    
    aws apigatewayv2 create-route `
        --api-id $apiId `
        --route-key "POST /auth/login" `
        --target "integrations/$($authIntegration.IntegrationId)" `
        --region $Region
    
    aws apigatewayv2 create-route `
        --api-id $apiId `
        --route-key "GET /auth/me" `
        --target "integrations/$($authIntegration.IntegrationId)" `
        --region $Region
    
    aws apigatewayv2 create-route `
        --api-id $apiId `
        --route-key "GET /stations" `
        --target "integrations/$($stationsIntegration.IntegrationId)" `
        --region $Region
    
    # Create $default stage
    aws apigatewayv2 create-stage `
        --api-id $apiId `
        --stage-name '$default' `
        --auto-deploy `
        --region $Region
    
    # Grant API Gateway permission to invoke Lambda
    $accountId = aws sts get-caller-identity --query Account --output text
    
    aws lambda add-permission `
        --function-name AuthHandler `
        --statement-id apigateway-invoke-auth `
        --action lambda:InvokeFunction `
        --principal apigateway.amazonaws.com `
        --source-arn "arn:aws:execute-api:$($Region):$($accountId):$($apiId)/*/*" `
        --region $Region
    
    aws lambda add-permission `
        --function-name StationsHandler `
        --statement-id apigateway-invoke-stations `
        --action lambda:InvokeFunction `
        --principal apigateway.amazonaws.com `
        --source-arn "arn:aws:execute-api:$($Region):$($accountId):$($apiId)/*/*" `
        --region $Region
    
    Write-Host "  API Gateway configured!" -ForegroundColor Green
} catch {
    Write-Host "  Error creating API Gateway: $_" -ForegroundColor Red
    $apiEndpoint = "(creation failed, check logs)"
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Deployment Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Important Information:" -ForegroundColor Cyan
Write-Host "  API Endpoint: $apiEndpoint" -ForegroundColor White
Write-Host "  Database: $DB_ENDPOINT" -ForegroundColor White
Write-Host "  Database Name: $DB_NAME" -ForegroundColor White
Write-Host "  Database User: $DB_USERNAME" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  SECURITY WARNING:" -ForegroundColor Yellow
Write-Host "  1. Rotate your AWS credentials immediately!" -ForegroundColor Red
Write-Host "  2. Change the DB password in production" -ForegroundColor Red
Write-Host "  3. Update JWT_SECRET in Lambda environment variables" -ForegroundColor Red
Write-Host ""
Write-Host "📱 Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Update mobile app .env file with API endpoint:" -ForegroundColor White
Write-Host "     API_BASE_URL=$apiEndpoint" -ForegroundColor Gray
Write-Host "  2. Connect to database and run schema.sql:" -ForegroundColor White
Write-Host "     psql -h $DB_ENDPOINT -U $DB_USERNAME -d $DB_NAME -f database/schema.sql" -ForegroundColor Gray
Write-Host ""
