package com.urlshort.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Analytics entity for tracking click statistics.
 * PK = shortCode (1-1 with UrlMapping)
 */
@Entity
@Table(name = "analytics")
public class Analytics {

    @Id
    @Column(name = "short_code", length = 10)
    private String shortCode;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    // Default constructor for JPA
    protected Analytics() {
    }

    public Analytics(String shortCode) {
        this.shortCode = shortCode;
        this.clickCount = 0L;
    }

    public void incrementClicks(long count) {
        this.clickCount += count;
        this.lastAccessed = LocalDateTime.now();
    }

    // Getters
    public String getShortCode() {
        return shortCode;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}
