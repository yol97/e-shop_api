package org.greta.eshop_api.exposition.dtos;

import org.greta.eshop_api.persistence.entities.AddressEntity;

public record CustomerResponseDTO(
        Long id,
        String firstName,
        String lastName,
        Long addressId
) {}
