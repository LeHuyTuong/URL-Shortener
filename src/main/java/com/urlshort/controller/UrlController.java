package com.urlshort.controller;

import com.urlshort.dto.AnalyticsResponse;
import com.urlshort.dto.ShortenRequest;
import com.urlshort.dto.ShortenResponse;
import com.urlshort.service.QrCodeService;
import com.urlshort.service.UrlShorteningService;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for URL operations.
 */
@RestController
public class UrlController {

    private final UrlShorteningService service;

    private final QrCodeService qrCodeService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlController(UrlShorteningService service, QrCodeService qrCodeService) {
        this.service = service;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/api/urls/shorten")
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        String shortUrl = service.shorten(request.getLongUrl());
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

        ShortenResponse response = new ShortenResponse(shortUrl, shortCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    /**
     * Generate QR Code for Short URL
     * GET /api/urls/{shortCode}/qr
     */
    @GetMapping(value = "/api/urls/{shortCode}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrCode(@PathVariable String shortCode) {
        String fullUrl = baseUrl + "/" + shortCode;
        return ResponseEntity.ok(qrCodeService.generateQrCode(fullUrl, 300, 300));
    }

    @GetMapping("/api/analytics")
    public ResponseEntity<List<AnalyticsResponse>> getAnalytics() {
        return ResponseEntity.ok(service.getAllAnalytics());
    }
}
