#!/bin/bash

echo "========================================"
echo "🛠️  EC2 Initial Setup Script"
echo "========================================"

# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
sudo apt-get install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

#Install Git
sudo apt-get install git -y

echo ""
echo "✅ EC2 setup completed!"
echo ""
echo "📝 NEXT STEPS:"
echo "1. Log out and log back in: exit"
echo "2. SSH again: ssh -i your-key.pem ubuntu@<ec2-ip>"
echo "3. Run deployment: ./deploy.sh"