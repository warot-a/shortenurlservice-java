package com.warota.shorturlservice.controller;

import com.warota.shorturlservice.model.ShortenRequest;
import com.warota.shorturlservice.model.ShortenResponse;
import com.warota.shorturlservice.service.UrlShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
public class UrlShortenerController {
    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        var versions = service.getRuntimeVersions();
        Map<String, Object> response = Map.of(
            "healthy", true,
            "java", versions.get("java"),
            "springBoot", versions.get("springBoot")
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest request) {
        try {
            String shortUrl = service.shortenUrl(request.longUrl());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ShortenResponse(shortUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        String longUrl = service.getLongUrlAndCache(shortCode);

        if (!longUrl.isEmpty()) {
            RedirectView redirectView = new RedirectView(longUrl);
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found");
    }
}
