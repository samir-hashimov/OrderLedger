package com.orderledger.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record CouponCreateRequest(
        @NotBlank(message = "Kupon kodu boş ola bilməz")
        String code,

        @NotNull(message = "Endirim faizi daxil edilməlidir")
        @DecimalMin(value = "1.00", message = "Endirim minimum 1% olmalıdır")
        @DecimalMax(value = "100.00", message = "Endirim maksimum 100% ola bilər")
        BigDecimal discountPercentage,

        @NotNull(message = "Maksimum istifadə limiti daxil edilməlidir")
        @Min(value = 1, message = "Limit ən azı 1 olmalıdır")
        Integer maxUsageLimit,

        @NotNull(message = "Son istifadə tarixi daxil edilməlidir")
        @Future(message = "Son istifadə tarixi gələcəkdə olmalıdır")
        LocalDateTime expirationDate
) {
}