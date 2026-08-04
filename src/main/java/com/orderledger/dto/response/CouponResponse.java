package com.orderledger.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String code,
        BigDecimal discountPercentage,
        Integer maxUsageLimit,
        LocalDateTime expirationDate
) {}