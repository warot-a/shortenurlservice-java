package com.warot_a.shorturlservice.service;

import com.warot_a.shorturlservice.model.ShortUrlEntry;
import com.warot_a.shorturlservice.repository.ShortUrlRepository;
import com.warot_a.shorturlservice.util.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlShortenerService {
    private final ShortUrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.domain.base-url}")
    private String DOMAIN_BASE_URL = "http://localhost:8080";

    public UrlShortenerService(ShortUrlRepository repository, RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        
    }

    @Transactional
    public String shortenUrl(String longUrl) throws Exception {
        var normalizedUrl = longUrl.trim().replaceAll("/$", "");

        // Lookup from cache
        var shortCodeFromCache = redisTemplate.opsForValue().get(getLongUrlKey(normalizedUrl));
        if (shortCodeFromCache != null) {
            return "%s/%s".formatted(DOMAIN_BASE_URL, shortCodeFromCache);
        }

        // Lookup from database
        var existingEntryOpt = repository.findByLongUrl(normalizedUrl);
        if (existingEntryOpt.isPresent()) {
            var existingEntry = existingEntryOpt.get();

            // Save to cache
            redisTemplate.opsForValue().set(getLongUrlKey(normalizedUrl), existingEntry.getShortCode(), 7, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(getShortCodeKey(existingEntry.getShortCode()), normalizedUrl, 7, TimeUnit.DAYS);

            return "%s/%s".formatted(DOMAIN_BASE_URL, existingEntry.getShortCode());
        }

        // Generate new short code
        var newEntry = new ShortUrlEntry();
        newEntry.setLongUrl(longUrl);
        newEntry = repository.save(newEntry);

        var newShortCode = Base62Encoder.encode(newEntry.getId());
        newEntry.setShortCode(newShortCode);
        repository.save(newEntry);

        redisTemplate.opsForValue().set(getLongUrlKey(normalizedUrl), newShortCode, 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(getShortCodeKey(newShortCode), normalizedUrl, 7, TimeUnit.DAYS);

        return "%s/%s".formatted(DOMAIN_BASE_URL, newShortCode);
    }

    public String getLongUrlAndCache(String shortCode) {
        String longUrl = redisTemplate.opsForValue().get(getShortCodeKey(shortCode));
        if (longUrl != null) {
            return longUrl;
        }

        var entryOpt = repository.findByShortCode(shortCode);
        if (entryOpt.isPresent()) {
            String foundLongUrl = entryOpt.get().getLongUrl();
            redisTemplate.opsForValue().set(getShortCodeKey(shortCode), foundLongUrl, 7, TimeUnit.DAYS);
            return foundLongUrl;
        }
        return null;
    }

    private String getShortCodeKey(String shortCode) {
        return "short:%s".formatted(shortCode);
    }

    private String getLongUrlKey(String longUrl) {
        return "long:%s".formatted(longUrl);
    }
}
