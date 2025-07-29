package com.inaf.authe_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SaleItemRequestDto(
        @NotNull(message = "L'ID du produit est requis")
        @Positive(message = "L'ID du produit doit être positif")
        Long productId,

        @NotNull(message = "La quantité est requise")
        @Positive(message = "La quantité doit être positive")
        Integer quantity,

        @NotNull(message = "Le prix unitaire est requis")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être supérieur à 0")
        @Digits(integer = 8, fraction = 2, message = "Format de prix invalide")
        BigDecimal unitPrice
) {}
