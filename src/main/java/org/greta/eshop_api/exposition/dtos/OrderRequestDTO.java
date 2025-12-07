package org.greta.eshop_api.exposition.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO(
        @NotBlank(message = "Le statut est obligatoire.")
        String status,

        @NotNull(message = "L'identifiant du client est obligatoire.")
        Long customerId
) {}
