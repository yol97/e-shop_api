package org.greta.eshop_api.exposition.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequestDTO(

        @NotNull(message = "Les informations de commande sont obligatoires.")
        @Valid
        OrderRequestDTO order,

        @NotEmpty(message = "La commande doit contenir au moins un article.")
        @Valid
        List<OrderItemRequestDTO> items

) {}
