package org.greta.eshop_api.unit;
import org.greta.eshop_api.domain.rules.ProductRules;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductRulesTest {

    @Test
    @DisplayName("should throw if discount > 100%")
    void shouldThrowIfDiscountAbove100() {

        double discount = 120.0;

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validateBeforeDiscount(discount));

        assertTrue(ex.getMessage().contains("Réduction"));
    }

    @Test
    @DisplayName("should throw if discount < 0%")
    void shouldThrowIfDiscountNegative() {

        double discount = -20.0;

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validateBeforeDiscount(discount));

        assertTrue(ex.getMessage().contains("Réduction"));
    }

    @Test
    @DisplayName("Should throw if discounted price is negative")
    void shouldThrowIfDiscountedPriceNegative() {

        double price = 50.0;
        double discount = 120.0;

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validateDiscountedPrice(price, discount));

        assertTrue(ex.getMessage().contains("prix"));
    }

    @Test
    @DisplayName("Should throw if inactive product has discount")
    void shouldThrowIfInactiveProductHasDiscount() {

        ProductEntity product = new ProductEntity();
        product.setIsActive(false);
        product.setDiscount(20.0);

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validateInactiveProductHasNoDiscount(product));

        assertTrue(ex.getMessage().contains("inactif"));
    }

    @Test
    @DisplayName("Should throw if promo start date is after promo end date")
    void shouldThrowIfPromoStartAfterPromoEnd() {

        ProductEntity product = new ProductEntity();
        product.setPromoStart(LocalDate.now().plusDays(5));
        product.setPromoEnd(LocalDate.now().plusDays(1));

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validatePromoDates(product));

        assertTrue(ex.getMessage().contains("Dates"));
    }

    @Test
    @DisplayName("Should throw if promo is expired")
    void shouldThrowIfPromoExpired() {

        ProductEntity product = new ProductEntity();
        product.setPromoEnd(LocalDate.now().minusDays(1));

        Exception ex = assertThrows(RuntimeException.class,
                () -> ProductRules.validatePromoNotExpired(product));

        assertTrue(ex.getMessage().contains("expirée"));
    }

    @Test
    @DisplayName("Should pass if promo dates are valid")
    void shouldPassIfPromoDatesAreValid() {

        ProductEntity product = new ProductEntity();
        product.setPromoStart(LocalDate.now().plusDays(1));
        product.setPromoEnd(LocalDate.now().plusDays(5));

        assertDoesNotThrow(() ->
                ProductRules.validatePromoDates(product));
    }
}