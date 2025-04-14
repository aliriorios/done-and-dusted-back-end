package br.com.aliriorios.done_and_dusted.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
@Getter @Setter @NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "birthday", length = 10)
    private LocalDate birthday;

    @Column(name = "phone", length = 11)
    private String phone;

    @Column(name = "rg", unique = true, length = 9)
    private String rg;

    @Column(name = "cpf", unique = true, length = 11)
    private String cpf;

    // Association ----------------------------------------
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Aggregation ----------------------------------------
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskList;

    // Auditor Aware fields -------------------------------
    @Column(name = "created_date")
    @Setter(AccessLevel.NONE)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(name = "created_by")
    @Setter(AccessLevel.NONE)
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @Setter(AccessLevel.NONE)
    @LastModifiedBy
    private String modifiedBy;

    // Equals and HashCode --------------------------------
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Client client = (Client) object;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
