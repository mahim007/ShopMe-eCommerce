MySQL Dump Import – Quick Steps (Docker + EC2)

SSH into EC2 and go to the deployment directory:

cd ~/shopme-deployment


Ensure MySQL container is running:

docker ps | grep shopme-mysql


Import the SQL dump into the database:

docker exec -i shopme-mysql mysql -uadmin -pPPPP shopmedb < Dump2026WXYZ.sql


(Enter the database password when prompted; the security warning is normal.)

Verify the import:

docker exec -it shopme-mysql mysql -uadmin -p shopmedb

SHOW TABLES;