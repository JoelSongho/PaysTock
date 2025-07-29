package com.inaf.authe_service.service.impl;


import com.inaf.authe_service.Enum.PaymentMethod;
import com.inaf.authe_service.Enum.SaleStatus;
import com.inaf.authe_service.dto.SaleItemRequestDto;
import com.inaf.authe_service.dto.SaleItemResponseDto;
import com.inaf.authe_service.dto.SaleRequestDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import com.inaf.authe_service.entity.Product;
import com.inaf.authe_service.entity.Sale;
import com.inaf.authe_service.entity.SaleItem;
import com.inaf.authe_service.repository.ProductRepository;
import com.inaf.authe_service.repository.SaleRepository;
import com.inaf.authe_service.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Override
    public SaleResponseDto createSale(SaleRequestDto requestDto) {
        // Générer un numéro de vente unique
        String saleNumber = generateSaleNumber();

        Sale sale = new Sale(saleNumber, requestDto.customerName(),
                requestDto.customerEmail(), requestDto.customerPhone());

        // Définir les montants
        sale.setTaxAmount(requestDto.taxAmount() != null ? requestDto.taxAmount() : BigDecimal.ZERO);
        sale.setDiscountAmount(requestDto.discountAmount() != null ? requestDto.discountAmount() : BigDecimal.ZERO);
        sale.setNotes(requestDto.notes());

        // Définir le mode de paiement
        if (requestDto.paymentMethod() != null) {
            sale.setPaymentMethod(PaymentMethod.valueOf(requestDto.paymentMethod().toUpperCase()));
        }

        // Ajouter les articles de vente
        for (SaleItemRequestDto itemDto : requestDto.saleItems()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID: " + itemDto.productId()));

            // Vérifier le stock disponible
            if (product.getQuantity() < itemDto.quantity()) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + product.getName() +
                        ". Stock disponible: " + product.getQuantity());
            }

            SaleItem saleItem = new SaleItem(product, itemDto.quantity(), itemDto.unitPrice());
            sale.addSaleItem(saleItem);
        }

        Sale savedSale = saleRepository.save(sale);
        return mapToResponseDto(savedSale);
    }

    @Override
    public SaleResponseDto confirmSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new RuntimeException("Seules les ventes en attente peuvent être confirmées");
        }

        // Déduire les quantités du stock
        for (SaleItem item : sale.getSaleItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getQuantity() - item.getQuantity();

            if (newQuantity < 0) {
                throw new RuntimeException("Stock insuffisant pour confirmer la vente. Produit: " + product.getName());
            }

            product.setQuantity(newQuantity);
            productRepository.save(product);
        }

        sale.setStatus(SaleStatus.CONFIRMED);
        Sale savedSale = saleRepository.save(sale);
        return mapToResponseDto(savedSale);
    }

    @Override
    public SaleResponseDto cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));

        if (sale.getStatus() == SaleStatus.DELIVERED) {
            throw new RuntimeException("Une vente livrée ne peut pas être annulée");
        }

        // Si la vente était confirmée, restaurer le stock
        if (sale.getStatus() == SaleStatus.CONFIRMED || sale.getStatus() == SaleStatus.SHIPPED) {
            for (SaleItem item : sale.getSaleItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        sale.setStatus(SaleStatus.CANCELLED);
        Sale savedSale = saleRepository.save(sale);
        return mapToResponseDto(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));
        return mapToResponseDto(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto getSaleByNumber(String saleNumber) {
        Sale sale = saleRepository.findBySaleNumber(saleNumber)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec le numéro: " + saleNumber));
        return mapToResponseDto(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable).map(this::mapToResponseDto);
    }



    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getAll() {
        return saleRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }



    @Override
    public SaleResponseDto updateSale(Long id, SaleRequestDto requestDto) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));

        if (sale.getStatus() == SaleStatus.CONFIRMED || sale.getStatus() == SaleStatus.DELIVERED) {
            throw new RuntimeException("Une vente confirmée ou livrée ne peut pas être modifiée");
        }

        // Mise à jour des informations client
        sale.setCustomerName(requestDto.customerName());
        sale.setCustomerEmail(requestDto.customerEmail());
        sale.setCustomerPhone(requestDto.customerPhone());
        sale.setTaxAmount(requestDto.taxAmount() != null ? requestDto.taxAmount() : BigDecimal.ZERO);
        sale.setDiscountAmount(requestDto.discountAmount() != null ? requestDto.discountAmount() : BigDecimal.ZERO);
        sale.setNotes(requestDto.notes());

        if (requestDto.paymentMethod() != null) {
            sale.setPaymentMethod(PaymentMethod.valueOf(requestDto.paymentMethod().toUpperCase()));
        }

        // Recalculer le total
        sale.calculateTotalAmount();

        Sale savedSale = saleRepository.save(sale);
        return mapToResponseDto(savedSale);
    }

    @Override
    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));

        if (sale.getStatus() == SaleStatus.CONFIRMED || sale.getStatus() == SaleStatus.DELIVERED) {
            throw new RuntimeException("Une vente confirmée ou livrée ne peut pas être supprimée");
        }

        saleRepository.deleteById(id);
    }

    @Override
    public SaleResponseDto updateSaleStatus(Long id, SaleStatus status) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée avec l'ID: " + id));

        sale.setStatus(status);
        Sale savedSale = saleRepository.save(sale);
        return mapToResponseDto(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getSalesByStatus(SaleStatus status) {
        return saleRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getSalesByCustomer(String customerName) {
        return saleRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> searchSales(String searchTerm, Pageable pageable) {
        return saleRepository.findByCustomerNameContainingIgnoreCaseOrCustomerEmailContainingIgnoreCase(
                        searchTerm, searchTerm, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = saleRepository.getTotalRevenueByPeriod(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSalesCountByStatus(SaleStatus status) {
        return saleRepository.countByStatus(status);
    }

    private String generateSaleNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "SALE-" + timestamp;
    }

    private SaleResponseDto mapToResponseDto(Sale sale) {
        List<SaleItemResponseDto> saleItemDtos = sale.getSaleItems().stream()
                .map(this::mapSaleItemToResponseDto)
                .collect(Collectors.toList());

        return new SaleResponseDto(
                sale.getId(),
                sale.getSaleNumber(),
                sale.getCustomerName(),
                sale.getCustomerEmail(),
                sale.getCustomerPhone(),
                sale.getStatus().name(),
                sale.getTotalAmount(),
                sale.getTaxAmount(),
                sale.getDiscountAmount(),
                sale.getPaymentMethod() != null ? sale.getPaymentMethod().name() : null,
                sale.getSaleDate(),
                sale.getCreatedAt(),
                sale.getUpdatedAt(),
                saleItemDtos,
                sale.getNotes()
        );
    }

    private SaleItemResponseDto mapSaleItemToResponseDto(SaleItem saleItem) {
        return new SaleItemResponseDto(
                saleItem.getId(),
                saleItem.getProduct().getId(),
                saleItem.getProduct().getName(),
                saleItem.getQuantity(),
                saleItem.getUnitPrice(),
                saleItem.getSubtotal()
        );
    }
}
