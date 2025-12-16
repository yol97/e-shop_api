package org.greta.eshop_api.exposition.dtos;

public record LoginUserRequestDTO(
        String email,
        String password
) {}
