#!/bin/bash

# Installation script for Portable Refill App
# Run this script from the portable-refill-app directory

echo "🚀 Portable Refill App - Installation Script"
echo "==========================================="
echo ""

# Check if we're in the right directory
if [ ! -f "package.json" ]; then
    echo "❌ Error: package.json not found!"
    echo "Please run this script from the portable-refill-app directory"
    exit 1
fi

echo "📦 Installing dependencies..."
echo ""

# Install main dependencies
npm install axios zustand @tanstack/react-query expo-secure-store react-native-qrcode-svg date-fns

# Install dev dependencies
npm install --save-dev @types/node

echo ""
echo "✅ Dependencies installed successfully!"
echo ""

# Check if .env exists
if [ -f ".env" ]; then
    echo "⚠️  .env file already exists, skipping creation"
else
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "✅ .env file created!"
    echo "⚠️  Please edit .env and add your API endpoint"
fi

echo ""
echo "==========================================="
echo "✅ Installation Complete!"
echo "==========================================="
echo ""
echo "📚 Next Steps:"
echo ""
echo "1. Edit .env file with your API endpoint:"
echo "   nano .env  (or use your favorite editor)"
echo ""
echo "2. Start the development server:"
echo "   npm start"
echo ""
echo "3. Follow the prompts to run on iOS or Android"
echo ""
echo "📖 Documentation:"
echo "   - QUICKSTART.md - Quick start guide"
echo "   - SETUP.md - Detailed setup"
echo "   - ../docs/architecture/ARCHITECTURE.md - System architecture"
echo "   - ../docs/guides/PROJECT_SUMMARY.md - Project overview"
echo ""
echo "Happy coding! 🎉"
