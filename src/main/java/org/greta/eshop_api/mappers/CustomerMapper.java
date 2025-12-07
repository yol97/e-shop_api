package org.greta.eshop_api.mappers;

import org.greta.eshop_api.exposition.dtos.CustomerRequestDTO;
import org.greta.eshop_api.exposition.dtos.CustomerResponseDTO;
import org.greta.eshop_api.persistence.entities.AddressEntity;
import org.greta.eshop_api.persistence.entities.CustomerEntity;

public class CustomerMapper {

    // 👇 Convertit un CustomerRequestDTO en CustomerEntity
    public static CustomerEntity toEntity(CustomerRequestDTO dto, AddressEntity address) {
        CustomerEntity entity = new CustomerEntity();

        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setAddress(address); // on injecte l'adresse récupérée par le service
        return entity;
    }

    // à voir si on fait le updateEntity avec DTO ici
    public static void updateEntity(CustomerEntity entity, CustomerRequestDTO dto, AddressEntity address) {
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setAddress(address);
    }

    // 👇 Convertit un CustomerEntity en CustomerResponseDTO
    public static CustomerResponseDTO toDto(CustomerEntity entity) {
        return new CustomerResponseDTO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAddress() != null ? entity.getAddress().getId() : null
        );
    }

}
