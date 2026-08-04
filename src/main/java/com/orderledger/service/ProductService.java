package com.orderledger.service;

import com.orderledger.dao.entity.ProductEntity;
import com.orderledger.dao.entity.UserEntity;
import com.orderledger.dao.repository.ProductRepository;
import com.orderledger.dao.repository.UserRepository;
import com.orderledger.dto.request.ProductCreateRequest;
import com.orderledger.dto.request.ProductUpdateRequest;
import com.orderledger.dto.response.ProductResponse;
import com.orderledger.exception.ProductNotFoundException;
import com.orderledger.exception.ResourceNotFoundException;
import com.orderledger.exception.UserNotFoundException;
import com.orderledger.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, String currentAdminEmail) {

        UserEntity currentAdmin = userRepository.findByEmail(currentAdminEmail)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı: " + currentAdminEmail));

        Optional<ProductEntity> existingProductOpt = productRepository
                .findByNameIgnoreCaseAndPriceAndUserEmail(request.name().trim(), request.price(), currentAdminEmail);

        if (existingProductOpt.isPresent()) {
            ProductEntity existingProduct = existingProductOpt.get();
            existingProduct.setStockQuantity(existingProduct.getStockQuantity() + request.stockQuantity());
            return productMapper.toResponse(productRepository.save(existingProduct));
        }

        ProductEntity newProduct = ProductEntity.builder()
                .name(request.name().trim())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .user(currentAdmin)
                .build();

        return productMapper.toResponse(productRepository.save(newProduct));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(String currentAdminEmail) {
        List<ProductEntity> products = productRepository.findAllByUserEmail(currentAdminEmail);

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductEntity getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Məhsul tapılmadı! ID: " + id));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request, String currentAdminEmail) {
        ProductEntity product = productRepository.findByIdAndUserEmail(id, currentAdminEmail)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Məhsul tapılmadı və ya bu məhsulu yeniləmək üçün icazəniz yoxdur! ID: " + id
                ));

        product.setName(request.name().trim());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        try {
            ProductEntity savedProduct = productRepository.save(product);
            return productMapper.toResponse(savedProduct);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Məhsul başqa bir istifadəçi tərəfindən yenilənib. Yenidən cəhd edin.");
        }
    }

    @Transactional
    public void deleteProduct(Long id,String currentAdminEmail) {
        ProductEntity product = productRepository.findByIdAndUserEmail(id, currentAdminEmail)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Məhsul tapılmadı və ya bu məhsulu silmək üçün icazəniz yoxdur! ID: " + id
                ));

        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("Bu məhsul aktiv sifarişlərdə istifadə olunduğu üçün silinə bilməz!");
        }
    }
}