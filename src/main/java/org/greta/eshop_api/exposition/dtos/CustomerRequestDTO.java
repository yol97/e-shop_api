package org.greta.eshop_api.exposition.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDTO(
        String firstName,
        String lastName,
        Long addressId   // peut être null si pas d’adresse au début
) {}
