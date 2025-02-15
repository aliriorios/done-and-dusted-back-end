package br.com.aliriorios.done_and_dusted.model.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Users {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NonNull
    @ToString.Include
    private String name;

    @NonNull
    @ToString.Include
    private String lastName;

    @NonNull
    @ToString.Include
    private String email;

    @NonNull
    private String hashedPassword;

    private LocalDate birthday;
    private String phone;
    private String rg;
    private String cpf;

    @PrePersist
    private void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
