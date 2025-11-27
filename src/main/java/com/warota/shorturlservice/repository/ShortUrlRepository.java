package com.warota.shorturlservice.repository;

import com.warota.shorturlservice.model.ShortUrlEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntry, Long> {
    Optional<ShortUrlEntry> findByLongUrl(String longUrl);

    Optional<ShortUrlEntry> findByShortCode(String shortCode);
}
