package org.greta.eshop_api.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AddressEntity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String street;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 10)
    private String zipCode;

    @Column(nullable = false, length = 80)
    private String country;

    // 👇 ça se passe ici (la jointure avec customer)
    @OneToOne(mappedBy = "address")
    private CustomerEntity customer;

}
