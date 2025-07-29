package com.inaf.authe_service.service;

import com.inaf.authe_service.Enum.SaleStatus;
import com.inaf.authe_service.dto.SaleRequestDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDto createSale(SaleRequestDto requestDto);

    SaleResponseDto getSaleById(Long id);

    SaleResponseDto getSaleByNumber(String saleNumber);

    Page<SaleResponseDto> getAllSales(Pageable pageable);
    List<SaleResponseDto> getAll();

    SaleResponseDto updateSale(Long id, SaleRequestDto requestDto);

    void deleteSale(Long id);

    SaleResponseDto confirmSale(Long id);

    SaleResponseDto cancelSale(Long id);

    SaleResponseDto updateSaleStatus(Long id, SaleStatus status);

    List<SaleResponseDto> getSalesByStatus(SaleStatus status);

    List<SaleResponseDto> getSalesByCustomer(String customerName);

    List<SaleResponseDto> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Page<SaleResponseDto> searchSales(String searchTerm, Pageable pageable);

    BigDecimal getTotalRevenueByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    Long getSalesCountByStatus(SaleStatus status);
}
