package org.greta.eshop_api.domain.rules;

import org.greta.eshop_api.exceptions.BusinessRuleException;
import org.greta.eshop_api.exceptions.ResourceNotFoundException;
import org.greta.eshop_api.exposition.dtos.OrderItemRequestDTO;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.OrderItemEntity;
import org.greta.eshop_api.persistence.entities.ProductEntity;

import java.util.List;

public class OrderRules {

    // 👉 Vérifie que le customer existe et n'est pas suspendu
    public static void validateCustomer(CustomerEntity customer) {
        if (customer == null) {
            throw new ResourceNotFoundException("Le client n'existe pas.");
        }
        if (customer.isSuspended()) {
            throw new BusinessRuleException("Le client est suspendu et ne peut pas passer de commande.");
        }
    }

    public static void validateProducts(List<ProductEntity> products) {
        if (products.isEmpty())
            throw new BusinessRuleException("Aucun produit dans la commande");
        boolean allInactive = products.stream().noneMatch(ProductEntity::getIsActive);
        if (allInactive)
            throw new BusinessRuleException("Aucun produit actif dans la commande");
    }

    public static void validateStock(OrderItemRequestDTO item, ProductEntity product) {
        if (item.quantity() > product.getStock()) {
            throw new BusinessRuleException("Stock insuffisant pour le produit " + product.getName());
        }
    }

    public static void validateTotal(double total) {
        double max = 5000.0;
        if (total > max) {
            throw new BusinessRuleException("Le montant total de la commande dépasse le plafond autorisé (" + max + "€)");
        }
    }

    public static void validateOrderStatus(String status) {
        List<String> allowed = List.of("PENDING", "SHIPPED", "DELIVERED", "CANCELLED");
        if (!allowed.contains(status)) {
            throw new BusinessRuleException("Statut de commande invalide : " + status);
        }
    }


}

