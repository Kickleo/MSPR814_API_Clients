package epsi.mspr814.kls.client.unit.service;

import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.service.PersonService;
import epsi.mspr814.kls.client.dto.Mappers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonService personService;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void testCreatePerson() {
        // Arrange
        RegisterPersonDTO registerPersonDTO = RegisterPersonDTO.builder()
                .username("JD")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .email("john.doe@example.com")
                .password("plain_password")
                .build();

        String hashedPassword = "$2a$10$HashedPasswordHere"; // Simulated hash
        Person personToSave = Mappers.registerDTOToPerson(registerPersonDTO);
        personToSave.setId(UUID.randomUUID());
        personToSave.setPassword(hashedPassword);

        when(passwordEncoder.encode(any(String.class))).thenReturn(hashedPassword);
        when(personRepository.save(any(Person.class))).thenReturn(personToSave);

        // Act
        Person savedPerson = personService.registerNewPerson(registerPersonDTO);

        // Assert
        assertNotNull(savedPerson.getId());
        assertEquals("John", savedPerson.getFirstName());
        assertNotEquals("plain_password", savedPerson.getPassword()); // Ensures password is hashed
        assertEquals(hashedPassword, savedPerson.getPassword()); // Ensures correct hash is stored

        verify(personRepository, times(1)).save(any(Person.class));
        verify(passwordEncoder, times(1)).encode("plain_password");
    }
}
