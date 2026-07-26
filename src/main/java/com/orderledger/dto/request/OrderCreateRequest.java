package com.orderledger.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateRequest(
        @NotEmpty(message = "Sifarişdə ən azı bir məhsul olmalıdır")
        List<OrderItemRequest> items,
        String couponCode
) {}