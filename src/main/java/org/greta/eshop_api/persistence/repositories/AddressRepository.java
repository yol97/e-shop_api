package org.greta.eshop_api.persistence.repositories;

import org.greta.eshop_api.persistence.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

// 👇 le 1er type est l’entité manipulée, le 2ème est le type de sa clé primaire (id de type Long)
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

}
