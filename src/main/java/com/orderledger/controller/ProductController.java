package com.orderledger.controller;

import com.orderledger.dto.request.ProductRequest;
import com.orderledger.dto.response.ProductResponse;
import com.orderledger.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Məhsul kataloqunun idarə olunması üzrə API-lər")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Yeni məhsul yaratmaq")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request, Principal principal) {
        String currentAdminEmail = principal.getName();
        ProductResponse response = productService.createProduct(request, currentAdminEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Daxil olmuş adminin özünə aid məhsulların siyahısını gətirmək")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Principal principal) {
        String currentAdminEmail = principal.getName();
        return ResponseEntity.ok(productService.getMyProducts(currentAdminEmail));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Məhsul məlumatlarını yeniləmək (Optimistic Locking )")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            Principal principal
    ) {
        String currentAdminEmail = principal.getName();
        ProductResponse updatedProduct = productService.updateProduct(id, request, currentAdminEmail);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Məhsulu silmək")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id,
                                              Principal principal) {
        String currentAdminEmail = principal.getName();
        productService.deleteProduct(id, currentAdminEmail);
        return ResponseEntity.noContent().build();
    }
}