package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.order.OrderRequest;
import com.ejada.ecommerce.dto.order.OrderItemRequest;
import com.ejada.ecommerce.dto.order.OrderResponse;
import com.ejada.ecommerce.dto.common.PageResponse;
import com.ejada.ecommerce.entity.Order;
import com.ejada.ecommerce.entity.OrderItem;
import com.ejada.ecommerce.entity.OrderStatus;
import com.ejada.ecommerce.entity.Product;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.exception.ResourceNotFoundException;
import com.ejada.ecommerce.exception.BusinessRuleException;
import com.ejada.ecommerce.exception.ErrorCode;
import com.ejada.ecommerce.mapper.OrderMapper;
import com.ejada.ecommerce.repository.OrderRepository;
import com.ejada.ecommerce.repository.ProductRepository;
import com.ejada.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import com.ejada.ecommerce.specification.OrderSpecification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCode.USER_NOT_FOUND));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId(), ErrorCode.PRODUCT_NOT_FOUND));
            
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new BusinessRuleException("Not enough stock for product: " + product.getName(), ErrorCode.INSUFFICIENT_STOCK);
            }

            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .order(order)
                    .quantity(itemDto.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(itemDto.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public PageResponse<OrderResponse> getOrders(Long userId, String userName, LocalDateTime dateFrom, LocalDateTime dateTo, OrderStatus orderStatus, int pageNo, int pageSize) {
        Specification<Order> spec = OrderSpecification.filterOrders(dateFrom, dateTo, userName, orderStatus, userId);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        
        List<OrderResponse> content = orders.getContent().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
                
        return new PageResponse<>(content, orders.getNumber(), orders.getSize(), orders.getTotalElements(), orders.getTotalPages(), orders.isLast());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId, ErrorCode.ORDER_NOT_FOUND));
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Order is already cancelled and cannot be modified.", ErrorCode.ORDER_ALREADY_CANCELLED);
        }

        if (status == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }
}
