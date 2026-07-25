package com.orderledger.dao.repository;

import com.orderledger.dao.entity.OrderEntity;
import com.orderledger.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Müəyyən bir istifadəçiyə aid bütün sifarişlər
    List<OrderEntity> findByUserId(Long userId);

    // Müəyyən statusda olan sifarişlərin tapılması (məsələn: CREATED olanları tapmaq üçün)
    List<OrderEntity> findByStatus(OrderStatus status);

    // Sifarişi, onun daxilindəki item-ları və həmin item-ların aid olduğu məhsulları 
    // tək bir SQL sorğusu ilə (Fetch Join) gətirərək N+1 probleminin qarşısını alır.
    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithDetails(@Param("orderId") Long orderId);
}