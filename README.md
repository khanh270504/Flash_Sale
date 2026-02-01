# 🚀 Flash Sale System – Spring Boot

Hệ thống **Flash Sale** được xây dựng để xử lý **lượng truy cập lớn**, tối ưu hiệu năng bằng **Redis Cache** và **xử lý bất đồng bộ với RabbitMQ**.  
Toàn bộ dự án được **đóng gói bằng Docker** giúp triển khai nhanh và nhất quán.

---

## 🧩 Mô tả dự án

- Hỗ trợ Flash Sale với số lượng người dùng lớn cùng lúc
- Giảm tải Database bằng Redis
- Xử lý đơn hàng bất đồng bộ qua RabbitMQ
- Tích hợp thanh toán **VNPay Sandbox**
- Triển khai nhanh bằng Docker & Docker Compose

---

## 🛠 Tech Stack

**Backend**
- Java 21
- Spring Boot 3.5.x
- Spring Data JPA

**Database**
- PostgreSQL

**Caching**
- Redis (cache sản phẩm, số lượt mua)

**Message Broker**
- RabbitMQ (xử lý đơn hàng bất đồng bộ)

**Payment**
- VNPay Sandbox

**DevOps**
- Docker
- Docker Compose

---

## ⚙️ Hướng dẫn khởi động nhanh (Docker)

### 1. Chuẩn bị file môi trường

Tạo file `.env.docker` tại thư mục gốc dự án:

```env
SPRING_APPLICATION_NAME=flashsale-core

# PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/flashsale_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123456
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Connection Pool
SPRING_DATASOURCE_MAX_POOL_SIZE=20
SPRING_DATASOURCE_MIN_IDLE=5

# RabbitMQ Consumer
RABBITMQ_CONCURRENCY=5
RABBITMQ_MAX_CONCURRENCY=10
RABBITMQ_PREFETCH=1

# VNPay Sandbox
VNPAY_TMN_CODE=your_tmn_code
VNPAY_HASH_SECRET=your_hash_secret
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/api/payments/vnpay-callback
```
2. Build Docker Image
docker build -t myusername/flash-sale:latest .

3. Chạy hệ thống với Docker Compose
docker-compose up -d --build
Docker Hub: docker pull helloworld22123/flash-sale:0.9.0
Khi xong vào http://localhost:8080/swagger-ui/index.html để test các api
   ốn kiểm tra sự tranh chấp sử dụng jmeter hoặc chạy file Test trong project
