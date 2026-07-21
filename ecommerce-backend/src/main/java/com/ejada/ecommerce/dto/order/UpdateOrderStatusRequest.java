package com.ejada.ecommerce.dto.order;

import com.ejada.ecommerce.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status cannot be null")
    private OrderStatus status;
}
