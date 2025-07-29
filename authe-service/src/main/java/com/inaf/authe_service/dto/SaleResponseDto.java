package com.inaf.authe_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponseDto(
        Long id,
        String saleNumber,
        String customerName,
        String customerEmail,
        String customerPhone,
        String status,
        BigDecimal totalAmount,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        String paymentMethod,
        LocalDateTime saleDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<SaleItemResponseDto> saleItems,
        String notes
) {}
