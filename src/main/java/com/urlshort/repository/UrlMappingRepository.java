package com.urlshort.repository;

import com.urlshort.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UrlMapping entity.
 * PK is String (shortCode).
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {

    // Custom query
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
