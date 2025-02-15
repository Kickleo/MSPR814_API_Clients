package epsi.mspr814.kls.client.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epsi.mspr814.kls.client.model.AuthRequest;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(LoginControllerIT.TestConfig.class)
class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JWTService jwtService() {
            // Create and return a Mockito mock of JWTService
            return Mockito.mock(JWTService.class);
        }
    }

    @BeforeEach
    public void setUp() {
        // Clean the repository for a fresh start
        personRepository.deleteAll();

        // Create a test user with an encrypted password
        Person testUser = new Person();
        testUser.setUsername("testUser");
        testUser.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        personRepository.save(testUser);
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("testPassword");

        // Configure the mock JWTService to return a dummy token
        when(jwtService.generateToken("testUser")).thenReturn("dummy-token");

        // When & Then: perform the request and expect a 200 OK with the token in the response
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        // Given a non-existent user
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("nonexistent");
        authRequest.setPassword("anyPassword");

        // When & Then: perform the request and expect a 401 Unauthorized with the appropriate message
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Utilisateur inconnu"));
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        // Given an existing user but with the wrong password
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("wrongPassword");

        // When & Then: perform the request and expect a 401 Unauthorized with the error message
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Mot de passe incorrect"));
    }
}