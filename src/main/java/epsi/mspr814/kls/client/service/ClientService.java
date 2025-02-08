package epsi.mspr814.kls.client.service;

import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private PersonRepository personRepository;
   
    public Optional<Person> getPerson(final Long id) {
        return personRepository.findById(id);
    }

    public Iterable<Person> getPersons() {
        return personRepository.findAll();
    }

    public void deletePerson(final Long id) {
        personRepository.deleteById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

}