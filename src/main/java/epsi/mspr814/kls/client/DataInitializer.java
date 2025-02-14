package epsi.mspr814.kls.client;

import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DataInitializer implements CommandLineRunner {

    Logger logger = Logger.getLogger(getClass().getName());

    private final PersonRepository personRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(PersonRepository personRepository, BCryptPasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if(personRepository.findByUsername("user").isEmpty()){
            Person person = new Person();
            person.setUsername("user");

            person.setPassword(passwordEncoder.encode("password"));
            personRepository.save(person);
            logger.info("Utilisateur 'user' créé avec le mot de passe 'password' (encrypté avec BCrypt).");
        }
    }
}