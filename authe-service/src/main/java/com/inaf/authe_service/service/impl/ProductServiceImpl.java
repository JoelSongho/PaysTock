package com.inaf.authe_service.service.impl;

import com.inaf.authe_service.dto.ProductRequestDto;
import com.inaf.authe_service.dto.ProductResponseDto;
import com.inaf.authe_service.dto.SaleResponseDto;
import com.inaf.authe_service.entity.Product;
import com.inaf.authe_service.repository.ProductRepository;
import com.inaf.authe_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        if (productRepository.existsByNameIgnoreCase(requestDto.name())) {
            throw new RuntimeException("Product with name '" + requestDto.name() + "' already exists");
        }

        Product product = new Product(
                requestDto.name(),
                requestDto.description(),
                requestDto.price(),
                requestDto.quantity(),
                requestDto.category()
        );

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }



    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!product.getName().equalsIgnoreCase(requestDto.name()) &&
                productRepository.existsByNameIgnoreCase(requestDto.name())) {
            throw new RuntimeException("Product with name '" + requestDto.name() + "' already exists");
        }

        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setQuantity(requestDto.quantity());
        product.setCategory(requestDto.category());

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCase(searchTerm)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProductsPaginated(String searchTerm, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        searchTerm, searchTerm, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }

        product.setQuantity(newQuantity);
        productRepository.save(product);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}