# Flash Sale System

This is a high-performance Flash Sale System that efficiently handles high-concurrency transactions.

---

## How to Run Flash Sale System

### Prerequisites
- Install [Java 8+](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
- Install [Maven](https://maven.apache.org/download.cgi)
- Install [RocketMQ](https://rocketmq.apache.org/release_notes/release-notes-4.7.1/)
- Install [Redis](https://redis.io/docs/getting-started/installation/)
- Install [MySQL](https://dev.mysql.com/downloads/installer/)

---

## Start Required Services

### 1. Start RocketMQ
- Start NameServer:
  ```bash
  mqnamesrv
  ```
- Start Broker:
  ```bash
  mqbroker -n localhost:9876
  ```

### 2. Start Redis
- Run Redis server:
  ```bash
  redis-server
  ```
- Verify Redis is running:
  ```bash
  redis-cli ping
  ```
  (should return `PONG`)

### 3. Start MySQL
- Run MySQL server and create a database:
  ```sql
  CREATE DATABASE flash_sale_db;
  ```
- Update `application.yml` with correct MySQL username & password.

---

## Run Flash Sale System

1. Clone the repository:
   ```bash
   git clone https://github.com/Szh022401/FlashSale-System.git
   ```
2. Navigate to the project directory:
   ```bash
   cd FlashSale-System
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## Verify System is Running
- Access the API at: [http://localhost:8080](http://localhost:8080)
- Check logs for errors if the system does not start correctly.

---

## Tech Stack
- **Backend**: Spring Boot, RocketMQ, Redis, MySQL
- **Frontend**: (If applicable, add details)
- **Build Tool**: Maven




![image](https://github.com/user-attachments/assets/9eff2263-a8a0-4f75-85e0-ab2810fe1025)
![image](https://github.com/user-attachments/assets/ee6c717c-1e58-4054-93e7-9e6773839019)
