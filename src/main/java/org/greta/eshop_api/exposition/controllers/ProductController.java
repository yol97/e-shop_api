package org.greta.eshop_api.exposition.controllers;
import jakarta.validation.Valid;
import org.greta.eshop_api.exceptions.ResourceNotFoundException;
import org.greta.eshop_api.exposition.dtos.ProductRequestDTO;
import org.greta.eshop_api.exposition.dtos.ProductResponseDTO;
import org.greta.eshop_api.mappers.ProductMapper;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController     // 👈 la classe expose des données au format JSON
@RequestMapping("/products")    // définit la base commune de tous les endpoints de ce controller
public class ProductController {

    @Autowired      // injecte automatiquement une instance du ProductRepository (Spring s’en charge)
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> dtos = productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String keyword) {
        List<ProductResponseDTO> dtos = productRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(ProductMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /* public List<String> searchproducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "1000") double maxPrice,
            @RequestParam(defaultValue = "asc") String order) {

        List<ProductEntity> products = productRepository.search(category, maxPrice);
        return productRepository.search(category, maxPrice);
    } */

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit avec l'ID : " + id + " n'existe pas."
                ));

        ProductResponseDTO response = ProductMapper.toDto(product);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(    // 👉 à la place de ResponseEntity<ProductEntity>
            @Valid @RequestBody ProductRequestDTO request       // @Valid des contraintes de validation
    ) {
        ProductEntity entity = ProductMapper.toEntity(request);
        ProductEntity saved = productRepository.save(entity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO newData) {

        ProductEntity existing = productRepository.findById(id).orElse(null);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // 🔁 Mise à jour manuelle de l’entité avec les données du DTO
        existing.setName(newData.name());
        existing.setDescription(newData.description());
        existing.setImageUrl(newData.imageUrl());
        existing.setIsActive(newData.isActive());   // Lombok -> setIsActive(...)
        existing.setPrice(newData.price());
        existing.setStock(newData.stock());
        existing.setDiscount(newData.discount());

        // 💾 Sauvegarde
        ProductEntity updated = productRepository.save(existing);

        // 🎁 Réponse en DTO
        ProductResponseDTO responseDto = ProductMapper.toDto(updated);
        return ResponseEntity.ok(responseDto);
    }

    // 👇 un DELETE n'a pas de corps de réponse, donc pas de DTO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }

    // Delete All
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllProducts() {
        productRepository.deleteAll();
        return ResponseEntity.noContent().build();  // 204 No content
    }

}