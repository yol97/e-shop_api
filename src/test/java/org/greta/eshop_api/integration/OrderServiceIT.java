package org.greta.eshop_api.integration;

import org.greta.eshop_api.domain.services.OrderService;
import org.greta.eshop_api.exposition.dtos.OrderItemRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderRequestDTO;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.CustomerRepository;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class OrderServiceIT {

    @Autowired private OrderService orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CustomerRepository customerRepository;

    @Test
    void shouldDecreaseStockAfterOrderCreation() {
        // 1) Produit stock = 10
        ProductEntity product = new ProductEntity();
        product.setName("T-shirt test");
        product.setDescription("Description test");
        product.setImageUrl("https://example.com/test.png");
        product.setIsActive(true);
        product.setPrice(20.0);
        product.setStock(10);
        product.setDiscount(0.0); // même si default, on le met explicitement
        product = productRepository.save(product);

        // 2) Client minimal requis
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer = customerRepository.save(customer);

        // 3) Commande qty = 2 (status autorisé)
        OrderRequestDTO orderDto = new OrderRequestDTO("PENDING", customer.getId());
        List<OrderItemRequestDTO> itemsDto = List.of(
                new OrderItemRequestDTO(product.getId(), 2)
        );

        orderService.create(orderDto, itemsDto);

        // 4) Vérifier stock = 8
        ProductEntity updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(8);
    }
}
