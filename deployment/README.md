Step 1: Create Directory Structure

# SSH to EC2
ssh -i your-key.pem ubuntu@<EC2-IP>

# Create main deployment directory
mkdir -p ~/shopme-deployment
cd ~/shopme-deployment

# Clone your repo as 'app'
git clone https://github.com/yourusername/ShopMe-eCommerce.git app

Step 2: Copy Deployment Files
# Copy deployment configs to main directory
cp -r app/deployment/* .

# Make scripts executable
chmod +x scripts/*.sh

Step 3: Run Setup
# Run EC2 setup
./scripts/setup-ec2.sh

# Log out and back in
exit

ssh -i your-key.pem ubuntu@<EC2-IP>
cd ~/shopme-deployment

Step 4: Configure Environment
# Create .env file
cp .env.example .env
nano .env  # Add your credentials

Step 5: Deploy
# Run deployment
./scripts/deploy.sh