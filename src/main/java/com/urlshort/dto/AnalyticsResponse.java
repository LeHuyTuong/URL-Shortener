package com.urlshort.dto;

public class AnalyticsResponse {

    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private Long clickCount;

    public AnalyticsResponse() {
    }


    public AnalyticsResponse(String shortCode, String shortUrl, String originalUrl, Long clickCount) {
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.clickCount = clickCount;
    }


    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}
