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
}
