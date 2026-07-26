package com.orderledger.service;

import com.orderledger.dao.entity.CouponEntity;
import com.orderledger.dao.repository.CouponRepository;
import com.orderledger.dto.request.CouponCreateRequest;
import com.orderledger.mapper.CouponMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    public CouponEntity saveCoupon(CouponCreateRequest request) {
        if (couponRepository.existsByCode(request.code())) {
            throw new RuntimeException("Bu kupon artıq mövcuddur");
        }
        return couponMapper.toEnt(request);
    }

}
