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


## Tài liệu

- [Phạm vi sản phẩm](docs/features.md)
- [Kế hoạch triển khai](docs/plan.md)
