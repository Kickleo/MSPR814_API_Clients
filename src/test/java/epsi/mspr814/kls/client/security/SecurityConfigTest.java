package epsi.mspr814.kls.client.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(properties = {"jwt.secret=my-secret-key"})
@AutoConfigureMockMvc
@Import(SecurityConfigTest.DummyController.class)  // Import our dummy endpoints for testing
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private InMemoryUserDetailsManager userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBeansExistence() {
        // Verify that our security-related beans are loaded
        assertThat(jwtEncoder).isNotNull();
        assertThat(jwtDecoder).isNotNull();
        assertThat(passwordEncoder).isNotNull();
        assertThat(userDetailsService).isNotNull();
    }

    @Test
    void testPasswordEncoder() {
        String rawPassword = "password";
        String encoded = passwordEncoder.encode(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encoded)).isTrue();
    }

    @Test
    void testUserDetailsService() {
        var user = userDetailsService.loadUserByUsername("user");
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("user");
        // Verify that the password (provided in the config) matches after encoding
        assertThat(passwordEncoder.matches("password", user.getPassword())).isTrue();
    }

    @Test
    void testPublicEndpointsArePermitted() throws Exception {
        // /auth/login and /api/clients/register should be accessible without authentication.
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/clients/register"))
                .andExpect(status().isOk());
    }

    @Test
    void testProtectedEndpointRequiresAuthentication() throws Exception {
        // Any endpoint not explicitly permitted should require authentication.
        // Our dummy controller defines "/api/secure" which should be protected.
        mockMvc.perform(get("/api/secure"))
                .andExpect(status().isUnauthorized());
    }

    // --- Dummy Controller for testing endpoints ---
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
