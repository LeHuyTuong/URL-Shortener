package com.urlshort.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache service for Redis operations.
 * Pattern: Write-Through (cache on write, read from cache first)
 */
@Service
public class CacheService {

    private static final String KEY_PREFIX = "url:";

    private final StringRedisTemplate redisTemplate; // Spring wrapper cho Redis client. Chuyên xử lý String key-value
    private final Duration ttl; // bao lâu thì key tự xóa

    public CacheService(
            StringRedisTemplate redisTemplate,
            @Value("${app.cache.ttl:604800}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    /**
     * Get original URL from cache.
     * 
     * @return Optional.empty() if not in cache
     */
    public Optional<String> get(String shortCode) {
        // opsForValue() redis command cho string type (GET,SET)
        // get ~ redis-cli GET url:abc123
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + shortCode);
        return Optional.ofNullable(value);
    }

    /**
     * Put URL mapping into cache (Write-Through).
     */
    public void put(String shortCode, String originalUrl) {
        // SET url:abc123 "https://google.com" EX 604800
        redisTemplate.opsForValue().set(KEY_PREFIX + shortCode, originalUrl, ttl);
    }

    /**
     * Delete from cache (if needed for updates/deletes).
     */
    public void evict(String shortCode) {
        redisTemplate.delete(KEY_PREFIX + shortCode);
    }

    // ==================== Analytics (Redis Hash) ====================
    private static final String STATS_HASH_KEY = "stats";

    /**
     * Increment click count for a shortCode (Atomic INCR).
     * - Dùng redisTemplate.opsForHash() để thao tác với Redis Hash
     * - Method cần dùng: increment(hashKey, field, delta)
     * - Tương đương Redis CLI: HINCRBY stats {shortCode} 1
     */
    public void incrementClickCount(String shortCode) {
        redisTemplate.opsForHash().increment(STATS_HASH_KEY, shortCode, 1);
    }

    /**
     * Get all click stats from Redis Hash.
     * Returns Map<shortCode, clickCount>
     * - Dùng redisTemplate.opsForHash().entries(hashKey)
     * - Tương đương Redis CLI: HGETALL stats
     * - Return type: java.util.Map<Object, Object>
     */
    public java.util.Map<Object, Object> getAllStats() {
        return redisTemplate.opsForHash().entries(STATS_HASH_KEY);
    }

    /**
     * Delete a stat entry after syncing to DB.
     * - Dùng redisTemplate.opsForHash().delete(hashKey, field)
     * - Tương đương Redis CLI: HDEL stats {shortCode}
     */
    public void deleteStats(String shortCode) {
        redisTemplate.opsForHash().delete(STATS_HASH_KEY, shortCode);
    }
}
