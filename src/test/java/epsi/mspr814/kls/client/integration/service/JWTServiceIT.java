package epsi.mspr814.kls.client.integration.service;

import epsi.mspr814.kls.client.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JWTServiceIT {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void testGenerateTokenIntegration() {
        String username = "integrationUser";
        String token = jwtService.generateToken(username);
        assertNotNull(token);

        // Decode the token to inspect its claims.
        Jwt jwt = jwtDecoder.decode(token);
        // Instead of using getIssuer(), retrieve the raw claim value.
        assertEquals("clients-api", jwt.getClaim("iss"));
        assertEquals(username, jwt.getSubject());

        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();
        assertNotNull(issuedAt);
        assertNotNull(expiresAt);
        assertEquals(1, ChronoUnit.DAYS.between(issuedAt, expiresAt));
    }
}