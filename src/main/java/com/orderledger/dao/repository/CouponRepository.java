package com.orderledger.dao.repository;

import com.orderledger.dao.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);
    boolean existsByCodeAndUserEmail(String code, String email);

    Optional<CouponEntity> findByIdAndUserEmail(Long id, String email);

    List<CouponEntity> findAllByUserEmail(String email);
}