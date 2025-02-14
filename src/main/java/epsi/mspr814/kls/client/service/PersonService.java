package epsi.mspr814.kls.client.service;

import epsi.mspr814.kls.client.dto.PersonDTO;
import epsi.mspr814.kls.client.dto.RegisterPersonDTO;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> getAll() { return personRepository.findAll(); }

    public Person registerNewPerson(RegisterPersonDTO registerPersonDTO) {
        Person person = new Person();
        person.setUsername(registerPersonDTO.getUsername());
        person.setFirstName(registerPersonDTO.getFirstName());
        person.setLastName(registerPersonDTO.getLastName());
        person.setPhone(registerPersonDTO.getPhone());
        person.setEmail(registerPersonDTO.getEmail());

        // Hash the password in the service layer
        person.setPassword(passwordEncoder.encode(registerPersonDTO.getPassword()));
        person.setAddresses(registerPersonDTO.getAddresses());
        person.setProfessional(registerPersonDTO.getProfessional());
        person.setRoles(registerPersonDTO.getRoles());

        return personRepository.save(person);
    }

    public Optional<Person> getById(UUID id) { return personRepository.findById(id); }
    public Optional<Person> getByUsername(String username) { return personRepository.findByUsername(username); }

    public Optional<Person> update(UUID id, RegisterPersonDTO updatedPerson) {
        return personRepository.findById(id).map(person -> {
            person.setUsername(updatedPerson.getUsername());
            person.setFirstName(updatedPerson.getFirstName());
            person.setLastName(updatedPerson.getLastName());
            person.setPhone(updatedPerson.getPhone());
            person.setEmail(updatedPerson.getEmail());
            person.setAddresses(updatedPerson.getAddresses());
            person.setProfessional(updatedPerson.getProfessional());
            person.setRoles(updatedPerson.getRoles());

            if (updatedPerson.getPassword() != null && !updatedPerson.getPassword().isEmpty()) {
                person.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));
            }

            return personRepository.save(person);
        });
    }

    public void delete(UUID id) { personRepository.deleteById(id); }
}