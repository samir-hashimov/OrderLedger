package com.orderledger.service;

import com.orderledger.dao.entity.ProductEntity;
import com.orderledger.dao.repository.ProductRepository;
import com.orderledger.dto.request.ProductCreateRequest;
import com.orderledger.dto.response.ProductResponse;
import com.orderledger.exception.ResourceNotFoundException;
import com.orderledger.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        ProductEntity product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductEntity getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Məhsul tapılmadı! ID: " + id));
    }
}