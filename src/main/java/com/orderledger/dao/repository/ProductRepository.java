package com.orderledger.dao.repository;

import com.orderledger.dao.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Stoku bitməyən, yəni satışda olan məhsulları siyahılamaq üçün
    List<ProductEntity> findByStockQuantityGreaterThan(Integer stock);
}