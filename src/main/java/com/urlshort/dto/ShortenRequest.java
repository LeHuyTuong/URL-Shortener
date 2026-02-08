package com.urlshort.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for URL shortening.
 */
public class ShortenRequest {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Invalid URL format")
    private String longUrl;

    // Default constructor for Jackson
    public ShortenRequest() {
    }

    public ShortenRequest(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
