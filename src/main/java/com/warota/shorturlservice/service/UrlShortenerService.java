package com.warota.shorturlservice.service;

import com.warota.shorturlservice.exception.ShortCodeGenerationException;
import com.warota.shorturlservice.model.ShortUrlEntry;
import com.warota.shorturlservice.repository.ShortUrlRepository;
import com.warota.shorturlservice.util.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
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
    public String shortenUrl(String longUrl) throws ShortCodeGenerationException {
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

        // Generate new short code with collision retry
        String newShortCode;
        int maxRetries = 10;
        int attempts = 0;
        
        do {
            newShortCode = ShortCodeGenerator.generate();
            attempts++;
            
            if (attempts > maxRetries) {
                throw new ShortCodeGenerationException("Failed to generate unique short code after " + maxRetries + " attempts");
            }
        } while (repository.findByShortCode(newShortCode).isPresent());
        
        var newEntry = new ShortUrlEntry();
        newEntry.setLongUrl(normalizedUrl);
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

    public Map<String, String> getRuntimeVersions() {
        var javaVersion = System.getProperty("java.version");
        var springBootVersion = SpringBootVersion.getVersion();
        if (javaVersion == null) {
            javaVersion = "unknown";
        }
        return java.util.Map.of("java", javaVersion, "springBoot", springBootVersion);
    }

    private String getShortCodeKey(String shortCode) {
        return "short:%s".formatted(shortCode);
    }

    private String getLongUrlKey(String longUrl) {
        return "long:%s".formatted(longUrl);
    }
}
