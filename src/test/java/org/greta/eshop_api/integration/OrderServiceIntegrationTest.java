package org.greta.eshop_api.integration;

import jakarta.transaction.Transactional;
import org.greta.eshop_api.domain.services.OrderService;
import org.greta.eshop_api.exposition.dtos.OrderItemRequestDTO;
import org.greta.eshop_api.exposition.dtos.OrderRequestDTO;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.OrdersEntity;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.CustomerRepository;
import org.greta.eshop_api.persistence.repositories.OrderRepository;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
class OrderServiceIntegrationTest {

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CustomerRepository customerRepository;

    @Test
    @Transactional
    void shouldCreateOrderAndPersistItWithProducts() {
        // 1) Arrange : client minimal
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer = customerRepository.save(customer);

        // 2) Arrange : produit (tous les champs NOT NULL)
        ProductEntity product = new ProductEntity();
        product.setName("Potion");
        product.setDescription("Restore HP");
        product.setImageUrl("potion.png");
        product.setIsActive(true);
        product.setPrice(50.0);
        product.setStock(10);
        product.setDiscount(0.0);
        product = productRepository.save(product);

        // 3) Arrange : DTO commande + items
        OrderRequestDTO orderDto = new OrderRequestDTO("PENDING", customer.getId());
        List<OrderItemRequestDTO> itemsDto = List.of(
                new OrderItemRequestDTO(product.getId(), 2)
        );

        // 4) Act
        orderService.create(orderDto, itemsDto);

        // 5) Assert : on vérifie en base (OrderRepository -> H2)
        List<OrdersEntity> all = orderRepository.findAll();
        assertThat(all).hasSize(1);

        OrdersEntity persisted = all.get(0);
        assertThat(persisted.getCustomer().getFirstName()).isEqualTo("John");
        assertThat(persisted.getItems()).hasSize(1);
        assertThat(persisted.getItems().get(0).getProduct().getName()).isEqualTo("Potion");
        assertThat(persisted.getItems().get(0).getQuantity()).isEqualTo(2);
    }
}

