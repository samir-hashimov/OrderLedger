package com.orderledger.mapper;

import com.orderledger.dao.entity.CouponEntity;
import com.orderledger.dto.request.CouponCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    public CouponEntity toEnt(CouponCreateRequest request) {
        return CouponEntity.builder()
                .code(request.code().toUpperCase())
                .discountPercentage(request.discountPercentage())
                .maxUsageLimit(request.maxUsageLimit())
                .expirationDate(request.expirationDate())
                .isActive(true)
                .build();
    }
}
