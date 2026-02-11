# URL Shortener - Learning Project

> A personal project built to learn Spring Boot, React, and system design fundamentals

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue)](https://react.dev/)

## Why I Built This

Tôi muốn hiểu cách các dịch vụ như Bitly hoạt động phía sau, đặc biệt là:
- Làm sao generate short codes duy nhất?
- Caching hoạt động như thế nào?
- Làm sao track analytics mà không làm chậm hệ thống?
---

## Features

- Shorten long URLs to 6-character codes
- QR code generation for each short URL
- Click tracking with simple analytics
- Responsive React dashboard

**Not Implemented** (yet):
- User authentication
- Custom short codes
- Link expiration

---

## Tech Stack

**Backend**: Spring Boot, H2 Database, Redis  
**Frontend**: React + Vite  
**Learning Focus**: REST API design, Caching strategies, React state management

---

## Quick Start

### Prerequisites
```bash
Java 17, Node.js 18, Redis
```

### Run Locally
```bash
# Backend
./mvnw spring-boot:run

# Frontend (new terminal)
cd frontend && npm install && npm run dev
```

Visit `http://localhost:5173`

---

## What I Learned

### 1. Base62 Encoding
**Challenge**: Database auto-increment IDs (1, 2, 3...) tạo URLs dễ đoán  
**Solution**: Encode IDs sang Base62 (a-z, A-Z, 0-9) → `1` = `b`, `1000` = `g8`
```java
// Simplified version
public String encode(long num) {
    StringBuilder result = new StringBuilder();
    while (num > 0) {
        result.insert(0, BASE62.charAt((int)(num % 62)));
        num /= 62;
    }
    return result.toString();
}
```

**Lesson**: Mã hóa đơn giản có thể tăng security và UX!

### 2. Redis Caching

**Problem**: Mỗi lần click vào short URL phải query database → chậm  
**What I tried**:
1. Cache toàn bộ URLs → Tốn RAM
2. Không cache → Mỗi redirect đều query DB
3. **Cache-Aside pattern** với TTL 7 ngày

**Code**:
```java
// Check cache first
String longUrl = redisCache.get(shortCode);
if (longUrl == null) {
    longUrl = database.findByShortCode(shortCode);
    redisCache.set(shortCode, longUrl, 7_DAYS);
}
return redirect(longUrl);
```

**Result**: Giảm database queries ~80% trong testing

### 3. CORS Hell với QR Code Download

**The Bug**: 
```javascript
// Không work vì cross-origin
<a href={qrUrl} download>Download</a>
```

**The Fix**: Fetch blob + create object URL
```javascript
const response = await fetch(qrUrl);
const blob = await response.blob();
const url = URL.createObjectURL(blob);
// ... trigger download
```

**Lesson**: Frontend-Backend separation cần hiểu CORS và binary data handling!

---

## Challenges & Solutions

| Problem | My Solution | What I Learned |
|---------|-------------|----------------|
| Concurrent clicks tăng counter sai | Dùng Redis INCR (atomic) | Database transactions != atomic operations |
| QR code bị cache cũ | Thêm timestamp vào URL | Browser caching cần query params |
| H2 data mất khi restart | Chấp nhận cho demo, note để migrate PostgreSQL | In-memory DB phù hợp cho prototype |

---

## Project Structure
```
├── src/main/java/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # JPA data access
│   └── config/         # Redis, CORS setup
├── frontend/src/
│   ├── components/     # React components
│   └── App.jsx
└── README.md
```

---

## API Documentation

### Shorten URL
```bash
POST /api/urls/shorten
Content-Type: application/json

{
  "url": "https://example.com/very/long/url"
}

# Response
{
  "shortCode": "a3x9Kp",
  "shortUrl": "http://localhost:8080/a3x9Kp",
  "originalUrl": "https://example.com/very/long/url"
}
```

### Redirect
```bash
GET /{shortCode}
# Returns 302 redirect
```

---

## Current Limitations

Đây là **learning project**, chưa production-ready:

- Không có authentication (ai cũng tạo được URL)
- Không validate URLs (có thể shorten malicious links)
- H2 in-memory → data mất khi restart
- Chưa test với high traffic
- Analytics đơn giản (chỉ count clicks)

**Next Steps**:
- [ ] Add Spring Security
- [ ] Migrate to PostgreSQL
- [ ] Implement URL validation
- [ ] Add rate limiting

---

## If I Rebuild This

**Things I'd do differently**:
1. Dùng UUID thay vì auto-increment (đơn giản hơn Base62 encoding)
2. Design database schema trước khi code
3. Write tests từ đầu (tôi viết tests sau nên phải refactor nhiều)
4. Document API với Swagger/OpenAPI

**Things I'd keep**:
- Redis caching strategy
- React component structure
- Separation of concerns (Controller-Service-Repository)

---

## Resources I Used

- [System Design Primer - URL Shortener](https://github.com/donnemartin/system-design-primer)
- [Spring Boot Official Docs](https://spring.io/guides)
- [Redis Caching Patterns](https://redis.io/docs/manual/patterns/)

---

## Contact

Nếu bạn có feedback hoặc câu hỏi về implementation, feel free to reach out!

**Email**: [lehuytuong2005@gmail.com]  
**LinkedIn**: [Hi Profile](https://www.linkedin.com/in/lehuytuong/)  
**Portfolio**: [My Porfolio](https://lehuytuong.vercel.app/)

---

*Built with and lots of StackOverflow searches*
