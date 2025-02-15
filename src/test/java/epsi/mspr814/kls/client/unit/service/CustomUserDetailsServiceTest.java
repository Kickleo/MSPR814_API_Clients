package epsi.mspr814.kls.client.unit.service;

import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.model.Role;
import epsi.mspr814.kls.client.model.RoleName;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange: create a dummy Person with a role
        Person dummyPerson = new Person();
        dummyPerson.setUsername("testuser");
        dummyPerson.setPassword("encodedPassword");

        // Create a dummy role using Mockito since we only need its behavior
        // Assumes that the Role type is accessible as epsi.mspr814.kls.client.model.Role
        // and that getName() returns an enum (or similar) whose name() returns a String.
        Role role = mock(Role.class);
        // Also mock the enum (or object) returned by role.getName()
        RoleName roleName = mock(RoleName.class);
        when(role.getName()).thenReturn(roleName);
        when(roleName.name()).thenReturn("ROLE_USER");

        dummyPerson.setRoles(Collections.singleton(role));
        when(personRepository.findByUsername("testuser")).thenReturn(Optional.of(dummyPerson));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange: simulate no user found
        when(personRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("nonexistent"));
    }
}