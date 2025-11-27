package com.warot_a.shorturlservice.repository;

import com.warot_a.shorturlservice.model.ShortUrlEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrlEntry, Long> {
    Optional<ShortUrlEntry> findByLongUrl(String longUrl);

    Optional<ShortUrlEntry> findByShortCode(String shortCode);
}
