package com.orderledger.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtPurchase,
        BigDecimal subtotal
) {}