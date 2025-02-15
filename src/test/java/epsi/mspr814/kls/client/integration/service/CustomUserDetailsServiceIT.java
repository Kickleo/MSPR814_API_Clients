package epsi.mspr814.kls.client.integration.service;

import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.model.Role;
import epsi.mspr814.kls.client.model.RoleName;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.repository.RoleRepository;
import epsi.mspr814.kls.client.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomUserDetailsServiceIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    public void setup() {
        // Clear the repository before each test
        personRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        // Create a dummy role.
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);
        role = roleRepository.save(role);

        // Arrange: create and save a dummy Person with a role
        Person person = new Person();
        person.setUsername("integrationUser");
        person.setPassword("encodedPassword");

        person.setRoles(Collections.singleton(role));

        personRepository.save(person);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("integrationUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("integrationUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("nonexistent"));
    }
}