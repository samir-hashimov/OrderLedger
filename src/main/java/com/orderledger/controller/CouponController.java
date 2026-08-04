package com.orderledger.controller;

import com.orderledger.dto.request.CouponRequest;
import com.orderledger.dto.response.CouponResponse;
import com.orderledger.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon Controller", description = "Kuponların idarə edilməsi üçün endpoint-lər")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "Yeni kupon yaratmaq")
    public ResponseEntity<CouponResponse> saveCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse response = couponService.saveCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Daxil olmuş adminin yalnız özünə aid kuponlarını gətirmək")
    public ResponseEntity<List<CouponResponse>> getMyCoupons() {
        return ResponseEntity.ok(couponService.getMyCoupons());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Adminin öz kuponunu yeniləməsi")
    public ResponseEntity<CouponResponse> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request
    ) {
        CouponResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Adminin öz kuponunu silməsi")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}