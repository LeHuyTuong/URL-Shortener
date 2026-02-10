package com.urlshort.service;

import com.urlshort.domain.Base62Encoder;
import com.urlshort.domain.IdGenerator;
import com.urlshort.dto.AnalyticsResponse;
import com.urlshort.entity.UrlMapping;
import com.urlshort.repository.UrlMappingRepository;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for URL shortening operations.
 * Uses Write-Through caching pattern.
 */

@Service
public class UrlShorteningService {

    private final UrlMappingRepository repository;
    private final com.urlshort.repository.AnalyticsRepository analyticsRepository;
    private final CacheService cacheService;
    private final IdGenerator idGenerator;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlShorteningService(
            UrlMappingRepository repository,
            com.urlshort.repository.AnalyticsRepository analyticsRepository,
            CacheService cacheService,
            @Value("${app.machine-id:1}") int machineId) {
        this.repository = repository;
        this.analyticsRepository = analyticsRepository;
        this.cacheService = cacheService;
        this.idGenerator = new IdGenerator(machineId);
    }

    /**
     * WRITE (shorten):
     * DB.save() → Cache.put() ← Write-Through
     */
    public String shorten(String longUrl) {
        long id = idGenerator.nextId();
        String shortCode = Base62Encoder.encode(id);

        // Save to DB
        UrlMapping mapping = new UrlMapping(shortCode, longUrl);
        repository.save(mapping);

        // Write-Through: Also put in cache
        cacheService.put(shortCode, longUrl);
        return baseUrl + "/" + shortCode;
    }

    /**
     * READ (redirect):
     * Cache.get() → HIT? return
     * → MISS? DB.query() → Cache.put() → return
     * + Async: INCR click count in Redis
     */
    public String getOriginalUrl(String shortCode) {
        // 1. Check cache first
        String originalUrl = cacheService.get(shortCode)
                .orElseGet(() -> {
                    // 2. Cache miss -> query DB
                    String url = repository.findById(shortCode)
                            .map(UrlMapping::getOriginalUrl)
                            .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));

                    // 3. Populate cache for next time
                    cacheService.put(shortCode, url);

                    return url;
                });

        // 4. Async: Increment click count (non-blocking)
        cacheService.incrementClickCount(shortCode);

        return originalUrl;
    }

    /**
     * Get all analytics data with URL mappings.
     */
    public List<AnalyticsResponse> getAllAnalytics() {
        return analyticsRepository.findAll().stream()
                .map(analytics -> {
                    String shortCode = analytics.getShortCode();
                    String shortUrl = baseUrl + "/" + shortCode;
                    String originalUrl = repository.findById(shortCode)
                            .map(UrlMapping::getOriginalUrl)
                            .orElse("N/A");
                    return new AnalyticsResponse(shortCode, shortUrl, originalUrl, analytics.getClickCount());
                })
                .collect(Collectors.toList());
    }
}
