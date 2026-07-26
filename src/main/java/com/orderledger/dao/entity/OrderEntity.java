package com.orderledger.dao.entity;

import com.orderledger.util.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_ledger_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Sifarişçi (User) seçilməlidir")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;

    @NotNull(message = "Ümumi məbləğ hesablanmalıdır")
    @DecimalMin(value = "0.0", message = "Ümumi məbləğ mənfi ola bilməz")
    BigDecimal totalAmount;

    @NotNull(message = "Sifariş statusu boş ola bilməz")
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItemEntity> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderStatusHistoryEntity> statusHistories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    CouponEntity coupon;

    BigDecimal discountAmount;
    @CreationTimestamp
    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = OrderStatus.CREATED;
        }
    }
}