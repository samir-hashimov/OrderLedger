package com.orderledger.dto.request;

import com.orderledger.util.OrderStatus;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderSearchFilter(
        OrderStatus status,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate,

        BigDecimal minAmount,
        BigDecimal maxAmount
) {}