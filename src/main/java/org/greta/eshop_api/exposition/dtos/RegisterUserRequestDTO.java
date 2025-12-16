package org.greta.eshop_api.exposition.dtos;

import org.greta.eshop_api.persistence.entities.Role;
import org.greta.eshop_api.persistence.entities.UserEntity;

public record RegisterUserRequestDTO(
        String email,
        String password
) {
    public UserEntity toEntity() {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setRole(Role.USER);
        // On ne SET pas le mot de passe dans le Mapper
        return user;
    }
}
