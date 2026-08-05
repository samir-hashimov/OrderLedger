package com.orderledger.controller;

import com.orderledger.dto.request.OrderCreateRequest;
import com.orderledger.dto.request.OrderSearchFilter;
import com.orderledger.dto.request.OrderStatusUpdateRequest;
import com.orderledger.dto.response.OrderResponse;
import com.orderledger.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Sifariş yaradılması, status keçidləri və history API-ləri")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Yeni sifariş yerləşdirmək (Stock yoxlaması və Transactional kontrol ilə)")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request, Principal principal) {
        String currentAdminEmail = principal.getName();
        OrderResponse response = orderService.createOrder(request, currentAdminEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Sifarişin statusunu yeniləmək və immutable audit log-a yazmaq")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Sifarişi ID-yə görə detalları və status tarixçəsi ilə gətirmək")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id
                                                                    ,Principal principal) {
        String currentAdminEmail = principal.getName();

        OrderResponse response = orderService.getOrderById(id,currentAdminEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Sifarişləri filterləmək və pagination ilə getirmək")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @ModelAttribute OrderSearchFilter filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<OrderResponse> orders = orderService.getOrdersWithFilter(filter, pageable);
        return ResponseEntity.ok(orders);
    }
}