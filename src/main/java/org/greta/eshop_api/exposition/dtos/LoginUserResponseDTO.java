package org.greta.eshop_api.exposition.dtos;

import org.greta.eshop_api.persistence.entities.UserEntity;

public record LoginUserResponseDTO(
        String token,
        String email,
        String role
) {

    public static LoginUserResponseDTO fromEntity(String token, UserEntity user) {
        return new LoginUserResponseDTO(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}
