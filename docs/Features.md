# Vid2Knowledge — Phạm vi sản phẩm & lộ trình MVP

## 1. Mục tiêu sản phẩm

> Người dùng dán một link YouTube công khai, hệ thống dùng Gemini để hiểu trực tiếp nội dung video và tạo học liệu giúp người dùng ôn tập chủ động.

**Đầu ra chính:** tóm tắt có cấu trúc, kiến thức trọng tâm, flashcard và câu hỏi ôn tập có đáp án.

**Đối tượng ban đầu:** sinh viên, người tự học, người xem bài giảng/podcast/video dài muốn học lại hiệu quả hơn.

**Mục tiêu giai đoạn đầu:** xác minh chất lượng đầu ra và nhu cầu thật của người dùng với chi phí thấp; chỉ mở rộng hạ tầng và thanh toán khi đã có tín hiệu sử dụng/quay lại ổn định.

---

## 2. Nguyên tắc kỹ thuật

- Backend gửi trực tiếp **YouTube URL công khai** đến Gemini API để Gemini hiểu cả âm thanh và hình ảnh của video.
- Không tải, lưu hoặc tự lấy transcript/video từ YouTube. Hệ thống chỉ lưu URL, metadata cần thiết và học liệu đã sinh.
- Gemini API key chỉ tồn tại ở backend; không đưa key ra frontend.
- Kết quả AI phải tuân thủ một JSON schema cố định để giao diện hiển thị ổn định, dễ lưu lịch sử và xuất file sau này.
- Một lần xử lý tạo đủ các sản phẩm học tập; không gọi Gemini riêng lẻ cho từng tab nếu không cần thiết.
- Cache theo `videoId + cấu hình đầu ra` để tránh xử lý lại cùng một video và tiết kiệm quota/chi phí.

### Giới hạn cần chấp nhận ở MVP

- Chỉ hỗ trợ video YouTube **công khai**; không cam kết hỗ trợ private/unlisted video.
- Khả năng truyền URL YouTube trực tiếp của Gemini đang ở preview, vì vậy cần đo tỷ lệ thành công và có xử lý lỗi rõ ràng.
- Kết quả AI có thể sai hoặc thiếu; giao diện cần hiển thị cảnh báo ngắn và cho phép người dùng mở video gốc để đối chiếu.

---

## 3. Knowledge package (đầu ra chuẩn)

Mỗi yêu cầu thành công sinh một gói học liệu gồm:

```json
{
  "video": { "youtubeUrl": "https://www.youtube.com/watch?v=...", "title": "...", "language": "vi" },
  "summary": {
    "overview": "...",
    "sections": [{ "title": "...", "timestamp": "00:00", "content": ["..."] }]
  },
  "keyTakeaways": ["..."],
  "flashcards": [{ "question": "...", "answer": "...", "timestamp": "00:00" }],
  "quiz": [{ "question": "...", "options": ["...", "...", "...", "..."], "correctAnswerIndex": 0, "explanation": "...", "timestamp": "00:00" }]
}
```

Schema thực tế sẽ được validate ở backend trước khi lưu/trả về frontend.

---

## 4. Scope MVP

### Must-have

| STT | Chức năng | Mô tả | Tiêu chí hoàn thành |
|---|---|---|---|
| 1 | Nhận link YouTube | Kiểm tra URL, chuẩn hoá video ID và chỉ nhận video công khai. | Báo lỗi rõ ràng với link sai/không hỗ trợ. |
| 2 | Xử lý bằng Gemini | Backend gọi Gemini với YouTube URL và prompt/schema chuẩn. | Có trạng thái `queued`, `processing`, `completed`, `failed`. |
| 3 | Tóm tắt có cấu trúc | Tóm tắt theo phần, bullet points và timestamp khi Gemini trả được. | Người dùng đọc được ý chính không cần xem lại toàn bộ video. |
| 4 | Flashcard | Sinh 10 cặp hỏi–đáp từ nội dung video. | Có thể lật thẻ và xem đáp án. |
| 5 | Quiz | Sinh 5 câu trắc nghiệm, đáp án và giải thích. | Người dùng làm bài, xem điểm và đáp án. |
| 6 | Trang kết quả | Tabs/sections rõ ràng cho summary, flashcard, quiz. | Copy được nội dung; có link quay lại video gốc. |
| 7 | Đăng nhập | Google sign-in hoặc email/password. | Dữ liệu gắn với người dùng. |
| 8 | Lịch sử | Xem và mở lại các learning package đã tạo. | Không gọi Gemini lại khi mở lại kết quả. |
| 9 | Quota & chống lạm dụng | Free: 3 video/tháng/người dùng; rate limit theo user/IP. | Vượt quota được chặn trước khi gọi Gemini. |

### Should-have (sau khi MVP lõi chạy ổn)

| STT | Chức năng | Ghi chú |
|---|---|---|
| 10 | Copy nhanh | Copy từng section hoặc toàn bộ notes/flashcards. |
| 11 | Xuất Markdown/PDF | Làm Markdown trước, PDF sau; Word chưa ưu tiên. |
| 12 | Theo dõi tiến độ flashcard | Đánh dấu đã thuộc/chưa thuộc, lọc để ôn lại. |
| 13 | Tuỳ chỉnh đầu ra | Chọn ngôn ngữ, độ chi tiết, số flashcard/quiz trong giới hạn quota. |

### Out of scope ở MVP

- Mobile app native.
- Chat hỏi đáp riêng với video.
- Workspace/team collaboration.
- Thanh toán và gói Pro hoàn chỉnh.
- Hỗ trợ video private/unlisted hoặc tải video lên.
- Multi-language UI phức tạp (AI vẫn có thể xử lý nội dung Việt/Anh).

---

## 5. Kiến trúc triển khai ban đầu

| Thành phần | Lựa chọn | Vai trò |
|---|---|---|
| Frontend | React + Vite | Form nhập URL, tiến trình và giao diện học liệu. |
| Backend | Spring Boot | Xác thực, quota, job, gọi Gemini, validate/lưu kết quả. |
| AI | Gemini API | Hiểu trực tiếp video YouTube và tạo knowledge package. |
| Database | PostgreSQL | Người dùng, quota, jobs và learning packages. |
| Deploy frontend | Vercel Hobby | Triển khai web thử nghiệm nhanh. |
| Deploy backend | Google Cloud Run | Scale-to-zero, kiểm soát chi phí giai đoạn đầu. |

## 6. Chỉ số cần đo từ ngày đầu

- Tỷ lệ job hoàn thành/thất bại theo loại video.
- Thời gian từ lúc gửi URL đến lúc có kết quả.
- Số learning package tạo trên mỗi người dùng.
- Tỷ lệ người dùng quay lại sau 7 ngày.
- Chi phí/quota Gemini cho mỗi video thành công.
- Tỷ lệ người dùng copy, làm quiz hoặc mở lại flashcard.
