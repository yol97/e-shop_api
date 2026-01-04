package org.greta.eshop_api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.greta.eshop_api.persistence.entities.Role;
import org.greta.eshop_api.persistence.entities.UserEntity;
import org.greta.eshop_api.persistence.repositories.UserRepository;
import org.greta.eshop_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("integration")
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnJwtTokenWhenLoginIsValid() throws Exception {
        // Arrange : user en base
        persistUser("admin@example.com", "admin123", Role.ADMIN);

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@example.com",
                                  "password": "admin123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldRejectAccessWhenPasswordIsInvalid() throws Exception {
        // Arrange : user en base
        persistUser("admin@example.com", "admin123", Role.ADMIN);

        // Act + Assert : mauvais mot de passe
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "admin@example.com",
                              "password": "wrong_password"
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenIsExpired() throws Exception {
        int originalExpiration = (int) ReflectionTestUtils.getField(jwtUtil, "jwtExpirationMs");

        try {
            // force un token expiré
            ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", -1000);

            UserEntity user = new UserEntity();
            user.setEmail("user@example.com");
            user.setRole(Role.USER);

            String expiredToken = jwtUtil.generateToken(user);

            mockMvc.perform(get("/products")
                            .header("Authorization", "Bearer " + expiredToken))
                    .andExpect(status().isUnauthorized());

        } finally {
            // ✅ restore pour ne pas casser les autres tests
            ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", originalExpiration);
        }
    }

    @Test
    void shouldRejectAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer fake_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectAccessForUserRoleToAdminEndpoint() throws Exception {
        // Arrange : USER en base
        persistUser("user@example.com", "user123", Role.USER);

        // Login -> token valide
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"user@example.com","password":"user123"}
                            """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = new ObjectMapper().readTree(loginResponse).get("token").asText();

        // Act + Assert : USER sur /admin -> 403
        mockMvc.perform(get("/admin/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminAccessToAdminEndpoint() throws Exception {
        persistUser("admin@example.com", "admin123", Role.ADMIN);

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"admin@example.com","password":"admin123"}
                            """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = new ObjectMapper().readTree(loginResponse).get("token").asText();

        mockMvc.perform(get("/admin/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPublicAccessToGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    private UserEntity persistUser(String email, String rawPassword, Role role) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }
}

