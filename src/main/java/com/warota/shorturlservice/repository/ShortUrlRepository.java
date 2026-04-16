package com.warota.shorturlservice.repository;

import com.warota.shorturlservice.model.ShortUrlEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortUrlRepository extends MongoRepository<ShortUrlEntry, String> {
    Optional<ShortUrlEntry> findByLongUrl(String longUrl);

    Optional<ShortUrlEntry> findByShortCode(String shortCode);
}
