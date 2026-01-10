#!/bin/bash
set -e

echo "========================================"
echo "🚀 ShopMe Deployment Script"
echo "========================================"

DEPLOY_DIR="$HOME/shopme-deployment"

mkdir -p "$DEPLOY_DIR"

# ----------------------------------------
# Clone or update code
# ----------------------------------------
echo "📥 Updating application code..."
cd "$DEPLOY_DIR"

if [ -d "app/.git" ]; then
    cd app
    git fetch origin
    git reset --hard origin/master
    cd ..
else
    git clone https://github.com/mahim007/ShopMe-eCommerce.git app
fi

# ----------------------------------------
# Stop running containers
# ----------------------------------------
echo "🛑 Stopping containers..."
docker-compose down

# ----------------------------------------
# Build only changed services (uses cache)
# ----------------------------------------
echo "🐳 Building backend & frontend..."
docker-compose build backend frontend

# ----------------------------------------
# Start containers
# ----------------------------------------
echo "▶️ Starting services..."
docker-compose up -d

# ----------------------------------------
# Safe cleanup (DO NOT break cache)
# ----------------------------------------
echo "🧹 Cleaning old Docker resources..."
docker image prune -f --filter "until=168h"      # images unused for 7 days
docker builder prune -f --filter "until=168h"    # old build cache
docker container prune -f
docker volume prune -f

# ----------------------------------------
# Done
# ----------------------------------------
PUBLIC_IP=$(get_public_ip)

echo "========================================"
echo "✅ Deployment completed!"
echo "Frontend: http://$PUBLIC_IP:8081/shopme"
echo "Backend : http://$PUBLIC_IP:8080/shopme-admin"
echo "========================================"


get_public_ip() {
  TOKEN=$(curl -s -X PUT "http://169.254.169.254/latest/api/token" \
    -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")

  if [ -z "$TOKEN" ]; then
    echo "localhost"
    return
  fi

  curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
    http://169.254.169.254/latest/meta-data/public-ipv4 \
    || echo "localhost"
}