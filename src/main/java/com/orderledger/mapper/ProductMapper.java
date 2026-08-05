package com.orderledger.mapper;

import com.orderledger.dao.entity.ProductEntity;
import com.orderledger.dto.request.ProductRequest;
import com.orderledger.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductEntity toEntity(ProductRequest request) {
        return ProductEntity.builder()
                .name(request.name())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();
    }

    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .build();
    }
}