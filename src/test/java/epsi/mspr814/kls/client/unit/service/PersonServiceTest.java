package epsi.mspr814.kls.client.unit.service;

import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.model.Professional;
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

import java.util.*;

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
    private Person person;
    private UUID personId;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        person = new Person();
        person.setId(personId);
        person.setUsername("oldUser");
        person.setFirstName("Old");
        person.setLastName("Name");
        person.setPhone("123456789");
        person.setEmail("old@example.com");
        person.setAddresses(Collections.emptyList());

        Professional professional = new Professional();
        professional.setSiret("old-siret");
        person.setProfessional(professional);
        person.setRoles(Collections.emptySet());
        person.setPassword("oldPassword");
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void testGetAll() {
        List<Person> persons = Collections.singletonList(person);
        when(personRepository.findAll()).thenReturn(persons);

        List<Person> result = personService.getAll();

        assertEquals(1, result.size());
        assertEquals(person, result.get(0));
        verify(personRepository).findAll();
    }

    @Test
    void testGetById_Found() {
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        Optional<Person> result = personService.getById(personId);

        assertTrue(result.isPresent());
        assertEquals(person, result.get());
        verify(personRepository).findById(personId);
    }

    @Test
    void testGetById_NotFound() {
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        Optional<Person> result = personService.getById(personId);

        assertFalse(result.isPresent());
        verify(personRepository).findById(personId);
    }

    @Test
    void testGetByUsername_Found() {
        when(personRepository.findByUsername("oldUser")).thenReturn(Optional.of(person));

        Optional<Person> result = personService.getByUsername("oldUser");

        assertTrue(result.isPresent());
        assertEquals(person, result.get());
        verify(personRepository).findByUsername("oldUser");
    }

    @Test
    void testGetByUsername_NotFound() {
        when(personRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Person> result = personService.getByUsername("unknown");

        assertFalse(result.isPresent());
        verify(personRepository).findByUsername("unknown");
    }

    @Test
    void testUpdate_WithPassword() {
        // Prepare updated DTO with new password and new Professional data.
        RegisterPersonDTO updatedDto = new RegisterPersonDTO();
        updatedDto.setUsername("newUser");
        updatedDto.setFirstName("New");
        updatedDto.setLastName("Name");
        updatedDto.setPhone("987654321");
        updatedDto.setEmail("new@example.com");
        updatedDto.setAddresses(Collections.emptyList());
        // New professional details
        Professional newProfessional = new Professional();
        newProfessional.setSiret("new-siret");
        updatedDto.setProfessional(newProfessional);
        updatedDto.setRoles(Collections.emptySet());
        updatedDto.setPassword("newPassword");

        // Simulate password encoding.
        String encodedPassword = "encodedNewPassword";
        when(passwordEncoder.encode("newPassword")).thenReturn(encodedPassword);

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Person> result = personService.update(personId, updatedDto);

        assertTrue(result.isPresent());
        Person updatedPerson = result.get();
        assertEquals("newUser", updatedPerson.getUsername());
        assertEquals("New", updatedPerson.getFirstName());
        assertEquals("Name", updatedPerson.getLastName());
        assertEquals("987654321", updatedPerson.getPhone());
        assertEquals("new@example.com", updatedPerson.getEmail());
        assertEquals(updatedDto.getAddresses(), updatedPerson.getAddresses());
        // Verify that professional is updated
        assertNotNull(updatedPerson.getProfessional());
        assertEquals("new-siret", updatedPerson.getProfessional().getSiret());
        // Password should now be the encoded version.
        assertEquals(encodedPassword, updatedPerson.getPassword());

        verify(passwordEncoder).encode("newPassword");
        verify(personRepository).findById(personId);
        verify(personRepository).save(updatedPerson);
    }

    @Test
    void testUpdate_WithoutPassword() {
        // Prepare updated DTO without a new password.
        RegisterPersonDTO updatedDto = new RegisterPersonDTO();
        updatedDto.setUsername("newUser");
        updatedDto.setFirstName("New");
        updatedDto.setLastName("Name");
        updatedDto.setPhone("987654321");
        updatedDto.setEmail("new@example.com");
        updatedDto.setAddresses(Collections.emptyList());
        // New professional details
        Professional newProfessional = new Professional();
        newProfessional.setSiret("new-siret");
        updatedDto.setProfessional(newProfessional);
        updatedDto.setRoles(Collections.emptySet());
        updatedDto.setPassword(""); // No new password provided.

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Person> result = personService.update(personId, updatedDto);

        assertTrue(result.isPresent());
        Person updatedPerson = result.get();
        assertEquals("newUser", updatedPerson.getUsername());
        assertEquals("New", updatedPerson.getFirstName());
        assertEquals("Name", updatedPerson.getLastName());
        assertEquals("987654321", updatedPerson.getPhone());
        assertEquals("new@example.com", updatedPerson.getEmail());
        assertEquals(updatedDto.getAddresses(), updatedPerson.getAddresses());
        // Verify that professional is updated
        assertNotNull(updatedPerson.getProfessional());
        assertEquals("new-siret", updatedPerson.getProfessional().getSiret());
        // Since no new password was provided, the password remains unchanged.
        assertEquals("oldPassword", updatedPerson.getPassword());

        verify(personRepository).findById(personId);
        verify(personRepository).save(updatedPerson);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testUpdate_NotFound() {
        RegisterPersonDTO updatedDto = new RegisterPersonDTO();
        updatedDto.setUsername("newUser");
        // Other fields can be set as needed.
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        Optional<Person> result = personService.update(personId, updatedDto);

        assertFalse(result.isPresent());
        verify(personRepository).findById(personId);
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void testDelete() {
        personService.delete(personId);
        verify(personRepository).deleteById(personId);
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
