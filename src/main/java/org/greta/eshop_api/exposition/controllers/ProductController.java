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

}