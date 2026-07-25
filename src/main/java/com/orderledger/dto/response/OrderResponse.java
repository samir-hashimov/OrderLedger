package com.orderledger.dto.response;

import com.orderledger.util.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        Long userId,
        String username,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderItemResponse> items,
        List<OrderStatusHistoryResponse> statusHistories,
        LocalDateTime createdAt
) {}