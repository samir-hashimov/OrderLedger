package com.orderledger.dto.response;

import com.orderledger.util.OrderStatus;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record OrderStatusHistoryResponse(
        Long id,
        OrderStatus previousStatus,
        OrderStatus newStatus,
        String reason,
        LocalDateTime timestamp
) {}