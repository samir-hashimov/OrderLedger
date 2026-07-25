package com.orderledger.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderItemRequest(
        @NotNull(message = "Məhsul ID-si boş ola bilməz")
        Long productId,

        @NotNull(message = "Məhsul miqdarı daxil edilməlidir")
        @Min(value = 1, message = "Ən azı 1 ədəd məhsul sifariş edilməlidir")
        Integer quantity
) {}