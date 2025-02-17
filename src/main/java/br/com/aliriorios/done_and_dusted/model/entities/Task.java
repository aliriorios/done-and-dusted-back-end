package br.com.aliriorios.done_and_dusted.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @EqualsAndHashCode.Include
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @NonNull
    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @NonNull
    @Column(nullable = false)
    private LocalDate dueDate;

    //Enum (priority and status)

    @OneToOne
    @JoinColumn(name = "user_id")
    @NonNull
    private Users user;
}
