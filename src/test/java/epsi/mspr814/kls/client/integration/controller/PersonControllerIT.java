package epsi.mspr814.kls.client.integration.controller;

import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.security.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Transactional
class PersonControllerIT {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void testCreatePerson() throws Exception {
        String personJson = """
        {
            "username": "john_doe",
            "firstName": "John",
            "lastName": "Doe",
            "phone": "123456789",
            "email": "john@example.com",
            "password": "password"
        }
        """;

        mockMvc.perform(post("/api/clients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(personJson))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void testGetPersonById() throws Exception {
        Person person = new Person(null, "jane_doe", "Jane", "Doe", "987654321", "jane@example.com", "password", null, null, null);

        person = personRepository.save(person);

        person = personRepository.findById(person.getId()).orElseThrow(() -> new RuntimeException("Person not found"));

        mockMvc.perform(get("/api/clients/" + person.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane_doe"));
    }

    @Test
    void testGetPersonById_NotFound() throws Exception {
        mockMvc.perform(get("/api/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePerson() throws Exception {
        Person person = new Person(null, "jane_doe", "Jane", "Doe", "987654321", "jane@example.com", "password", null, null, null);
        person = personRepository.save(person);

        String updatedJson = "{\"username\": \"jane_updated\", \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"phone\": \"987654321\", \"email\": \"jane@example.com\", \"password\": \"password\"}";
        mockMvc.perform(put("/api/clients/" + person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane_updated"));
    }

    @Test
    void testDeletePerson() throws Exception {
        Person person = new Person(null, "delete_me", "Delete", "Me", "111222333", "delete@example.com", "password", null, null, null);
        person = personRepository.save(person);

        mockMvc.perform(delete("/api/clients/" + person.getId()))
                .andExpect(status().isNoContent());
    }
}
