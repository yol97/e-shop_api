package org.greta.eshop_api.domain.rules;
import org.greta.eshop_api.persistence.entities.ProductEntity;

public class ProductRules {

    public static void validateBeforeCreation(ProductEntity product) {
        if (product.getPrice() <= 0) {
            throw new RuntimeException("Le prix doit être supérieur à 0 (depuis ProductRules).");
        }
        if (product.getStock() < 0) {
            throw new RuntimeException("Le stock ne peut pas être négatif (depuis ProductRules).");
        }
    }

    public static void validateBeforeUpdate(ProductEntity product) {
        if (product.getPrice() > 10000) {
            throw new RuntimeException("Le prix dépasse la limite autorisée (depuis ProductRules).");
        }
    }
}