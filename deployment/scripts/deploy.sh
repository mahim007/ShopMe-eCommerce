#!/bin/bash

set -e  # Exit on any error

echo "========================================"
echo "🚀 ShopMe Deployment Script"
echo "========================================"

DEPLOY_DIR="$HOME/shopme-deployment"
BACKUP_DIR="$HOME/shopme-backup"

# Create directories
mkdir -p $DEPLOY_DIR $BACKUP_DIR

# Copy deployment files
#echo "📦 Setting up deployment directory..."
#cp -r deployment/* $DEPLOY_DIR/

# Clone application code
echo "📥 Cloning application code..."
cd $DEPLOY_DIR
if [ -d "app" ]; then
    cd app && git pull origin master && cd ..
else
    git clone https://github.com/mahim007/ShopMe-eCommerce.git app
fi

# Build and deploy
echo "🐳 Building and deploying containers..."
cd $DEPLOY_DIR
docker-compose down || true     # Stop existing containers
docker-compose build --no-cache
docker-compose up -d

echo "✅ Deployment completed!"
echo ""
echo "Access your application:"
echo "Frontend: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8081/shopme"
echo "Backend: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8080/shopme-admin"