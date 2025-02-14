package epsi.mspr814.kls.client.controller;

import epsi.mspr814.kls.client.model.AuthRequest;
import epsi.mspr814.kls.client.model.AuthResponse;
import epsi.mspr814.kls.client.model.Person;
import epsi.mspr814.kls.client.repository.PersonRepository;
import epsi.mspr814.kls.client.service.JWTService;
import epsi.mspr814.kls.client.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final PersonService personService;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public LoginController(JWTService jwtService, PersonService personService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtService = jwtService;
        this.personService = personService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        Optional<Person> personOptional = personService.getByUsername(authRequest.getUsername());
        if (personOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur inconnu");
        }

        Person person = personOptional.get();

        if (!bCryptPasswordEncoder.matches(authRequest.getPassword(), person.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(person.getUsername())));
    }
}