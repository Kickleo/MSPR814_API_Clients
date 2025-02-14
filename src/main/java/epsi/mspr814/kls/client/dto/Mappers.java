package epsi.mspr814.kls.client.dto;

import epsi.mspr814.kls.client.model.Person;

import java.util.ArrayList;
import java.util.HashSet;

public class Mappers {

    private Mappers() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static PersonDTO personToDTO(Person person) {
        return PersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .email(person.getEmail())
                .addresses(person.getAddresses() != null ? new ArrayList<>(person.getAddresses()) : null)  // ✅ Ensure deep copy
                .professional(person.getProfessional())
                .roles(person.getRoles() != null ? new HashSet<>(person.getRoles()) : null)  // ✅ Prevent modifications
                .build();
    }

    public static RegisterPersonDTO personToRegisterDTO(Person person) {
        return RegisterPersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .phone(person.getPhone())
                .email(person.getEmail())
                .password(person.getPassword())  // Set password here
                .addresses(person.getAddresses() != null ? new ArrayList<>(person.getAddresses()) : null)  // ✅ Ensure deep copy
                .professional(person.getProfessional())
                .roles(person.getRoles() != null ? new HashSet<>(person.getRoles()) : null)  // ✅ Prevent modifications
                .build();
    }

    public static Person dtoToPerson(PersonDTO personDTO) {
        return Person.builder()
                .id(personDTO.getId())
                .username(personDTO.getUsername())
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .phone(personDTO.getPhone())
                .email(personDTO.getEmail())
                .addresses(personDTO.getAddresses() != null ? new ArrayList<>(personDTO.getAddresses()) : null)
                .professional(personDTO.getProfessional())
                .roles(personDTO.getRoles() != null ? new HashSet<>(personDTO.getRoles()) : null)
                .build();
    }

    public static Person registerDTOToPerson(RegisterPersonDTO registerPersonDTO) {
        return Person.builder()
                .id(registerPersonDTO.getId())
                .username(registerPersonDTO.getUsername())
                .firstName(registerPersonDTO.getFirstName())
                .lastName(registerPersonDTO.getLastName())
                .phone(registerPersonDTO.getPhone())
                .email(registerPersonDTO.getEmail())
                .password(registerPersonDTO.getPassword())
                .addresses(registerPersonDTO.getAddresses() != null ? new ArrayList<>(registerPersonDTO.getAddresses()) : null)
                .professional(registerPersonDTO.getProfessional())
                .roles(registerPersonDTO.getRoles() != null ? new HashSet<>(registerPersonDTO.getRoles()) : null)
                .build();
    }
}
