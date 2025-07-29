package com.inaf.authe_service.service;

import com.inaf.authe_service.dto.ProductRequestDto;
import com.inaf.authe_service.dto.ProductResponseDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    ProductResponseDto getProductById(Long id);

    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    List<ProductResponseDto> getAll();


    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);

    void deleteProduct(Long id);

    List<ProductResponseDto> getProductsByCategory(String category);

    List<ProductResponseDto> searchProducts(String searchTerm);

    Page<ProductResponseDto> searchProductsPaginated(String searchTerm, Pageable pageable);

    List<ProductResponseDto> getLowStockProducts(Integer threshold);

    void updateStock(Long id, Integer newQuantity);
}
