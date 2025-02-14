package epsi.mspr814.kls.client.dto;

import epsi.mspr814.kls.client.model.Address;
import epsi.mspr814.kls.client.model.Professional;
import epsi.mspr814.kls.client.model.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
public class PersonDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private List<Address> addresses;
    private Professional professional;
    private Set<Role> roles;
}