package com.musiconline.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record VinylResponse(
        Long id,
        String title,
        String artist,
        String type,
        String genre,
        String label,
        LocalDate releaseDate,
        BigDecimal price,
        String condition,
        String description,
        String imageUrl,
        boolean available,
        String sellerEmail,
        String sellerUsername,
        Instant createdAt,
        Instant updatedAt
) {}
