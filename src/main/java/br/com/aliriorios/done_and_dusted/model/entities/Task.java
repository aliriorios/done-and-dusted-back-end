package br.com.aliriorios.done_and_dusted.model.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @Id
    @EqualsAndHashCode.Include
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NonNull
    private String name;

    private String description;

    @NonNull
    private LocalDate dueDate;

    //Enum (priority and status)

    @OneToOne
    @JoinColumn(name = "user_id")
    @NonNull
    private Users user;

    @PrePersist
    private void generateId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
