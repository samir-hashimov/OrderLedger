package com.orderledger.dao.repository;

import com.orderledger.dao.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {

    // Sifarişin status dəyişiklik tarixçəsini (Audit Log) yaranma tarixinə görə sıralı gətirir
    List<OrderStatusHistoryEntity> findByOrderIdOrderByTimestampAsc(Long orderId);
}