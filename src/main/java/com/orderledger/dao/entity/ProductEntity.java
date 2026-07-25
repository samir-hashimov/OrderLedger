package com.orderledger.dao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "orderLedgerProducts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Məhsul adı boş ola bilməz")
    String name;

    @NotNull(message = "Məhsul qiyməti daxil edilməlidir")
    @DecimalMin(value = "0.01", message = "Qiymət 0-dan böyük olmalıdır")
    BigDecimal price;

    @NotNull(message = "Stok miqdarı qeyd edilməlidir")
    @Min(value = 0, message = "Stok miqdarı mənfi ola bilməz")
    Integer stockQuantity;

//    @Version // Optimistic locking
//     Long version;
}