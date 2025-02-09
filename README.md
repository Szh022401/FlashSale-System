# FlashSale-System

How to Run Flash Sale System

Prerequisites

Install Java 8+

Install Maven

Install RocketMQ

Install Redis

Install MySQL

Start Required Services

1. Start RocketMQ

Start NameServer:

mqnamesrv

Start Broker:

mqbroker -n localhost:9876

2. Start Redis

Run Redis server:

redis-server

Verify Redis is running:

redis-cli ping

(should return PONG)

3. Start MySQL

Run MySQL server and create a database:

CREATE DATABASE flash_sale_db;

Update application.yml with correct MySQL username & password.

Run Flash Sale System

Clone the repository:

git clone https://github.com/Szh022401/FlashSale-System.git

Navigate to the project directory:

cd FlashSale-System

Build the project:

mvn clean install

Run the application:

mvn spring-boot:run

Verify System is Running

Access the API at: http://localhost:8080

Check logs for errors if the system does not start correctly.


![image](https://github.com/user-attachments/assets/9eff2263-a8a0-4f75-85e0-ab2810fe1025)
![image](https://github.com/user-attachments/assets/ee6c717c-1e58-4054-93e7-9e6773839019)
