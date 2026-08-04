package com.orderledger.service;

import com.orderledger.dao.entity.*;
import com.orderledger.dao.repository.CouponRepository;
import com.orderledger.dao.repository.OrderRepository;
import com.orderledger.dao.repository.UserRepository;
import com.orderledger.dao.specification.OrderSpecification;
import com.orderledger.dto.request.OrderCreateRequest;
import com.orderledger.dto.request.OrderItemRequest;
import com.orderledger.dto.request.OrderSearchFilter;
import com.orderledger.dto.request.OrderStatusUpdateRequest;
import com.orderledger.dto.response.OrderResponse;
import com.orderledger.exception.InsufficientStockException;
import com.orderledger.exception.InvalidCouponException;
import com.orderledger.exception.InvalidStatusTransitionException;
import com.orderledger.exception.ResourceNotFoundException;
import com.orderledger.mapper.OrderMapper;
import com.orderledger.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final CouponRepository couponRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + email));

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .statusHistories(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            ProductEntity product = productService.getProductEntityById(itemReq.productId());

            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new InsufficientStockException("Kifayət qədər stok yoxdur! Məhsul: "
                        + product.getName() + " (Mövcud stok: " + product.getStockQuantity() + ")");
            }

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


        BigDecimal originalTotal = totalAmount;
        BigDecimal discountAmount = BigDecimal.ZERO;
        CouponEntity appliedCoupon = null;

        if (request.couponCode() != null && !request.couponCode().trim().isEmpty()) {
            appliedCoupon = couponRepository.findByCodeAndIsActiveTrue(request.couponCode().trim().toUpperCase())
                    .orElseThrow(() -> new InvalidCouponException("Daxil edilən promo kod keçərsizdir: " + request.couponCode()));

            // Validation Rules
            if (appliedCoupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new InvalidCouponException("Promo kodun istifadə müddəti bitib!");
            }

            if (appliedCoupon.getCurrentUsageCount() >= appliedCoupon.getMaxUsageLimit()) {
                throw new InvalidCouponException("Promo kodun maksimum istifadə limitinə çatılıb!");
            }

            discountAmount = originalTotal
                    .multiply(appliedCoupon.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            totalAmount = originalTotal.subtract(discountAmount);

            appliedCoupon.setCurrentUsageCount(appliedCoupon.getCurrentUsageCount() + 1);
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setCoupon(appliedCoupon);


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
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        OrderEntity order = orderRepository.findByIdAndUserEmailWithDetails(orderId, currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sifariş tapılmadı və ya bu sifarişə baxmaq üçün icazəniz yoxdur! ID: " + orderId
                ));

        return orderMapper.toResponse(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            throw new InvalidStatusTransitionException("Sifariş artıq " + current + " statusundadır.");
        }

        boolean isValid = switch (current) {
            case CREATED -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!isValid) {
            throw new InvalidStatusTransitionException(
                    String.format("Sifariş statusunu %s statusundan %s statusuna dəyişmək mümkün deyil!", current, next)
            );
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersWithFilter(OrderSearchFilter filter, Pageable pageable) {
        Specification<OrderEntity> spec = OrderSpecification.getOrdersByFilter(filter);

        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toResponse);
    }
}