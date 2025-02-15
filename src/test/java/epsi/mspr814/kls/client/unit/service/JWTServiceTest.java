package epsi.mspr814.kls.client.unit.service;

import epsi.mspr814.kls.client.service.JWTService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JWTService jwtService;

    @Test
    void testGenerateToken() {
        // Arrange
        String username = "testuser";
        // Create a dummy Jwt that returns a fixed token value.
        Jwt dummyJwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "HS256")
                .claim("sub", username)
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(dummyJwt);

        // Act
        String token = jwtService.generateToken(username);

        // Assert
        assertEquals("dummy-token", token);

        // Capture the encoder parameters and verify the claims.
        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtEncoderParameters params = captor.getValue();
        JwtClaimsSet claims = params.getClaims();
        assertEquals("clients-api", claims.getClaim("iss"));
        assertEquals(username, claims.getSubject());
        // Verify that the expiration is set to 1 day after issuance.
        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();
        assertNotNull(issuedAt);
        assertNotNull(expiresAt);
        assertEquals(1, ChronoUnit.DAYS.between(issuedAt, expiresAt));
    }
}