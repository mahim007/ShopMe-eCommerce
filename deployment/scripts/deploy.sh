#!/bin/bash
set -e

echo "========================================"
echo "🚀 ShopMe Deployment Script"
echo "========================================"

DEPLOY_DIR="$HOME/shopme-deployment"
BACKUP_DIR="$HOME/shopme-backup"

mkdir -p $DEPLOY_DIR $BACKUP_DIR

# Clone / update repo
echo "📥 Updating application code..."
cd $DEPLOY_DIR
if [ -d "app" ]; then
    cd app && git fetch origin && git reset --hard origin/master && cd ..
else
    git clone https://github.com/mahim007/ShopMe-eCommerce.git app
fi

# Build & deploy containers
echo "🐳 Building and deploying containers..."
cd $DEPLOY_DIR
docker-compose up -d --build backend frontend

# delete unused/old stale images
docker image prune -f

echo "✅ Deployment completed!"
echo ""
PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
echo "Frontend: http://$PUBLIC_IP:8081/shopme"
echo "Backend:  http://$PUBLIC_IP:8080/shopme-admin"
