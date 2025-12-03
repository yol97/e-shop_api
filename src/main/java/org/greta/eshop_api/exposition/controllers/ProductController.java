package org.greta.eshop_api.exposition.controllers;

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

    /* @GetMapping
    public List<ProductEntity> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products;
    } */

    @GetMapping     // indique que la méthode répond aux requêtes HTTP GET
    public ResponseEntity<List<ProductEntity>> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    /* @GetMapping("/search")
    public List<ProductEntity> searchProducts(@RequestParam String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    } */

    @GetMapping("/search")
    public List<ProductEntity> searchProducts(@RequestParam String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    /* public List<String> searchproducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "1000") double maxPrice,
            @RequestParam(defaultValue = "asc") String order) {

        List<ProductEntity> products = productRepository.search(category, maxPrice);
        return productRepository.search(category, maxPrice);
    } */

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity product = productRepository.findById(id).orElse(null);

        if (product != null) {
            return ResponseEntity.ok(product); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @PostMapping
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity product) {
        ProductEntity saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductEntity newData) {

        ProductEntity existing = productRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setName(newData.getName());
            existing.setDescription(newData.getDescription());
            /* existing.setImageUrl(newData.getImageUrl());
            existing.setActive(newData.isActive());
            existing.setPrice(newData.getPrice());
            existing.setStock(newData.getStock());
            existing.setDiscount(newData.getDiscount()); */

            ProductEntity updated = productRepository.save(existing);
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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