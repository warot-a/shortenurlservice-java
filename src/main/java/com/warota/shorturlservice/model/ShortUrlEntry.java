package com.warota.shorturlservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlEntry {

    @Id
    private String id;

    @Indexed(unique = true)
    private String shortCode;

    @Indexed(unique = true)
    private String longUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
}
