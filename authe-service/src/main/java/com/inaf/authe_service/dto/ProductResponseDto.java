package com.inaf.authe_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}