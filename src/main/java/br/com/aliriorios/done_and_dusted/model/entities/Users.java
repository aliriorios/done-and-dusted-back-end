package br.com.aliriorios.done_and_dusted.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@RequiredArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Users {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @NonNull
    @ToString.Include
    @Column(nullable = false, length = 100)
    private String name;

    @NonNull
    @ToString.Include
    @Column(nullable = false, length = 100)
    private String lastName;

    @NonNull
    @ToString.Include
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String hashedPassword;

    private LocalDate birthday;

    @Column(length = 15)
    private String phone;

    @Column(length = 20, unique = true)
    private String rg;

    @Column(length = 14, unique = true)
    private String cpf;
}
