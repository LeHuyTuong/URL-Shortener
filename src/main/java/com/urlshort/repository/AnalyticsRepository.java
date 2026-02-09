package com.urlshort.repository;

import com.urlshort.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Analytics entity.
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, String> {
    // JpaRepository provides: save, findById, existsById, etc.
}
