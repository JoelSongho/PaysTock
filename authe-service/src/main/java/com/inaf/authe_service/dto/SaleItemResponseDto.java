package com.inaf.authe_service.dto;

import java.math.BigDecimal;

public record SaleItemResponseDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
