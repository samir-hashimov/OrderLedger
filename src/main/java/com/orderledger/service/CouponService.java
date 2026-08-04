package com.orderledger.service;

import com.orderledger.dao.entity.CouponEntity;
import com.orderledger.dao.entity.UserEntity;
import com.orderledger.dao.repository.CouponRepository;
import com.orderledger.dao.repository.UserRepository;
import com.orderledger.dto.request.CouponRequest;
import com.orderledger.dto.response.CouponResponse;
import com.orderledger.exception.ResourceNotFoundException;
import com.orderledger.exception.UserNotFoundException;
import com.orderledger.mapper.CouponMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponMapper couponMapper;

    @Transactional
    public CouponResponse saveCoupon(CouponRequest request) {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity currentAdmin = userRepository.findByEmail(currentAdminEmail)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı: " + currentAdminEmail));

        if (couponRepository.existsByCodeAndUserEmail(request.code().trim().toUpperCase(), currentAdminEmail)) {
            throw new RuntimeException("Bu kupon kodu artıq sizin tərəfinizdən yaradılıb!");
        }

        CouponEntity coupon = couponMapper.toEnt(request);
        coupon.setCode(request.code().trim().toUpperCase());
        coupon.setUser(currentAdmin);

        CouponEntity savedCoupon = couponRepository.save(coupon);
        return couponMapper.toResponse(savedCoupon);
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CouponEntity coupon = couponRepository.findByIdAndUserEmail(id, currentAdminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Kupon tapılmadı və ya yeniləmək üçün icazəniz yoxdur! ID: " + id));

        String newCode = request.code().trim().toUpperCase();
        if (!coupon.getCode().equalsIgnoreCase(newCode) && couponRepository.existsByCodeAndUserEmail(newCode, currentAdminEmail)) {
            throw new RuntimeException("Bu kupon kodu artıq başqa kuponunuzda istifadə olunur!");
        }

        coupon.setCode(newCode);
        coupon.setDiscountPercentage(request.discountPercentage());
        coupon.setMaxUsageLimit(request.maxUsageLimit());
        coupon.setExpirationDate(request.expirationDate());

        CouponEntity updatedCoupon = couponRepository.save(coupon);
        return couponMapper.toResponse(updatedCoupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        CouponEntity coupon = couponRepository.findByIdAndUserEmail(id, currentAdminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Kupon tapılmadı və ya silmək üçün icazəniz yoxdur! ID: " + id));

        couponRepository.delete(coupon);
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> getMyCoupons() {
        String currentAdminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return couponRepository.findAllByUserEmail(currentAdminEmail)
                .stream()
                .map(couponMapper::toResponse)
                .toList();
    }
}