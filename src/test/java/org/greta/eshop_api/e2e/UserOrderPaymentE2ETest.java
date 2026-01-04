package org.greta.eshop_api.e2e;

import org.greta.eshop_api.e2e.config.TestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("e2e")
class UserOrderPaymentE2ETest extends TestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterLoginAndCreatePaidOrder() throws Exception {

        // 1. Créer un compte utilisateur
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "john",
                        "email": "john@example.com",
                        "password": "secret123"
                    }
                """))
                .andExpect(status().isCreated());

        // 2. Se connecter et récupérer le token JWT
        String tokenResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    { "username": "john", "password": "secret123" }
                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = extractToken(tokenResponse); // helper qui parse le JSON

        // 3. Créer un produit (simule un admin)
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + generateJwtForRole("ADMIN"))
                        .content("""
                    { "name": "Potion", "price": 50.0, "stock": 10, "active": true }
                """))
                .andExpect(status().isCreated());

        // 4. Passer une commande en tant qu’utilisateur
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "products": [ { "id": 1, "quantity": 2 } ]
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(100.0));

        // 5. Simuler un paiement Stripe (mocké)
        mockMvc.perform(post("/payment/checkout")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "orderId": 1,
                        "amount": 100.0,
                        "paymentMethod": "STRIPE_MOCK"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // 6. Vérifier que la commande est bien marquée comme payée
        mockMvc.perform(get("/orders/1")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.products[0].name").value("Potion"));
    }
}
