package br.com.aliriorios.done_and_dusted.ut.repository;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.repository.ClientRepository;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientRepositoryUnitTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    ClientRepository clientRepository;

    @Test
    @DisplayName("Should return Client successfully from DB by User Id")
    void findByUserId_Successfully() {
        RegisterDto dto = new RegisterDto("test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Test Client");
        User user = this.createEntities(dto);

        Optional<Client> responseBody = this.clientRepository.findByUserId(user.getId());

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.isPresent()).isTrue();
        assertThat(responseBody.get().getName()).isEqualTo("Test Client");
    }

    @Test
    @DisplayName("Test failed and should not return User")
    void findByUserId_Failed() {
        RegisterDto dto = new RegisterDto("test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Test Client");
        User user = UserMapper.toUser(dto);

        Optional<Client> responseBody = this.clientRepository.findByUserId(user.getId());

        assertThat(responseBody.isEmpty()).isTrue();
    }

    /* Insert User and Client */
    private User createEntities(RegisterDto dto) {
        User user = UserMapper.toUser(dto);
        this.entityManager.persist(user);

        Client client = ClientMapper.toClient(dto);
        client.setUser(user);
        this.entityManager.persist(client);

        this.entityManager.flush();
        return user;
    }
}
