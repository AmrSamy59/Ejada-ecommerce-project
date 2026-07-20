package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.OrderDto;
import com.ejada.ecommerce.entity.Order;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "items", source = "orderItems")
    OrderDto.Response toDto(Order order);
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderDto.OrderItemResponseDto toOrderItemDto(com.ejada.ecommerce.entity.OrderItem orderItem);
}
