package com.urlshort.dto;

/**
 * Response DTO for URL shortening.
 */
public class ShortenResponse {

    private String shortUrl;
    private String shortCode;

    public ShortenResponse() {
    }

    public ShortenResponse(String shortUrl, String shortCode) {
        this.shortUrl = shortUrl;
        this.shortCode = shortCode;
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
}
