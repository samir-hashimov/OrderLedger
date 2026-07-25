package com.orderledger.service;

import com.orderledger.dao.entity.*;
import com.orderledger.dao.repository.OrderRepository;
import com.orderledger.dao.repository.UserRepository;
import com.orderledger.dto.request.OrderCreateRequest;
import com.orderledger.dto.request.OrderItemRequest;
import com.orderledger.dto.request.OrderStatusUpdateRequest;
import com.orderledger.dto.response.OrderResponse;
import com.orderledger.exception.InsufficientStockException;
import com.orderledger.exception.InvalidStatusTransitionException;
import com.orderledger.exception.ResourceNotFoundException;
import com.orderledger.mapper.OrderMapper;
import com.orderledger.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + username));

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .statusHistories(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. Məhsul stokunun yoxlanması və azaldılması
        for (OrderItemRequest itemReq : request.items()) {
            ProductEntity product = productService.getProductEntityById(itemReq.productId());

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new InsufficientStockException("Kifayət qədər stok yoxdur! Məhsul: "
                        + product.getName() + " (Mövcud stok: " + product.getStockQuantity() + ")");
            }

            // Stokun yenilənməsi (@Version vasitəsilə Optimistic Lock qorunur)
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.quantity())
                    .priceAtPurchase(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 3. İlk Status Log-unun yazılması
        OrderStatusHistoryEntity initialHistory = OrderStatusHistoryEntity.builder()
                .order(order)
                .previousStatus(null)
                .newStatus(OrderStatus.CREATED)
                .reason("Sifariş uğurla yaradıldı")
                .build();

        order.getStatusHistories().add(initialHistory);

        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        OrderEntity order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sifariş tapılmadı! ID: " + orderId));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.newStatus();
        
        validateStatusTransition(currentStatus, newStatus);

        order.setStatus(newStatus);

        OrderStatusHistoryEntity history = OrderStatusHistoryEntity.builder()
                .order(order)
                .previousStatus(currentStatus)
                .newStatus(newStatus)
                .reason(request.reason())
                .build();

        order.getStatusHistories().add(history);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sifariş tapılmadı! ID: " + orderId));
        return orderMapper.toResponse(order);
    }

    // STATE MACHINE LOGIC (Keçid Qaydaları)
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            throw new InvalidStatusTransitionException("Sifariş artıq " + current + " statusundadır.");
        }

        if (current == OrderStatus.SHIPPED || current == OrderStatus.COMPLETED) {
            if (next == OrderStatus.CANCELLED) {
                throw new InvalidStatusTransitionException("SHIPPED və ya COMPLETED olunmuş sifariş ləğv edilə bilməz!");
            }
        }

        if (current == OrderStatus.CANCELLED || current == OrderStatus.COMPLETED) {
            throw new InvalidStatusTransitionException(current + " statusunda olan sifarişin statusu yenidən dəyişdirilə bilməz.");
        }
    }
}