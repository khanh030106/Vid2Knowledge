# Vid2Knowledge — Kế hoạch triển khai

## 1. Mục tiêu triển khai

Xác minh nhanh liệu Gemini có thể nhận trực tiếp YouTube URL công khai và tạo học liệu đủ tốt cho người tự học hay không. Nếu khả thi, phát triển thành MVP có người dùng thật với chi phí vận hành thấp.

Phạm vi sản phẩm, cấu trúc dữ liệu đầu ra và các giới hạn kỹ thuật được mô tả tại [Features.md](./Features.md).

---

## 2. Nguyên tắc thực hiện

- Xây dựng theo lát cắt dọc: một luồng hoàn chỉnh từ dán link đến nhận kết quả, rồi mới mở rộng.
- Không xây thanh toán, PDF/Word hoặc tính năng phức tạp trước khi xác minh chất lượng học liệu.
- Không để Gemini API key ở frontend.
- Mỗi lần gọi Gemini phải được ghi log trạng thái, thời gian và lỗi để đánh giá feasibility và chi phí.
- Ưu tiên output có schema JSON ổn định thay vì văn bản Markdown tự do.

---

## 3. Phase 0 — Chuẩn bị dự án

### Mục tiêu

Chuẩn bị môi trường local và quy ước kỹ thuật để có thể phát triển, kiểm thử và bảo mật cấu hình.

### Công việc

1. Xác nhận frontend chạy với React + Vite và backend chạy với Spring Boot.
2. Thiết lập biến môi trường local:
   - `GEMINI_API_KEY`;
   - cấu hình PostgreSQL;
   - URL frontend/backend cho CORS.
3. Thêm file `.env.example` (không chứa secret) và cập nhật `.gitignore` cho file môi trường thực tế.
4. Chuẩn hoá cấu trúc backend theo các lớp: `controller`, `service`, `client`, `domain`, `repository`, `dto`.
5. Thống nhất format lỗi API và cấu hình logging không làm lộ API key.

### Đầu ra

- Frontend và backend chạy local độc lập.
- API key được đọc từ biến môi trường, không xuất hiện trong Git hoặc frontend bundle.

### Tiêu chí hoàn thành

- Có endpoint health check trả về thành công.
- Một lập trình viên mới có thể chạy local theo hướng dẫn mà không cần sửa mã nguồn.

---

## 4. Phase 1 — Proof of feasibility với Gemini

### Mục tiêu

Chứng minh Gemini hiểu được YouTube URL và có thể trả về learning package đúng schema.

### Công việc backend

1. Tạo `POST /api/v1/analysis/preview` chỉ dùng cho môi trường phát triển.
2. Nhận và validate YouTube URL:
   - chấp nhận dạng `youtube.com/watch`, `youtu.be`, `youtube.com/shorts`;
   - chuẩn hoá thành canonical URL và video ID;
   - từ chối URL không phải YouTube.
3. Tạo Gemini client ở backend:
   - truyền trực tiếp canonical YouTube URL;
   - dùng model Gemini phù hợp tại thời điểm triển khai;
   - đặt timeout, retry có giới hạn và phân loại lỗi.
4. Viết prompt yêu cầu Gemini trả JSON theo schema trong `Features.md`:
   - tóm tắt theo sections;
   - key takeaways;
   - 10 flashcards;
   - 5 quiz questions có đáp án/giải thích;
   - timestamp nếu có đủ căn cứ.
5. Parse và validate JSON trước khi trả về client.
6. Trả lỗi dễ hiểu cho các trường hợp video private/unavailable, Gemini timeout, quota exceeded hoặc output không hợp lệ.

### Công việc frontend tối thiểu

1. Form dán YouTube URL.
2. Trạng thái loading và hiển thị lỗi.
3. Render JSON raw/đơn giản để kiểm tra dữ liệu trước khi đầu tư UI.

### Bộ kiểm thử feasibility

Chuẩn bị 15–20 video công khai gồm:

- bài giảng tiếng Việt;
- bài giảng tiếng Anh;
- podcast dài;
- video ngắn;
- video có slide/màn hình minh hoạ;
- video ít lời thoại;
- video không có transcript công khai.

Ghi nhận cho từng video: thành công/thất bại, thời gian xử lý, lỗi, chất lượng summary, độ đúng flashcard/quiz và mức dùng quota/token.

### Tiêu chí hoàn thành

- Ít nhất 80% bộ video test sinh được learning package hợp lệ.
- Nội dung đủ hữu ích khi đối chiếu thủ công với video gốc.
- Có số liệu về thời gian xử lý và mức dùng Gemini để quyết định quota miễn phí.

---

## 5. Phase 2 — Luồng MVP hoàn chỉnh

### Mục tiêu

Biến proof of feasibility thành luồng sản phẩm sử dụng được từ dán link đến học lại.

### Thiết kế dữ liệu

Tạo migration Flyway và các bảng tối thiểu:

| Bảng | Nội dung chính |
|---|---|
| `users` | Tài khoản và thông tin xác thực cần thiết. |
| `video_analyses` | YouTube video ID, canonical URL, trạng thái xử lý, lỗi, thời điểm tạo. |
| `learning_packages` | JSON kết quả đã validate, liên kết với analysis và user. |
| `usage_records` | Lượt xử lý theo user/tháng, dùng để áp quota. |

### Công việc backend

1. Thêm đăng nhập Google trước; email/password chỉ thêm nếu thực sự cần.
2. Thay endpoint preview bằng API chính:
   - `POST /api/v1/analyses` tạo job;
   - `GET /api/v1/analyses/{id}` lấy trạng thái/kết quả;
   - `GET /api/v1/analyses` lấy lịch sử.
