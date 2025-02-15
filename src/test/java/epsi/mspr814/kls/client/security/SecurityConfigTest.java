package epsi.mspr814.kls.client.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import epsi.mspr814.kls.client.ClientApplication;
import epsi.mspr814.kls.client.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(classes = {ClientApplication.class, SecurityConfigTest.TestConfig.class})
@AutoConfigureMockMvc
@ImportAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
@TestPropertySource(properties = {
        "jwt.secret=my-secret-key",
        "spring.web.resources.add-mappings=false" // désactive les mappings statiques pour forcer l'appel à notre contrôleur
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InMemoryUserDetailsManager userDetailsService;

    @Test
    void testBeansExistence() {
        // Vérifie que les beans de sécurité essentiels sont bien créés
        assertThat(jwtEncoder).isNotNull();
        assertThat(jwtDecoder).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(userDetailsService).isNotNull();
    }

    @Test
    void testPasswordEncoder() {
        String raw = "password";
        String encoded = passwordEncoder.encode(raw);
        assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
    }

    @Test
    void testUserDetailsService() {
        var user = userDetailsService.loadUserByUsername("user");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(passwordEncoder.matches("password", user.getPassword())).isTrue();
    }

    @Test
    void testPublicEndpointsAreAccessible() throws Exception {
        // Les endpoints "/auth/login" et "/api/clients/register" doivent être accessibles sans authentification
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/clients/register"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointRequiresAuthentication() throws Exception {
        // Toute autre URL (ici "/api/secure") doit renvoyer 401 Unauthorized si non authentifiée
        mockMvc.perform(get("/api/secure"))
                .andExpect(status().isUnauthorized());
    }

    // --- Configuration de test pour déclarer les beans nécessaires ---
    @TestConfiguration
    @Import({SecurityConfig.class, DummyController.class})
    static class TestConfig {

        @Bean
        public PersonRepository personRepository() {
            // Définition d'un bean factice pour PersonRepository via Mockito
            return Mockito.mock(PersonRepository.class);
        }
    }

    // --- DummyController pour tester les mappings ---
    @RestController
    public static class DummyController {

        @GetMapping("/auth/login")
        public String login() {
            return "login";
        }

        @GetMapping("/api/clients/register")
        public String register() {
            return "register";
        }

        @GetMapping("/api/secure")
        public String secure() {
            return "secure";
        }
    }
}