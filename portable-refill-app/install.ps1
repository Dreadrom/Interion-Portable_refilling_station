# Installation script for Portable Refill App (Windows)
# Run this script from the portable-refill-app directory

Write-Host "🚀 Portable Refill App - Installation Script" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "package.json")) {
    Write-Host "❌ Error: package.json not found!" -ForegroundColor Red
    Write-Host "Please run this script from the portable-refill-app directory" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
Write-Host ""

# Install main dependencies
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns

# Install dev dependencies
npm install --save-dev @types/node

Write-Host ""
Write-Host "✅ Dependencies installed successfully!" -ForegroundColor Green
Write-Host ""

# Check if .env exists
if (Test-Path ".env") {
    Write-Host "⚠️  .env file already exists, skipping creation" -ForegroundColor Yellow
} else {
    Write-Host "📝 Creating .env file from template..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ .env file created!" -ForegroundColor Green
    Write-Host "⚠️  Please edit .env and add your API endpoint" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "✅ Installation Complete!" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📚 Next Steps:" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Edit .env file with your API endpoint:"
Write-Host "   notepad .env  (or use your favorite editor)"
Write-Host ""
Write-Host "2. Start the development server:"
Write-Host "   npm start"
Write-Host ""
Write-Host "3. Follow the prompts to run on iOS or Android"
Write-Host ""
Write-Host "📖 Documentation:" -ForegroundColor Cyan
Write-Host "   - QUICKSTART.md - Quick start guide"
Write-Host "   - SETUP.md - Detailed setup"
Write-Host "   - ..\..\ARCHITECTURE.md - System architecture"
Write-Host "   - ..\..\PROJECT_SUMMARY.md - Project overview"
Write-Host ""
Write-Host "Happy coding! 🎉" -ForegroundColor Green
