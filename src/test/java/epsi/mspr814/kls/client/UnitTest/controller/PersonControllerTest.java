package epsi.mspr814.kls.client.UnitTest.controller;

import epsi.mspr814.kls.client.controller.PersonController;
import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.service.PersonService;
import epsi.mspr814.kls.client.dto.Mappers;
import epsi.mspr814.kls.client.dto.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PersonControllerTest {
    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    @Test
    void testCreatePerson() {
        RegisterPersonDTO registerPersonDTO = RegisterPersonDTO.builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phone("123456789")
                .email("john@example.com")
                .password("password")
                .build();

        Person person = Person.builder()
                .id(UUID.randomUUID()) // Assume the DB assigns an ID
                .username(registerPersonDTO.getUsername())
                .firstName(registerPersonDTO.getFirstName())
                .lastName(registerPersonDTO.getLastName())
                .phone(registerPersonDTO.getPhone())
                .email(registerPersonDTO.getEmail())
                .password("$2a$10$HashedPasswordHere") // Fake hashed password
                .build();

        when(personService.registerNewPerson(any(RegisterPersonDTO.class))).thenReturn(person);

        // Call the controller
        ResponseEntity<PersonDTO> response = personController.register(registerPersonDTO);

        // Check response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(person.getUsername(), response.getBody().getUsername());
        assertEquals(person.getFirstName(), response.getBody().getFirstName());
        assertEquals(person.getLastName(), response.getBody().getLastName());
        assertEquals(person.getPhone(), response.getBody().getPhone());
        assertEquals(person.getEmail(), response.getBody().getEmail());
    }

    @Test
    void testGetPersonById() {
        UUID id = UUID.randomUUID();
        Person person = Person.builder()
                .id(id)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phone("123456789")
                .email("john@example.com")
                .password("$2a$10$HashedPasswordHere") // Simulate hashed password
                .build();

        PersonDTO expectedDTO = PersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .email(person.getEmail())
                .build();

        when(personService.getById(id)).thenReturn(Optional.of(person));

        ResponseEntity<PersonDTO> response = personController.getById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedDTO, response.getBody());
    }

    @Test
    void testGetPersonById_NotFound() {
        UUID id = UUID.randomUUID();
        when(personService.getById(id)).thenReturn(Optional.empty());

        ResponseEntity<PersonDTO> response = personController.getById(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdatePerson() {
        UUID id = UUID.randomUUID();
        Person person = Person.builder()
                .id(id)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phone("123456789")
                .email("john@example.com")
                .password("$2a$10$HashedPasswordHere") // Simulate stored hashed password
                .build();

        PersonDTO expectedDTO = PersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .email(person.getEmail())
                .build();

        when(personService.update(eq(id), any(RegisterPersonDTO.class))).thenReturn(Optional.of(person));

        // Act
        ResponseEntity<PersonDTO> response = personController.update(id, Mappers.personToRegisterDTO(person));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedDTO, response.getBody());

        verify(personService, times(1)).update(eq(id), any(RegisterPersonDTO.class));
    }

    @Test
    void testUpdatePerson_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        Person person = Person.builder()
                .id(id)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phone("123456789")
                .email("john@example.com")
                .password("$2a$10$HashedPasswordHere") // Simulate hashed password
                .build();

        when(personService.update(eq(id), any(RegisterPersonDTO.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<PersonDTO> response = personController.update(id, Mappers.personToRegisterDTO(person));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(personService, times(1)).update(eq(id), any(RegisterPersonDTO.class));
    }

    @Test
    void testDeletePerson() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(personService).delete(id);

        // Act
        ResponseEntity<Void> response = personController.delete(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(personService, times(1)).delete(id);
    }
}
