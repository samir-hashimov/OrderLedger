package com.orderledger.dao.specification;

import com.orderledger.dao.entity.OrderEntity;
import com.orderledger.dto.request.OrderSearchFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<OrderEntity> getOrdersByFilter(OrderSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.endDate()));
            }

            if (filter.minAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), filter.minAmount()));
            }

            if (filter.maxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), filter.maxAmount()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}