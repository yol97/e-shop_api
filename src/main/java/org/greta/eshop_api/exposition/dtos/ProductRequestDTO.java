package org.greta.eshop_api.exposition.dtos;
import jakarta.validation.constraints.*;

public record ProductRequestDTO(

        @NotBlank(message = "Le nom du produit ne peut pas être vide")
        @Size(max = 100, message = "Le nom du produit ne doit pas dépasser 100 caractères")
        String name,

        @NotBlank(message = "La description ne peut pas être vide")
        @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
        String description,

        @NotBlank(message = "L’URL de l’image ne peut pas être vide")
        @Size(max = 255, message = "L’URL de l’image ne doit pas dépasser 255 caractères")
        @Pattern(
                regexp = "^(https?://).+$",
                message = "L’URL de l’image doit commencer par http:// ou https://"
        )
        String imageUrl,

        @NotNull(message = "Le statut d’activation ne peut pas être null")
        Boolean isActive,

        @NotNull(message = "Le prix ne peut pas être null")
        // @Positive(message = "Le prix doit être positif")
        // @Min(value = 1, message = "Le prix doit être au moins de 1")
        @Max(value = 10000, message = "Le prix ne peut pas dépasser 10 000")
        double price,

        @PositiveOrZero(message = "Le stock ne peut pas être négatif")
        int stock,

        @PositiveOrZero(message = "La remise ne peut pas être négative")
        @Max(value = 90, message = "La remise ne peut pas dépasser 90%")
        double discount
) {}