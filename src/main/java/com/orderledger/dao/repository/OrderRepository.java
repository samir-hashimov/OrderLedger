package com.orderledger.dao.repository;

import com.orderledger.dao.entity.OrderEntity;
import com.orderledger.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :orderId AND o.user.email = :email")
    Optional<OrderEntity> findByIdAndUserEmailWithDetails(@Param("orderId") Long orderId, @Param("email") String email);

    List<OrderEntity> findByUserId(Long userId);

    List<OrderEntity> findByStatus(OrderStatus status);

    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithDetails(@Param("orderId") Long orderId);
}