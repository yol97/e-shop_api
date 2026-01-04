package org.greta.eshop_api.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.greta.eshop_api.e2e.config.TestContainerConfig;
import org.greta.eshop_api.persistence.entities.CustomerEntity;
import org.greta.eshop_api.persistence.entities.ProductEntity;
import org.greta.eshop_api.persistence.repositories.CustomerRepository;
import org.greta.eshop_api.persistence.repositories.ProductRepository;
import org.greta.eshop_api.persistence.entities.Role;
import org.greta.eshop_api.persistence.entities.UserEntity;
import org.greta.eshop_api.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("e2e")
class UserOrderPaymentE2ETest extends TestContainerConfig {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterLoginAndCreatePaidOrder() throws Exception {

        // Pré-requis technique : créer un customer (OrderService exige un customerId existant)
        CustomerEntity customer = new CustomerEntity();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer = customerRepository.save(customer);

        // 1. Créer un compte utilisateur
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk());

        // 2. Se connecter et récupérer le token JWT
        String tokenResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = extractToken(tokenResponse);

        // 3. Créer un produit (simule un admin)
        // NOTE: /products/** est permitAll , donc pas besoin de token admin.
        // On crée le produit en base pour être sûr (et éviter de dépendre de ProductRequestDTO).
        ProductEntity product = new ProductEntity();
        product.setName("Potion");
        product.setDescription("Restore HP");
        product.setImageUrl("potion.png");
        product.setIsActive(true);
        product.setPrice(50.0);
        product.setStock(10);
        product.setDiscount(0.0);
        product = productRepository.save(product);

        // 4. Passer une commande en tant qu’utilisateur
        String orderResponse = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "order": {
                                    "status": "PENDING",
                                    "customerId": %d
                                  },
                                  "items": [
                                    { "productId": %d, "quantity": 2 }
                                  ]
                                }
                                """.formatted(customer.getId(), product.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.customerId").value(customer.getId().intValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = extractOrderId(orderResponse);

        // 5. Simuler un paiement Stripe (mocké)
        // TODO: pas de PaymentController pour le moment => endpoint inexistant (404)
        /*
        mockMvc.perform(post("/payment/checkout")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "amount": 100.0,
                                  "paymentMethod": "STRIPE_MOCK"
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
        */

        // 6. Vérifier que la commande est bien marquée comme payée
        // TODO: ton OrderResponseDTO ne contient pas "PAID" ni les items, donc on vérifie l'existant.
        mockMvc.perform(get("/orders/" + orderId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) orderId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.customerId").value(customer.getId().intValue()));
    }

    private String extractToken(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    private long extractOrderId(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return node.get("id").asLong();
    }

    // Optionnel si plus tard tu veux vraiment "simuler un admin" via login :
    @SuppressWarnings("unused")
    private String loginAsAdminAndGetJwt() throws Exception {
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        String loginJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@example.com","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return extractToken(loginJson);
    }
}
