package com.warota.shorturlservice.model;

import com.warota.shorturlservice.validation.ValidUrl;

public record ShortenRequest(@ValidUrl(message = "Please provide a valid URL") String longUrl) {
}
