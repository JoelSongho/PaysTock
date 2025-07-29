package com.inaf.authe_service.controller;


import com.inaf.authe_service.Enum.SaleStatus;
import com.inaf.authe_service.dto.SaleRequestDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import com.inaf.authe_service.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle vente")
    public ResponseEntity<SaleResponseDto> createSale(@RequestBody SaleRequestDto requestDto) {
        try {
            SaleResponseDto sale = saleService.createSale(requestDto);
            return new ResponseEntity<>(sale, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer toutes les ventes sans pagination",
            description = "Renvoie la liste complète des ventes disponibles.")
    @ApiResponse(responseCode = "200", description = "Liste des ventes récupérée avec succès")
    @GetMapping("/all")
    public ResponseEntity<List<SaleResponseDto>> getAllSales() {
        List<SaleResponseDto> sales = saleService.getAll();
        return ResponseEntity.ok(sales);
    }

    @Operation(summary = "Récupérer toutes les ventes avec pagination",
            description = "Renvoie une page de ventes, selon les paramètres Pageable fournis.")
    @ApiResponse(responseCode = "200", description = "Page des ventes récupérée avec succès")
    @GetMapping("/paged")
    public ResponseEntity<Page<SaleResponseDto>> getAllSalesPaged(Pageable pageable) {
        Page<SaleResponseDto> sales = saleService.getAllSales(pageable);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une vente par son identifiant")
    public ResponseEntity<SaleResponseDto> getSaleById(@PathVariable Long id) {
        try {
            SaleResponseDto sale = saleService.getSaleById(id);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/number/{saleNumber}")
    @Operation(summary = "Récupérer une vente par son numéro")
    public ResponseEntity<SaleResponseDto> getSaleByNumber(@PathVariable String saleNumber) {
        try {
            SaleResponseDto sale = saleService.getSaleByNumber(saleNumber);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une vente")
    public ResponseEntity<SaleResponseDto> updateSale(@PathVariable Long id,
                                                      @RequestBody SaleRequestDto requestDto) {
        try {
            SaleResponseDto sale = saleService.updateSale(id, requestDto);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une vente")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            saleService.deleteSale(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une vente")
    public ResponseEntity<SaleResponseDto> confirmSale(@PathVariable Long id) {
        try {
            SaleResponseDto sale = saleService.confirmSale(id);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une vente")
    public ResponseEntity<SaleResponseDto> cancelSale(@PathVariable Long id) {
        try {
            SaleResponseDto sale = saleService.cancelSale(id);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une vente")
    public ResponseEntity<SaleResponseDto> updateSaleStatus(@PathVariable Long id,
                                                            @RequestParam SaleStatus status) {
        try {
            SaleResponseDto sale = saleService.updateSaleStatus(id, status);
            return new ResponseEntity<>(sale, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les ventes par statut")
    public ResponseEntity<List<SaleResponseDto>> getSalesByStatus(@PathVariable SaleStatus status) {
        List<SaleResponseDto> sales = saleService.getSalesByStatus(status);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerName}")
    @Operation(summary = "Récupérer les ventes par nom du client")
    public ResponseEntity<List<SaleResponseDto>> getSalesByCustomer(@PathVariable String customerName) {
        List<SaleResponseDto> sales = saleService.getSalesByCustomer(customerName);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Récupérer les ventes dans une plage de dates")
    public ResponseEntity<List<SaleResponseDto>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<SaleResponseDto> sales = saleService.getSalesByDateRange(startDate, endDate);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des ventes avec pagination")
    public ResponseEntity<Page<SaleResponseDto>> searchSales(@RequestParam String searchTerm,
                                                             Pageable pageable) {
        Page<SaleResponseDto> sales = saleService.searchSales(searchTerm, pageable);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Calculer le chiffre d'affaires total sur une période")
    public ResponseEntity<BigDecimal> getTotalRevenueByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal revenue = saleService.getTotalRevenueByPeriod(startDate, endDate);
        return new ResponseEntity<>(revenue, HttpStatus.OK);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Compter le nombre de ventes par statut")
    public ResponseEntity<Long> getSalesCountByStatus(@PathVariable SaleStatus status) {
        Long count = saleService.getSalesCountByStatus(status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
