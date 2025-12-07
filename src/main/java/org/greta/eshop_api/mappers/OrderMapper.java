package org.greta.eshop_api.mappers;

import org.greta.eshop_api.exposition.dtos.OrderRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderResponseDTO;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.OrdersEntity;

public class OrderMapper {

    // 👇 DTO => Entity (Create)
    public static OrdersEntity toEntity(OrderRequestDTO dto, CustomerEntity customer) {
        OrdersEntity entity = new OrdersEntity();

        entity.setStatus(dto.status());
        entity.setCustomer(customer);   // customer chargé dans le service
        // createdAt / updateAt gérés par @PrePersist dans OrdersEntity
        // items rest vide au début

        return entity;
    }

    // 👉 DTO → Entity (UPDATE sur une entité existante)
    public static void updateEntity(OrdersEntity entity, OrderRequestDTO dto, CustomerEntity customer) {
        entity.setStatus(dto.status());
        entity.setCustomer(customer);
        // on ne touche pas aux dates : @PreUpdate s'en chargera
        // on ne touche pas aux items ici
    }

    // 👉 Entity → DTO (RESPONSE)
    public static OrderResponseDTO toDto(OrdersEntity entity) {
        return new OrderResponseDTO(
                entity.getId(),
                entity.getStatus(),
                entity.getCustomer() != null ? entity.getCustomer().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
