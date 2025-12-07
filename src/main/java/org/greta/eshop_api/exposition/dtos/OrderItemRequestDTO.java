package org.greta.eshop_api.exposition.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(

        @NotNull(message = "L'identifiant du produit est obligatoire.")
        @Positive(message = "L'identifiant du produit doit être positif.")
        Long productId,

        @Positive(message = "La quantité doit être supérieure à 0.")
        int quantity
) {}
