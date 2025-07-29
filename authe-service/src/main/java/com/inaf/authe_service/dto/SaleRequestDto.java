package com.inaf.authe_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record SaleRequestDto(
        @Size(max = 100, message = "Le nom du client ne peut pas dépasser 100 caractères")
        String customerName,

        @Email(message = "Format d'email invalide")
        @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
        String customerEmail,

        @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
        String customerPhone,

        @NotNull(message = "Les articles de vente sont requis")
        @NotEmpty(message = "Au moins un article doit être présent")
        List<SaleItemRequestDto> saleItems,

        @DecimalMin(value = "0.0", inclusive = true, message = "Le montant de la taxe ne peut pas être négatif")
        @Digits(integer = 8, fraction = 2, message = "Format de taxe invalide")
        BigDecimal taxAmount,

        @DecimalMin(value = "0.0", inclusive = true, message = "Le montant de la remise ne peut pas être négatif")
        @Digits(integer = 8, fraction = 2, message = "Format de remise invalide")
        BigDecimal discountAmount,

        String paymentMethod,

        @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
        String notes
) {}
