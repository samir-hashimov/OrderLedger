package com.orderledger.mapper;

import com.orderledger.dao.entity.CouponEntity;
import com.orderledger.dto.request.CouponRequest;
import com.orderledger.dto.response.CouponResponse;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    public CouponEntity toEnt(CouponRequest request) {
        if (request == null) {
            return null;
        }
        return CouponEntity.builder()
                .code(request.code().toUpperCase())
                .discountPercentage(request.discountPercentage())
                .maxUsageLimit(request.maxUsageLimit())
                .expirationDate(request.expirationDate())
                .isActive(true)
                .build();
    }

    public CouponResponse toResponse(CouponEntity entity) {
        if (entity == null) {
            return null;
        }

        return new CouponResponse(
                entity.getId(),
                entity.getCode(),
                entity.getDiscountPercentage(),
                entity.getMaxUsageLimit(),
                entity.getExpirationDate()
        );
    }

}
