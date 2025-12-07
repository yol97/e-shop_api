package org.greta.eshop_api.persistence.repositories;

import org.greta.eshop_api.persistence.entities.OrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface OrderRepository extends JpaRepository<OrdersEntity, Long> {
    // Comme Product et Customer, on utilisera les méthodes de base de Jparepository
}
