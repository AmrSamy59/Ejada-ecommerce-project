package com.ejada.ecommerce.dto;

import com.ejada.ecommerce.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data
    public static class Request {
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        private List<OrderItemDto> items;
    }

    @Data
    public static class OrderItemDto {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    public static class Response {
        private Long id;
        private LocalDateTime orderDate;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private List<OrderItemResponseDto> items;
    }

    @Data
    public static class OrderItemResponseDto {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
    }
}
