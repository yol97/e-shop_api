package org.greta.eshop_api.exposition.controllers;

import jakarta.validation.Valid;
import org.greta.eshop_api.domain.services.OrderService;
import org.greta.eshop_api.exposition.dtos.OrderCreateRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 🔹 GET /orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> dtos = orderService.findAll();
        return ResponseEntity.ok(dtos);
    }

    // 🔹 GET /orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO dto = orderService.findById(id);
        return ResponseEntity.ok(dto);
    }

    // 🔹 POST /orders
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO request
    ) {
        OrderResponseDTO dto = orderService.create(request.order(), request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // 🔹 PUT /orders/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderCreateRequestDTO request
    ) {
        OrderResponseDTO dto = orderService.update(id, request.order(), request.items());
        return ResponseEntity.ok(dto);
    }

    // 🔹 DELETE /orders/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

