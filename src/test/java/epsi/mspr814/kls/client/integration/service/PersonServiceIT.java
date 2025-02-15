package epsi.mspr814.kls.client.integration.service;

import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.model.Professional;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PersonServiceIT {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID personId;

    @BeforeEach
    public void setup() {
        // Clean up the repository before each test.
        personRepository.deleteAll();

        Person person = new Person();
        person.setUsername("integrationUser");
        person.setFirstName("First");
        person.setLastName("Last");
        person.setPhone("111111111");
        person.setEmail("integration@example.com");
        person.setAddresses(Collections.emptyList());
        // Set professional as a Professional instance.
        Professional professional = new Professional();
        professional.setSiret("initial-siret");
        person.setProfessional(professional);
        person.setRoles(Collections.emptySet());
        // Encode a known password.
        person.setPassword(passwordEncoder.encode("password123"));
        person = personRepository.save(person);
        personId = person.getId();
    }

    @Test
    void testRegisterNewPerson() {
        // Arrange: create a DTO with test values.
        RegisterPersonDTO dto = new RegisterPersonDTO();
        dto.setUsername("newUser");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPhone("555555555");
        dto.setEmail("newuser@example.com");
        dto.setPassword("secret");
        dto.setAddresses(Collections.emptyList());

        // Set up a Professional instance with a SIRET value.
        Professional professional = new Professional();
        professional.setSiret("123456789");
        dto.setProfessional(professional);

        // Set roles as an empty set (adjust if you have specific roles).
        dto.setRoles(Collections.emptySet());

        // Act: register the new person.
        Person newPerson = personService.registerNewPerson(dto);

        // Assert: verify that the person was saved correctly.
        assertNotNull(newPerson.getId(), "The new person should have an assigned ID");
        assertEquals("newUser", newPerson.getUsername());
        assertEquals("New", newPerson.getFirstName());
        assertEquals("User", newPerson.getLastName());
        assertEquals("555555555", newPerson.getPhone());
        assertEquals("newuser@example.com", newPerson.getEmail());
        // Check that the password is encoded.
        assertTrue(passwordEncoder.matches("secret", newPerson.getPassword()));
        // Check that addresses and roles match the DTO.
        assertEquals(Collections.emptyList(), newPerson.getAddresses());
        assertEquals(Collections.emptySet(), newPerson.getRoles());
        // Verify professional details.
        assertNotNull(newPerson.getProfessional(), "Professional information should be present");
        assertEquals("123456789", newPerson.getProfessional().getSiret());
    }

    @Test
    void testGetAll() {
        var allPersons = personService.getAll();
        assertFalse(allPersons.isEmpty());
        assertEquals(1, allPersons.size());
    }

    @Test
    void testGetById() {
        Optional<Person> result = personService.getById(personId);
        assertTrue(result.isPresent());
        assertEquals("integrationUser", result.get().getUsername());
    }

    @Test
    void testGetByUsername() {
        Optional<Person> result = personService.getByUsername("integrationUser");
        assertTrue(result.isPresent());
        assertEquals(personId, result.get().getId());
    }

    @Test
    void testUpdate_WithPassword() {
        RegisterPersonDTO updatedDto = new RegisterPersonDTO();
        updatedDto.setUsername("updatedUser");
        updatedDto.setFirstName("UpdatedFirst");
        updatedDto.setLastName("UpdatedLast");
        updatedDto.setPhone("222222222");
        updatedDto.setEmail("updated@example.com");
        updatedDto.setAddresses(Collections.emptyList());
        // Update professional information.
        Professional updatedProfessional = new Professional();
        updatedProfessional.setSiret("updated-siret");
        updatedDto.setProfessional(updatedProfessional);
        updatedDto.setRoles(Collections.emptySet());
        updatedDto.setPassword("newPassword");

        Optional<Person> result = personService.update(personId, updatedDto);
        assertTrue(result.isPresent());
        Person updatedPerson = result.get();
        assertEquals("updatedUser", updatedPerson.getUsername());
        assertEquals("UpdatedFirst", updatedPerson.getFirstName());
        assertEquals("UpdatedLast", updatedPerson.getLastName());
        assertEquals("222222222", updatedPerson.getPhone());
        assertEquals("updated@example.com", updatedPerson.getEmail());
        assertEquals(updatedDto.getAddresses(), updatedPerson.getAddresses());
        // Verify updated professional details.
        assertNotNull(updatedPerson.getProfessional());
        assertEquals("updated-siret", updatedPerson.getProfessional().getSiret());
        // Verify the new password has been encoded.
        assertTrue(passwordEncoder.matches("newPassword", updatedPerson.getPassword()));
    }

    @Test
    void testUpdate_WithoutPassword() {
        RegisterPersonDTO updatedDto = new RegisterPersonDTO();
        updatedDto.setUsername("updatedUserNoPass");
        updatedDto.setFirstName("UpdatedFirst");
        updatedDto.setLastName("UpdatedLast");
        updatedDto.setPhone("333333333");
        updatedDto.setEmail("updatednopass@example.com");
        updatedDto.setAddresses(Collections.emptyList());
        // Update professional information.
        Professional updatedProfessional = new Professional();
        updatedProfessional.setSiret("updated-siret");
        updatedDto.setProfessional(updatedProfessional);
        updatedDto.setRoles(Collections.emptySet());
        updatedDto.setPassword(""); // No new password provided.

        Optional<Person> result = personService.update(personId, updatedDto);
        assertTrue(result.isPresent());
        Person updatedPerson = result.get();
        assertEquals("updatedUserNoPass", updatedPerson.getUsername());
        assertEquals("UpdatedFirst", updatedPerson.getFirstName());
        assertEquals("UpdatedLast", updatedPerson.getLastName());
        assertEquals("333333333", updatedPerson.getPhone());
        assertEquals("updatednopass@example.com", updatedPerson.getEmail());
        assertNotNull(updatedPerson.getProfessional());
        assertEquals("updated-siret", updatedPerson.getProfessional().getSiret());
        // Password should remain unchanged (original password "password123")
        assertTrue(passwordEncoder.matches("password123", updatedPerson.getPassword()));
    }

    @Test
    void testDelete() {
        personService.delete(personId);
        Optional<Person> result = personRepository.findById(personId);
        assertFalse(result.isPresent());
    }
}
