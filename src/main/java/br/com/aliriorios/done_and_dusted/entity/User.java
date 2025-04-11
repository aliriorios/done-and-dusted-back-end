package br.com.aliriorios.done_and_dusted.entity;

import br.com.aliriorios.done_and_dusted.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 25)
    private Role role;

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
        User user = (User) object;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
