package com.orderledger.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_ledger_coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String code;

    @Column(nullable = false)
    BigDecimal discountPercentage;

    @Column(nullable = false)
    Integer maxUsageLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @Builder.Default
    @Column(nullable = false)
    Integer currentUsageCount = 0;

    @Column(nullable = false)
    LocalDateTime expirationDate;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;
}