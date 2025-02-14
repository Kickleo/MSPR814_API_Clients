package epsi.mspr814.kls.client.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RegisterPersonDTO extends PersonDTO {
    private String password; // Only used for registration
}