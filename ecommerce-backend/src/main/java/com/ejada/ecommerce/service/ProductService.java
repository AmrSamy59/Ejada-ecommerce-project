package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.product.ProductRequest;
import com.ejada.ecommerce.dto.product.ProductResponse;
import com.ejada.ecommerce.dto.common.PageResponse;
import com.ejada.ecommerce.entity.Product;
import com.ejada.ecommerce.entity.ProductStatus;
import com.ejada.ecommerce.exception.ResourceNotFoundException;
import com.ejada.ecommerce.exception.ErrorCode;
import com.ejada.ecommerce.mapper.ProductMapper;
import com.ejada.ecommerce.repository.ProductRepository;
import com.ejada.ecommerce.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductResponse> getAllProducts(String name, BigDecimal minPrice, BigDecimal maxPrice, int pageNo, int pageSize) {
        Specification<Product> spec = ProductSpecification.filterProducts(name, minPrice, maxPrice);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> products = productRepository.findAll(spec, pageable);
        
        List<ProductResponse> content = products.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
                
        return new PageResponse<>(content, products.getNumber(), products.getSize(), products.getTotalElements(), products.getTotalPages(), products.isLast());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id, ErrorCode.PRODUCT_NOT_FOUND));
        
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ResourceNotFoundException("Product with id: " + id + " is deleted." , ErrorCode.PRODUCT_DELETED);
        }
        
        return productMapper.toDto(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id, ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ResourceNotFoundException("Product with id: " + id + " is deleted." , ErrorCode.PRODUCT_DELETED);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id, ErrorCode.PRODUCT_NOT_FOUND));
        
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new ResourceNotFoundException("Product with id: " + id + " is already deleted." , ErrorCode.PRODUCT_DELETED);
        }
        
        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }
}
