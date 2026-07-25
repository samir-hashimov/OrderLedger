package com.orderledger.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stockQuantity
) {}