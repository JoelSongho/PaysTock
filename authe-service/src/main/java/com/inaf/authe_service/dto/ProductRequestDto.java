package com.inaf.authe_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequestDto(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,

        @Size(max = 50, message = "Category cannot exceed 50 characters")
        String category
) {}
