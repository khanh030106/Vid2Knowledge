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

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

Vite sẽ hiển thị URL local sau khi khởi động.

### PostgreSQL với Docker

```powershell
cd backend
docker compose up -d
```

File `backend/compose.yaml` hiện tạo PostgreSQL cho môi trường local. Không dùng các thông tin mặc định trong file này cho production.

### Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

> Cấu hình kết nối database và biến môi trường Gemini sẽ được bổ sung cùng Phase 0 trước khi tích hợp API.

## Tài liệu

- [Phạm vi sản phẩm](docs/Features.md)
- [Kế hoạch triển khai](docs/Plan.md)

## Trạng thái

Dự án đang ở giai đoạn proof of feasibility: xác minh Gemini có thể tạo learning package đáng tin cậy từ YouTube URL công khai trước khi phát triển MVP đầy đủ.