3. Xử lý bất đồng bộ:
   - tạo job ở trạng thái `queued`;
   - worker/service gọi Gemini và cập nhật `processing`, `completed` hoặc `failed`;
   - frontend polling trạng thái ban đầu; SSE có thể thêm sau.
4. Cache theo `videoId + outputConfig`:
   - nếu kết quả cache hợp lệ, trả lại kết quả có sẵn;
   - chỉ tái sử dụng khi phù hợp quyền truy cập và không rò rỉ dữ liệu người dùng.
5. Lưu kết quả JSON đã validate và metadata xử lý.
6. Thêm observability: correlation ID, thời gian Gemini call, mã lỗi và chỉ số job thành công/thất bại.

### Công việc frontend

1. Trang nhập URL và hiển thị tiến trình xử lý.
2. Trang kết quả có các khu vực:
   - overview và summary theo section;
   - key takeaways;
   - flashcards lật mặt;
   - quiz chấm điểm tại client;
   - link mở video gốc tại timestamp (nếu có).
3. Trang lịch sử và mở lại một learning package.
4. Responsive cho desktop và mobile browser.

### Tiêu chí hoàn thành

- Người dùng có thể gửi một video, rời trang/refresh và quay lại xem kết quả sau khi job hoàn thành.
- Mở lại lịch sử không gọi Gemini lần nữa.
- Không có API key hoặc dữ liệu nhạy cảm trong frontend/network response.

---

## 6. Phase 3 — Xác thực, quota và bảo vệ chi phí

### Mục tiêu

Cho phép mở thử nghiệm công khai mà vẫn kiểm soát spam và chi phí Gemini.

### Công việc

1. Áp quota Free: 3 video/tháng/tài khoản.
2. Kiểm tra quota trước khi tạo job, không phải sau khi gọi Gemini.
3. Rate limit theo tài khoản và IP cho endpoint tạo job.
4. Đặt giới hạn thời lượng/loại URL hợp lý nếu kết quả test cho thấy video dài gây chi phí hoặc thất bại cao.
5. Hiển thị số lượt còn lại và lý do khi từ chối xử lý.
6. Thêm giới hạn ngân sách/alert cho Gemini và hạ tầng deploy.

### Tiêu chí hoàn thành

- Không thể tạo job vượt quota bằng refresh/retry song song.
- Các lỗi quota/rate limit có phản hồi rõ ràng.
- Có thể xem lượng dùng theo ngày/tháng để phát hiện bất thường.

---

## 7. Phase 4 — Deploy thử nghiệm

### Mục tiêu

Đưa MVP lên môi trường công khai với chi phí thấp và quy trình triển khai lặp lại được.

### Công việc

1. Deploy frontend lên Vercel Hobby.
2. Docker hoá backend Spring Boot và deploy lên Google Cloud Run, cấu hình scale-to-zero.
3. Dùng PostgreSQL managed có free tier phù hợp trong giai đoạn đầu.
4. Lưu secrets bằng nền tảng deploy/secret manager, không commit vào repository.
5. Cấu hình biến môi trường production, CORS, HTTPS và health check.
6. Thiết lập CI cơ bản: lint/build frontend, test/build backend trước khi deploy.
7. Test smoke trên production: login, tạo job, mở kết quả, quota và lịch sử.

### Tiêu chí hoàn thành

- Người dùng ngoài mạng local truy cập và dùng được luồng chính.
- Deploy lại không làm mất dữ liệu và không cần sửa cấu hình thủ công.
- Có log để điều tra lỗi production.

---

## 8. Phase 5 — Đo lường, cải thiện và quyết định mở rộng

### Mục tiêu

Xác minh người dùng có thực sự học lại và quay lại sản phẩm trước khi đầu tư tính năng trả phí/hạ tầng cao hơn.

### Công việc

1. Đo các sự kiện: tạo analysis, job hoàn thành/thất bại, mở kết quả, lật flashcard, làm quiz, copy nội dung và quay lại sau 7 ngày.
2. Thu thập phản hồi ngắn trong app: học liệu có hữu ích không, lỗi nào, họ muốn xuất file hay học lại bằng flashcard.
3. Theo dõi tỷ lệ thành công, thời gian xử lý, chi phí Gemini/video thành công và tỷ lệ dùng hết quota.
4. Cải thiện prompt/schema từ các lỗi thực tế; thêm evaluation set khi có mẫu video đại diện.
5. Ưu tiên Markdown export, sau đó PDF; chỉ thêm Word nếu phản hồi người dùng chứng minh nhu cầu.
6. Chỉ thiết kế pricing/Pro khi có retention và nhu cầu vượt quota rõ ràng.

### Các chỉ số quyết định

| Chỉ số | Ý nghĩa |
|---|---|
| Tỷ lệ job thành công | Độ tin cậy của Gemini + hệ thống. |
| Thời gian tạo kết quả | Ảnh hưởng trực tiếp activation. |
| Lượt tạo/user | Mức độ sử dụng. |
| Retention 7 ngày | Người dùng có quay lại học tiếp không. |
| Tỷ lệ làm quiz/lật flashcard | Học liệu có được dùng, không chỉ đọc summary. |
| Chi phí mỗi video thành công | Cơ sở để đặt quota và giá Pro. |

---

## 9. Thứ tự bắt tay vào code

1. Hoàn tất Phase 0: cấu hình local và bảo mật secrets.
2. Làm Phase 1 đến khi chạy được từ URL sang JSON learning package.
3. Chạy bộ test feasibility và ghi kết quả.
4. Nếu đạt tiêu chí, làm Phase 2: job, database, trang kết quả và lịch sử.
5. Thêm Phase 3 trước khi public link.
6. Deploy Phase 4 và bắt đầu đo Phase 5.
