package com.urlshort.scheduler;

import com.urlshort.entity.Analytics;
import com.urlshort.repository.AnalyticsRepository;
import com.urlshort.service.CacheService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * TODO: Anh implement Scheduled job này
 * 
 * Scheduled job to sync click stats from Redis to DB.
 * Runs every 5 minutes.
 * 
 * Flow:
 * 1. HGETALL stats -> Map<shortCode, clickCount>
 * 2. For each entry: UPDATE or INSERT into DB
 * 3. HDEL stats {shortCode} (clear Redis after sync)
 */
@Component
@Slf4j
public class AnalyticsSyncScheduler {

    private final CacheService cacheService;
    private final AnalyticsRepository analyticsRepository;

    public AnalyticsSyncScheduler(CacheService cacheService, AnalyticsRepository analyticsRepository) {
        this.cacheService = cacheService;
        this.analyticsRepository = analyticsRepository;
    }

    /**
     * TODO: Sync Redis stats to DB every 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000ms
    @Transactional
    public void syncClickStats() {
        Map<Object, Object> stats = cacheService.getAllStats();
        if (stats.isEmpty()) {
            return;
        }
        log.info("[AnalyticsSyncScheduler] Syncing {} stats to DB...", stats.size());
        stats.forEach((shortCode, clickCount) -> {
            String code = (String) shortCode;
            Long count = Long.parseLong((String) clickCount);
            Analytics analytics = analyticsRepository.findById(code).orElse(new Analytics(code));

            analytics.incrementClicks(count);
            analyticsRepository.save(analytics);
            cacheService.deleteStats(code);
            log.info("Synced " + count + " clicks for " + code);
        });
        log.info("[AnalyticsSyncScheduler] Sync Completed!");
    }
}
