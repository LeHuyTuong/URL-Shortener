package com.urlshort.service;

import com.urlshort.domain.Base62Encoder;
import com.urlshort.domain.IdGenerator;
import com.urlshort.entity.UrlMapping;
import com.urlshort.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for URL shortening operations.
 * Uses Write-Through caching pattern.
 */
@Service
public class UrlShorteningService {

    private final UrlMappingRepository repository;
    private final CacheService cacheService;
    private final IdGenerator idGenerator;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlShorteningService(
            UrlMappingRepository repository,
            CacheService cacheService,
            @Value("${app.machine-id:1}") int machineId) {
        this.repository = repository;
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
     */
    public String getOriginalUrl(String shortCode) {
        // 1. Check cache first
        return cacheService.get(shortCode)
                .orElseGet(() -> {
                    // 2. Cache miss -> query DB
                    String originalUrl = repository.findById(shortCode)
                            .map(UrlMapping::getOriginalUrl)
                            .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));

                    // 3. Populate cache for next time
                    cacheService.put(shortCode, originalUrl);

                    return originalUrl;
                });
    }
}
