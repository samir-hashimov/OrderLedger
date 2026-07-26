package com.orderledger.dao.repository;

import com.orderledger.dao.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCodeAndIsActiveTrue(String code);
    boolean existsByCode(String code);
}