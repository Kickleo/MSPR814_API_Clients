package epsi.mspr814.kls.client.controller;

import epsi.mspr814.kls.client.dto.Mappers;
import epsi.mspr814.kls.client.dto.PersonDTO;
import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/register")
    public ResponseEntity<PersonDTO> register(@RequestBody RegisterPersonDTO registerPersonDTO) {
        Person savedPerson = personService.registerNewPerson(registerPersonDTO);
        return ResponseEntity.ok(Mappers.personToDTO(savedPerson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getById(@PathVariable UUID id) {
        Optional<Person> person = personService.getById(id);
        return person.map(value -> ResponseEntity.ok(Mappers.personToDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> update(@PathVariable UUID id, @RequestBody RegisterPersonDTO registerPersonDTO) {
        Optional<Person> updatedPerson = personService.update(id, registerPersonDTO);
        return updatedPerson.map(value -> ResponseEntity.ok(Mappers.personToDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
