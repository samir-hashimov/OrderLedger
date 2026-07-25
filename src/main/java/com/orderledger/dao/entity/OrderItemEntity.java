package com.orderledger.dao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "orderLedgerOrder_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Builder
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Sifariş mütləq seçilməlidir")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity order;

    @NotNull(message = "Məhsul mütləq seçilməlidir")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity product;

    @NotNull(message = "Məhsul miqdarı qeyd edilməlidir")
    @Min(value = 1, message = "Sifariş edilən məhsul sayı ən azı 1 olmalıdır")
    Integer quantity;

    @NotNull(message = "Alış qiyməti boş ola bilməz")
    @DecimalMin(value = "0.01", message = "Qiymət 0-dan böyük olmalıdır")
    BigDecimal priceAtPurchase;

    @NotNull(message = "Cəm məbləğ boş ola bilməz")
    @DecimalMin(value = "0.01", message = "Cəm məbləğ 0-dan böyük olmalıdır")
    BigDecimal subtotal;
}