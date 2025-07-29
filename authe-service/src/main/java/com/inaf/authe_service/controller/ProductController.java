package com.inaf.authe_service.controller;

import com.inaf.authe_service.dto.ProductRequestDto;
import com.inaf.authe_service.dto.ProductResponseDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import com.inaf.authe_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        try {
            ProductResponseDto product = productService.createProduct(requestDto);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Récupérer toutes les ventes sans pagination",
            description = "Renvoie la liste complète des ventes disponibles.")
    @ApiResponse(responseCode = "200", description = "Liste des ventes récupérée avec succès")
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllSales() {
        List<ProductResponseDto> product = productService.getAll();
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        try {
            ProductResponseDto product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponseDto> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto) {
        try {
            ProductResponseDto product = productService.updateProduct(id, requestDto);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponseDto> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String term) {
        List<ProductResponseDto> products = productService.searchProducts(term);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<ProductResponseDto>> searchProductsPaginated(
            @RequestParam String term,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponseDto> products = productService.searchProductsPaginated(term, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductResponseDto> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        try {
            productService.updateStock(id, quantity);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
