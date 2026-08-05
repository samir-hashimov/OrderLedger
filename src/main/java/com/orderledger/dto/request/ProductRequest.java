package com.orderledger.dto.request;

import  jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRequest(
        @NotBlank(message = "Məhsul adı boş ola bilməz")
        String name,

        @NotNull(message = "Məhsul qiyməti daxil edilməlidir")
        @DecimalMin(value = "0.01", message = "Qiymət 0-dan böyük olmalıdır")
        BigDecimal price,

        @NotNull(message = "Stok miqdarı qeyd edilməlidir")
        @Min(value = 0, message = "Stok miqdarı mənfi ola bilməz")
        Integer stockQuantity
) {
}