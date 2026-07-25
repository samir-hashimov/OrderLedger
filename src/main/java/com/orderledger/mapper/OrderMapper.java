package com.orderledger.mapper;

import com.orderledger.dao.entity.OrderEntity;
import com.orderledger.dao.entity.OrderItemEntity;
import com.orderledger.dao.entity.OrderStatusHistoryEntity;
import com.orderledger.dto.response.OrderItemResponse;
import com.orderledger.dto.response.OrderResponse;
import com.orderledger.dto.response.OrderStatusHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(OrderEntity entity) {
        List<OrderItemResponse> itemResponses = entity.getItems() != null ?
                entity.getItems().stream().map(this::toItemResponse).toList() : Collections.emptyList();

        List<OrderStatusHistoryResponse> historyResponses = entity.getStatusHistories() != null ?
                entity.getStatusHistories().stream().map(this::toHistoryResponse).toList() : Collections.emptyList();

        return OrderResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .username(entity.getUser().getUsername())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .items(itemResponses)
                .statusHistories(historyResponses)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public OrderItemResponse toItemResponse(OrderItemEntity entity) {
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .quantity(entity.getQuantity())
                .priceAtPurchase(entity.getPriceAtPurchase())
                .subtotal(entity.getSubtotal())
                .build();
    }

    public OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistoryEntity entity) {
        return OrderStatusHistoryResponse.builder()
                .id(entity.getId())
                .previousStatus(entity.getPreviousStatus())
                .newStatus(entity.getNewStatus())
                .reason(entity.getReason())
                .timestamp(entity.getTimestamp())
                .build();
    }
}