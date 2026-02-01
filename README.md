Flash Sale System - Spring Boot
Hệ thống Flash Sale xử lý lượng truy cập lớn, tối ưu hiệu năng bằng Caching (Redis) và hàng đợi (RabbitMQ). Dự án được đóng gói hoàn toàn bằng Docker để triển khai nhanh chóng.

Tech Stack
Backend: Java 21, Spring Boot 3.5.x, Spring Data JPA.

Frontend: ReactJS (Vite).

Database: PostgreSQL.

Message Broker: RabbitMQ (Xử lý đơn hàng bất đồng bộ).

Caching: Redis (Lưu trữ thông tin sản phẩm và lượt mua).

Thanh toán: Tích hợp VNPAY Sandbox.

DevOps: Docker, Docker Compose.

Hướng dẫn khởi động nhanh (với Docker)
1. Chuẩn bị file môi trường
Dự án sử dụng biến môi trường để bảo mật. Bạn cần tạo file .env.docker tại thư mục gốc và copy nội dung từ file mẫu:

SPRING_APPLICATION_NAME=flashsale-core


SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/flashsale_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123456
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver


SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379


SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

SPRING_DATASOURCE_MAX_POOL_SIZE=20
SPRING_DATASOURCE_MIN_IDLE=5
RABBITMQ_CONCURRENCY=5
RABBITMQ_MAX_CONCURRENCY=10
RABBITMQ_PREFETCH=1

# VNPay
VNPAY_TMN_CODE=code cua ban
VNPAY_HASH_SECRET=key cua ban
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/api/payments/vnpay-callback

Khởi động hệ thống
Mở Terminal tại thư mục gốc và chạy lệnh :
+, Build image tu Dockerfile:
docker build -t myusername/my-image-name:tag-name .

+, Chạy docker-compose:
docker-compose up -d --build

Docker Hub: docker pull helloworld22123/flash-sale:0.9.0

                
