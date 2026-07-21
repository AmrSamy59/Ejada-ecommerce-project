package com.ejada.ecommerce.mapper;

import com.ejada.ecommerce.dto.order.OrderResponse;
import com.ejada.ecommerce.dto.order.OrderItemResponse;
import com.ejada.ecommerce.entity.Order;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(target = "items", source = "orderItems")
    OrderResponse toDto(Order order);
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toOrderItemDto(com.ejada.ecommerce.entity.OrderItem orderItem);
}
