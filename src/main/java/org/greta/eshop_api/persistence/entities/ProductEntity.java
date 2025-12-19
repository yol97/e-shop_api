package org.greta.eshop_api.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.greta.eshop_api.exposition.dtos.ProductRequestDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class ProductEntity extends BaseEntity {

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private double discount = 0.0;

    // création de la table relation "product_category"
    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    private LocalDate promoStart;
    private LocalDate promoEnd;

    public void updateFrom(ProductRequestDTO dto) {
        this.name = dto.name();
        this.description = dto.description();
        this.imageUrl = dto.imageUrl();
        this.isActive = dto.isActive();
        this.price = dto.price();
        this.stock = dto.stock();
        this.discount = dto.discount();
    }

}