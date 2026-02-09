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

### 4. Caching Strategy (Phase 4)

**Problem:** Read:Write ratio = 100:1, DB latency = 700ms

**Solution:** Redis cache with Write-Through + Cache-Aside patterns

| Pattern | When | How |
|---------|------|-----|
| **Write-Through** | Shorten URL | Save to DB → Update cache |
| **Cache-Aside** | Redirect | Check cache → If miss, query DB → Populate cache |

**Performance:**
```
Before: 700ms (DB disk I/O)
After:  15ms (Redis RAM)
Improvement: 46x faster
```

**TTL:** 7 days (604,800 seconds) - Balance between hit rate and memory usage

**Redis Key:** `url:{shortCode}` → `originalUrl`

---

### 5. Analytics Architecture (Phase 5)

**Challenge:** High-concurrency click counting without DB row locks

**Solution:** Redis Hash INCR + Scheduled Batch Sync

```
Click → Redis HINCRBY stats {shortCode} 1  (Atomic, <1ms)
         ↓
    [Every 5 minutes]
         ↓
Scheduled Job → Batch sync to DB → Clear Redis
```

**Why Redis Hash over separate keys?**

| Approach | Pros | Cons | Chosen |
|----------|------|------|--------|
| Separate keys (`stats:{code}`) | Individual TTL | Slow SCAN for batch sync | ❌ |
| **Hash (`stats` field `{code}`)** | **Fast HGETALL** | No per-field TTL | ✅ |

**Why 5-minute sync interval?**
- ✅ Reduces DB write load (batch vs per-click)
- ✅ Acceptable data freshness for analytics
- ⚠️ Max 5 minutes data loss if Redis crashes (mitigated by AOF)

**Redis Persistence: AOF (Append Only File)**

```yaml
command: redis-server --appendonly yes
```

| RDB (Snapshot) | AOF (Log) | Choice |
|----------------|-----------|--------|
| Snapshot every X min | Log every write | **AOF** |
| Max X min data loss | Max 1 sec data loss | ✅ |
| Small file | Larger file (~2-3x) | Acceptable |

**Why AOF?** Click analytics requires data integrity. Losing 5 minutes of clicks from RDB snapshot is unacceptable. AOF with `everysec` sync ensures max 1-second data loss.

**Race Condition Handling:**

Redis INCR is atomic - multiple gateways can increment simultaneously without locks:
```
Gateway A: HINCRBY stats abc123 1  ─┐
Gateway B: HINCRBY stats abc123 1  ─┼→ Redis serializes → Final count = 2
Gateway C: HINCRBY stats abc123 1  ─┘
```

No DB row locks, no deadlocks, no lost updates.

---

## Project Structure
```
src/main/java/com/urlshort/
├── UrlShortenerApplication.java
├── domain/
│   ├── Base62Encoder.java
│   └── IdGenerator.java
├── entity/
│   ├── UrlMapping.java
│   └── Analytics.java
├── repository/
│   ├── UrlMappingRepository.java
│   └── AnalyticsRepository.java
├── service/
│   ├── UrlShorteningService.java
│   └── CacheService.java
├── scheduler/
│   └── AnalyticsSyncScheduler.java
├── controller/UrlController.java
└── dto/
    ├── ShortenRequest.java
    └── ShortenResponse.java
```

## Phase Checklist
- [x] Phase 1: System Design Document
- [x] Phase 2: Core Domain (Base62, IdGenerator)
- [x] Phase 3: Shorten URL API
- [x] Phase 4: Redirect + Redis Cache (700ms → 15ms)
- [x] Phase 5: Analytics (Redis INCR + Scheduled Sync)
