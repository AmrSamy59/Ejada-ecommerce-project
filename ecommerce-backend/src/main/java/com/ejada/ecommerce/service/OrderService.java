package com.ejada.ecommerce.service;

import com.ejada.ecommerce.dto.OrderDto;
import com.ejada.ecommerce.entity.Order;
import com.ejada.ecommerce.entity.OrderItem;
import com.ejada.ecommerce.entity.OrderStatus;
import com.ejada.ecommerce.entity.Product;
import com.ejada.ecommerce.entity.User;
import com.ejada.ecommerce.exception.ResourceNotFoundException;
import com.ejada.ecommerce.repository.OrderRepository;
import com.ejada.ecommerce.repository.ProductRepository;
import com.ejada.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderDto.Response placeOrder(Long userId, OrderDto.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDto.OrderItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            
            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
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

        return mapToResponse(savedOrder);
    }

    public List<OrderDto.Response> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderDto.Response> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderDto.Response mapToResponse(Order order) {
        OrderDto.Response response = new OrderDto.Response();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        
        List<OrderDto.OrderItemResponseDto> items = order.getOrderItems().stream().map(item -> {
            OrderDto.OrderItemResponseDto itemDto = new OrderDto.OrderItemResponseDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            return itemDto;
        }).collect(Collectors.toList());
        
        response.setItems(items);
        return response;
    }
}
