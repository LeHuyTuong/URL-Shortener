package com.urlshort.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the URL mapping.
 * PK = shortCode (String) as per Option A design decision.
 */
@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    @Column(name = "short_code", length = 10)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    protected UrlMapping() {
    }

    public UrlMapping(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
