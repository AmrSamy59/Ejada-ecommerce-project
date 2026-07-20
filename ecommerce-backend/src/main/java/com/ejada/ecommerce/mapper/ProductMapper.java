package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.ProductDto;
import com.ejada.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto.Response toDto(Product product);
    
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto.Request request);
}
