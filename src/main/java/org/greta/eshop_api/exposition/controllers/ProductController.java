package org.greta.eshop_api.exposition.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.greta.eshop_api.domain.services.ProductService;
import org.greta.eshop_api.exposition.dtos.ProductRequestDTO;
import org.greta.eshop_api.exposition.dtos.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    // 👇 On injecte le service
    @Autowired
    private ProductService productService;

    // 👇👇 On met nos méthodes CRUD
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> response = productService.findAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtenir un produit par son ID",
            description = "Retourne les informations détaillées d’un produit existant"
    )
    @ApiResponse(responseCode = "200", description = "Produit trouvé avec succès")
    @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        ProductResponseDTO response = productService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO response = productService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}