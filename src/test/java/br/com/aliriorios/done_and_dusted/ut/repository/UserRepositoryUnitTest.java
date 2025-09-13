package br.com.aliriorios.done_and_dusted.ut.repository;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.entity.enums.Role;
import br.com.aliriorios.done_and_dusted.repository.UserRepository;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest // Default: Database in memory (H2)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Uses the application-test
public class UserRepositoryUnitTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Should return User successfully from DB")
    void findByUsername_Successfully() { // JPA already tests
        RegisterDto dto = new RegisterDto("bob@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Bob Test");
        this.createUser(dto);

        Optional<User> responseBody = this.userRepository.findByUsername(dto.getUsername());

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.isPresent()).isTrue();
        assertThat(responseBody.get().getUsername()).isEqualTo("bob@email.com");
    }

    @Test
    @DisplayName("Test failed and should not return User from DB")
    void findByUsername_Failed() {
        RegisterDto dto = new RegisterDto("bob@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Bob Test");

        Optional<User> responseBody = this.userRepository.findByUsername(dto.getUsername());

        assertThat(responseBody.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return the user role that the username was sent")
    void findRoleByUsername_Successfully() { // Contains @Query, so testing is mandatory
        RegisterDto dto = new RegisterDto("bob@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Bob Test");
        this.createUser(dto);

        // Forces persistence and synchronization to the DB
        entityManager.flush();
        entityManager.clear();

        Role response = this.userRepository.findRoleByUsername(dto.getUsername());

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(Role.ROLE_CLIENT);
    }

    @Test
    @DisplayName("Test failed, should not return anything or at least one not found")
    void findRoleByUsername_Failed() {
        RegisterDto dto = new RegisterDto("bob@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Bob Test");

        Role response = this.userRepository.findRoleByUsername(dto.getUsername());

        assertThat(response).isNull();
    }

    /*
    * Method for creating the user that will be used in testing
    * An alternative way to insert without using .sql files
    */
    private void createUser(RegisterDto dto) {
        User newUser = UserMapper.toUser(dto);
        this.entityManager.persist(newUser);
    }
}
