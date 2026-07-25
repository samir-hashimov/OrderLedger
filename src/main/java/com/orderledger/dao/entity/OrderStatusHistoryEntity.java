package com.orderledger.dao.entity;


import com.orderledger.util.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orderLedgerOrder_status_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder

public class OrderStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "Sifariş id-si mütləq olmalıdır")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    OrderEntity order;

    @Enumerated(EnumType.STRING)
    OrderStatus previousStatus;

    @NotNull(message = "Yeni status daxil edilməlidir")
    @Enumerated(EnumType.STRING)
    OrderStatus newStatus;

    String reason;

    @CreationTimestamp
    LocalDateTime timestamp;


}