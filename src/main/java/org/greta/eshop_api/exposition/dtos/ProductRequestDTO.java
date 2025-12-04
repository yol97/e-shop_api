package org.greta.eshop_api.exposition.dtos;

public record ProductRequestDTO(
        String name,
        String description,
        String imageUrl,
        Boolean isActive,
        double price,
        int stock,
        double discount
) {}