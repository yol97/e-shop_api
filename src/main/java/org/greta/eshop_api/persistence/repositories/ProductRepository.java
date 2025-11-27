package org.greta.eshop_api.persistence.repositories;

import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository             // 👈 indique à Spring que cette interface gère l’accès aux données.

// 👇 le 1er type est l’entité manipulée, le 2ème est le type de sa clé primaire (id de type Long)
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 👇 SELECT * FROM product WHERE LOWER(name) LIKE LOWER('%keyword%');
    List<ProductEntity> findByNameContainingIgnoreCase(String keyword);

    /* SELECT p FROM ProductEntity p
    WHERE (:category IS NULL OR LOWER(p.category) = LOWER(:category))
    AND p.price <= :maxPrice
           */
    /* List<ProductEntity> search(
            @Param("category") String category,
            @Param("maxPrice") double maxPrice
    ); */

}
