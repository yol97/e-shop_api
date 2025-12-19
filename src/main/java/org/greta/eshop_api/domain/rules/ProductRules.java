package org.greta.eshop_api.domain.rules;
import org.greta.eshop_api.exceptions.BusinessRuleException;
import org.greta.eshop_api.persistence.entities.ProductEntity;

import java.time.LocalDate;

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

    public static void validateBeforeDiscount(double discount) {
        if (discount < 0 || discount > 100) {
            throw new BusinessRuleException("Réduction invalide");
        }
    }

    public static void validateDiscountedPrice(double price, double discount) {
        double DiscountedPrice = price * (1-discount/100);
        if (DiscountedPrice < 0) {
            throw new BusinessRuleException(("Le prix remisé ne peut être négatif"));
        }
    }

    public static void validateInactiveProductHasNoDiscount(ProductEntity product) {
        if (!product.getIsActive() && product.getDiscount() > 0) {
            throw new BusinessRuleException("Un produit inactif ne peut pas avoir de réduction");
        }
    }

    public static void validatePromoDates(ProductEntity product) {
        LocalDate start = product.getPromoStart();
        LocalDate end = product.getPromoEnd();

        if (start != null && end != null && start.isAfter(end)) {
            throw new BusinessRuleException("Dates de promotion invalides");
        }
    }

    public static void validatePromoNotExpired(ProductEntity product) {
        if (product.getPromoEnd() != null &&
        product.getPromoEnd().isBefore(LocalDate.now())); {
            throw new BusinessRuleException("La promotion est expirée");
        }
    }
}