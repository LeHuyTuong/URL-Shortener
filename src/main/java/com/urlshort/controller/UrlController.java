package com.urlshort.controller;

import com.urlshort.dto.ShortenRequest;
import com.urlshort.dto.ShortenResponse;
import com.urlshort.service.UrlShorteningService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST Controller for URL operations.
 */
@RestController
public class UrlController {

    private final UrlShorteningService service;

    public UrlController(UrlShorteningService service) {
        this.service = service;
    }

    /**
     * Management API: Shorten URL
     * POST /api/urls/shorten
     */
    @PostMapping("/api/urls/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortUrl = service.shorten(request.getLongUrl());
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        ShortenResponse response = new ShortenResponse(shortUrl, shortCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Public API: Redirect
     * GET /{shortCode}
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
