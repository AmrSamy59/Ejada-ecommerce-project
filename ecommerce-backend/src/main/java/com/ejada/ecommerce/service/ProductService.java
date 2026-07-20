package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.ProductDto;
import com.ejada.ecommerce.entity.Product;
import com.ejada.ecommerce.exception.ResourceNotFoundException;
import com.ejada.ecommerce.mapper.ProductMapper;
import com.ejada.ecommerce.repository.ProductRepository;
import com.ejada.ecommerce.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDto.Response> getAllProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Product> spec = ProductSpecification.filterProducts(name, minPrice, maxPrice);
        return productRepository.findAll(spec).stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProductDto.Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    public ProductDto.Response createProduct(ProductDto.Request request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductDto.Response updateProduct(Long id, ProductDto.Request request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }
}
