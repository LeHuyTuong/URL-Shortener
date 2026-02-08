# URL Shortener

A URL Shortener service built for learning System Design concepts.

## Quick Start
```bash
# Build
mvn clean compile

# Run tests
mvn test

# Run application
mvn spring-boot:run
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/urls/shorten` | Create short URL |
| GET | `/{shortCode}` | Redirect to original URL |

**Example:**
```bash
curl -X POST http://localhost:8080/api/urls/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl": "https://www.google.com"}'
```

---

## Design Decisions & Trade-offs

### 1. ID Generation Strategy

| Approach | Short Code Length | Distributed? | Use Case |
|----------|-------------------|--------------|----------|
| Sequence only | ~4-6 chars | ❌ Single machine | Demo/Dev |
| MachineId + Seq | ~9-11 chars | ✅ Multi machine | Production |
| Snowflake (full) | ~11+ chars | ✅ + Timestamp | High scale |

**Current:** `MachineId (16 bits) + Sequence (48 bits)` → ~9 chars

**Why?** Ensures uniqueness across distributed instances without coordination.

### 2. Base62 Encoding

```
Charset: a-z, A-Z, 0-9 (62 characters)
```
- ✅ URL-safe (no special chars like `+`, `/`)
- ✅ Case-sensitive = more combinations
- ✅ Human-readable (no confusing chars)

### 3. Primary Key Choice

**Option A (chosen):** `shortCode` (String) as PK
- ✅ Simple lookup
- ⚠️ Random B-Tree insert (acceptable at 1M writes/day)

**Option B:** `id` (Long) as PK + indexed `shortCode`
- ✅ Sequential insert (better write perf)
- ⚠️ Extra index storage

### 4. Read-Heavy Optimization (Phase 4)

```
Read:Write ratio = 100:1
Solution: Redis cache with TTL
```

---

## Project Structure
```
src/main/java/com/urlshort/
├── UrlShortenerApplication.java
├── domain/
│   ├── Base62Encoder.java
│   └── IdGenerator.java
├── entity/UrlMapping.java
├── repository/UrlMappingRepository.java
├── service/UrlShorteningService.java
├── controller/UrlController.java
└── dto/
    ├── ShortenRequest.java
    └── ShortenResponse.java
```

## Phase Checklist
- [x] Phase 1: System Design Document
- [x] Phase 2: Core Domain (Base62, IdGenerator)
- [x] Phase 3: Shorten URL API
- [ ] Phase 4: Redirect + Redis Cache
- [ ] Phase 5: Analytics
