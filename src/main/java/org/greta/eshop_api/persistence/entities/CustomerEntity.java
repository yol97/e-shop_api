package org.greta.eshop_api.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter


public class CustomerEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    // Ajout pour challenge 2 Métiers : compte suspendu du client
    @Column(nullable = false)
    private boolean suspended = false;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 👇 ça se passe ici (la jointure avec address)
    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )

    @JoinColumn(name = "address_id")
    private AddressEntity address;

    // 👇 relation 1 -> N avec orders
    @OneToMany(
            mappedBy = "customer",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<OrdersEntity> orders = new ArrayList<>();

}
