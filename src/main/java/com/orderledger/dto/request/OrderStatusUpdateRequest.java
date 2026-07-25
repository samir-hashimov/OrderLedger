package com.orderledger.dto.request;

import com.orderledger.util.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderStatusUpdateRequest(
        @NotNull(message = "Yeni status boş ola bilməz")
        OrderStatus newStatus,

        String reason
) {}