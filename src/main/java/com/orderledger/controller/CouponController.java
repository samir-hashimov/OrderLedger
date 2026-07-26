package com.orderledger.controller;

import com.orderledger.dao.entity.CouponEntity;
import com.orderledger.dao.repository.CouponRepository;
import com.orderledger.dto.request.CouponCreateRequest;
import com.orderledger.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon Management", description = "Dinamik Promo kodların yaradılması və idarəsi")
public class CouponController {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "Yeni promo kod/kupon yaratmaq")
    public ResponseEntity<CouponEntity> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.saveCoupon(request));
    }
}