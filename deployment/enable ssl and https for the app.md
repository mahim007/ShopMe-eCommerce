# Secure HTTPS Setup on AWS EC2 (Free Domain + Nginx + Certbot)

This guide shows how to:
- Get a **free domain** using DuckDNS
- Configure **Nginx reverse proxy**
- Enable **HTTPS (Let’s Encrypt)**
- Hide application ports (8080 / 8081)
- Secure access using **UFW**

Target OS: Ubuntu (AWS EC2)

---

## 1. Create Free Domain (DuckDNS)

1. Visit https://www.duckdns.org
2. Login (Google / GitHub)
3. Create a subdomain shoptoday.duckdns.org
4. Set IP to your EC2 public IP and save

---

## 2. Install Nginx

```bash
sudo apt update
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

---
## 3. Configure Nginx Reverse Proxy (HTTP)
Create config (copy and paste from /deployment/config/nginx_initial.conf):

```bash
sudo nano /etc/nginx/sites-available/shopme

server {
    listen 80;
    server_name shoptoday.duckdns.org;

    location /shopme/ {
        proxy_pass http://127.0.0.1:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /shopme-admin/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    ...
    ...
}
```

#### Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/shopme /etc/nginx/sites-enabled/
sudo rm /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
```

#### Verify:
http://shoptoday.duckdns.org/shopme/

http://shoptoday.duckdns.org/shopme-admin/

---

### 4. Install Certbot (SSL)
```bash

sudo apt install certbot python3-certbot-nginx -y
```

---

### 5. Enable HTTPS
```bash
sudo certbot --nginx -d shoptoday.duckdns.org
```
Choose:
Redirect HTTP → HTTPS: Yes

Verify auto-renew:

```bash
sudo certbot renew --dry-run
```

---
### 6. Final Nginx HTTPS Config (Reference)
Final nginx config should look similar to this: (copy and paste from /deployment/config/nginx_final.conf):
```nginx
server {
    listen 80;
    server_name shoptoday.duckdns.org;
    return 301 https://$host$request_uri;
}
...
...
```

---

### 7. Enable Firewall (UFW)
```bash
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw enable
sudo ufw status
```

---

### 8. Hide Application Ports
AWS Security Group
Remove:

❌ 8080

❌ 8081

Allow only:

22 (SSH)

80

443

---

✅ Should work:

https://shoptoday.duckdns.org/shopme/

https://shoptoday.duckdns.org/shopme-admin/

---