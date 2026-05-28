package com.musiconline.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VinylRequest(
        @NotBlank String title,
        @NotBlank String artist,
        @NotBlank String type,        // ALBUM, EP, SINGLE
        String genre,
        String label,
        LocalDate releaseDate,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        String condition,
        String description,
        String imageUrl,
        Boolean available
) {}
