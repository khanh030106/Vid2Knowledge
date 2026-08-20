# Vid2Knowledge

Vid2Knowledge biến video YouTube thành học liệu để người xem ôn tập chủ động.

Người dùng dán một link YouTube công khai, ứng dụng gửi trực tiếp URL đó đến Gemini API để tạo:

- Tóm tắt có cấu trúc theo từng phần
- Key takeaways
- Flashcards hỏi–đáp
- Quiz có đáp án và giải thích

## Tech stack

- Frontend: React + Vite
- Backend: Spring Boot
- Database: PostgreSQL
- AI: Gemini API
- Deploy dự kiến: Vercel (frontend) và Google Cloud Run (backend)

## Kiến trúc dự kiến

```text
React + Vite frontend
        ↓
Spring Boot API
   ↓             ↓
Gemini API   PostgreSQL
```

Gemini API key chỉ được cấu hình ở backend; không được đưa vào mã nguồn frontend hoặc commit vào Git.

## Điều kiện phát triển local

- Node.js và npm
- Java 25 (theo cấu hình hiện tại của backend)
- Docker Desktop hoặc một PostgreSQL đang chạy local
- Gemini API key (cần khi triển khai flow phân tích video)

## Chạy dự án local

### Cấu hình môi trường backend

Spring Boot đọc các biến môi trường của terminal hoặc IDE; không tự động đọc file `.env`.

Thiết lập các biến sau trong Run Configuration của IDE hoặc terminal trước khi chạy backend:

```text
SPRING_PROFILES_ACTIVE=local
DB_URL=jdbc:postgresql://localhost:5433/mybusiness
DB_USERNAME=<postgres-user>
DB_PASSWORD=<postgres-password>
GEMINI_API_KEY=<gemini-api-key>
```

Trên Windows, Java cần dùng timezone IANA chuẩn để PostgreSQL chấp nhận kết nối:

```text
JAVA_TOOL_OPTIONS=-Duser.timezone=Asia/Ho_Chi_Minh
```

> `GEMINI_API_KEY` chưa được sử dụng cho đến Phase 1, nhưng phải luôn được giữ ở environment variables và không commit vào Git.

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

Vite sẽ hiển thị URL local sau khi khởi động.

### PostgreSQL với Docker

Từ root của repository:

```powershell
docker compose up -d
docker compose ps
```

Docker PostgreSQL được expose tại `localhost:5433`; backend kết nối đến database bên trong container qua `localhost:5433`, không phải port PostgreSQL cài trực tiếp trên máy.

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Chạy test backend:

```powershell
cd backend
.\mvnw.cmd test
```

### Health check

Sau khi backend khởi động, kiểm tra Actuator health endpoint:

```powershell
curl.exe http://localhost:8080/actuator/health
```

## API error format

Các API nghiệp vụ sẽ trả lỗi JSON thống nhất để frontend xử lý ổn định:

```json
{
  "timestamp": "2026-08-20T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/example"
}
```

Backend không ghi request body, header hay giá trị cấu hình vào log lỗi chung; tuyệt đối không log `GEMINI_API_KEY`.

## Tài liệu

- [Phạm vi sản phẩm](docs/features.md)
- [Kế hoạch triển khai](docs/plan.md)

## Trạng thái

Đã hoàn tất baseline local: frontend build được, backend khởi động với PostgreSQL Docker và Flyway trên database sạch. Bước tiếp theo là Phase 1 — proof of feasibility với Gemini và YouTube URL công khai.
