package com.warota.shorturlservice.model;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenRequest(
    @NotBlank(message = "URL cannot be blank")
    @URL(message = "Invalid URL format")
    String longUrl
) {
}
