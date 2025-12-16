package org.greta.eshop_api.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="order_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class OrderItemEntity extends BaseEntity {

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unit_price;

    // FK -> orders.id
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrdersEntity order;

    // FK -> product.id
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // clé étrangère order_id

    // clé étrangère product_id

}
