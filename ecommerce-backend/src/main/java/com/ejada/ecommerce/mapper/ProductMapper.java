package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.product.ProductRequest;
import com.ejada.ecommerce.dto.product.ProductResponse;
import com.ejada.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toDto(Product product);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Product toEntity(ProductRequest request);
}
