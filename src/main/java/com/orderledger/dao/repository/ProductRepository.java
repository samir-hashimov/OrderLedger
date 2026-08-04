package com.orderledger.dao.repository;

import com.orderledger.dao.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByUserEmail(String email);

    Optional<ProductEntity> findByNameIgnoreCaseAndPriceAndUserEmail(String name, BigDecimal price, String email);

    Optional<ProductEntity> findByIdAndUserEmail(Long id, String email);

    List<ProductEntity> findByStockQuantityGreaterThan(Integer stock);
}