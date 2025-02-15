package epsi.mspr814.kls.client.unit.controller;

import epsi.mspr814.kls.client.controller.LoginController;
import epsi.mspr814.kls.client.model.AuthRequest;
import epsi.mspr814.kls.client.model.AuthResponse;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.service.JWTService;
import epsi.mspr814.kls.client.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private PersonService personService;

    @Mock
    private JWTService jwtService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private LoginController loginController;

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("nonexistent");
        authRequest.setPassword("anyPassword");

        // Simulate no user found in the repository
        when(personService.getByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = loginController.authenticate(authRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Utilisateur inconnu", response.getBody());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("wrongPassword");

        Person person = new Person();
        person.setUsername("user");
        // Assume this is the encoded password stored in the system
        person.setPassword("encodedPassword");

        // Simulate user found
        when(personService.getByUsername("user")).thenReturn(Optional.of(person));
        // Simulate password mismatch
        when(bCryptPasswordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When
        ResponseEntity<?> response = loginController.authenticate(authRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Mot de passe incorrect", response.getBody());
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("user");
        authRequest.setPassword("correctPassword");

        Person person = new Person();
        person.setUsername("user");
        person.setPassword("encodedPassword");

        // Simulate user found
        when(personService.getByUsername("user")).thenReturn(Optional.of(person));
        // Simulate matching password
        when(bCryptPasswordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        // Simulate token generation
        when(jwtService.generateToken("user")).thenReturn("dummy-token");

        // When
        ResponseEntity<?> response = loginController.authenticate(authRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("dummy-token", authResponse.getToken());
    }
}