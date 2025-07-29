package com.inaf.authe_service.repository;

import com.inaf.authe_service.Enum.SaleStatus;
import com.inaf.authe_service.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    List<Sale> findByStatus(SaleStatus status);

    List<Sale> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Sale> findByCustomerEmail(String customerEmail);

    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findBySaleDateBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Sale> findByTotalAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                        @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.status = :status")
    Long countByStatus(@Param("status") SaleStatus status);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.status = 'CONFIRMED' AND s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByPeriod(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    Page<Sale> findByCustomerNameContainingIgnoreCaseOrCustomerEmailContainingIgnoreCase(
            String customerName, String customerEmail, Pageable pageable);
}

