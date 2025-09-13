package br.com.aliriorios.done_and_dusted.ut.repository;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepositoryUnitTest {
    @Autowired
    EntityManager entityManager;

    @Autowired
    TaskRepository taskRepository;

    @Test
    @DisplayName("Test successfully when everything is OK")
    void findByIdAndClientId_Successfully() {
        Client client = this.createClient();
        Task task = this.createTask(client);

        Optional<Task> responseBody = this.taskRepository.findByIdAndClientId(client.getId(), task.getId());

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.isPresent()).isTrue();
        assertThat(responseBody.get().getName()).isEqualTo("Task test 1");
        assertThat(responseBody.get().getClient().getName()).isEqualTo("Test Client");
    }

    @Test
    @DisplayName("Test failed - Task not found")
    void findByIdAndClientId_Failed() {
        RegisterDto dto = new RegisterDto("test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Test Client");
        User user = UserMapper.toUser(dto);
        Client client = ClientMapper.toClient(dto);
        client.setUser(user);

        Optional<Task> responseBody = this.taskRepository.findByIdAndClientId(client.getId(), 1L);

        assertThat(responseBody.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Test Successfully when everything is OK - Return all pageable")
    void findAllPageable_Successfully() {
        Client client = this.createClient();

        for (int i=0; i<10; i++) {
            Task task = createTask(client);
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<Task> page = taskRepository.findAllPageable(client.getId(), pageable);
        assertThat(page.getContent().size()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(10);

        Page<Task> pageTwo = taskRepository.findAllPageable(client.getId(), PageRequest.of(1, 5));
        assertThat(page.getContent().size()).isEqualTo(5);
        assertThat(page.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("Test failed")
    void findAllPageable_Failed() {
        RegisterDto dto = new RegisterDto("test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Test Client");
        User user = UserMapper.toUser(dto);
        Client client = ClientMapper.toClient(dto);
        client.setUser(user);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Task> page = taskRepository.findAllPageable(client.getId(), pageable);
        assertThat(page.isEmpty()).isTrue();
    }

    /* Optimizing Client Creation */
    private Client createClient() {
        RegisterDto dto = new RegisterDto("test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Test Client");
        User user = UserMapper.toUser(dto);

        Client client = ClientMapper.toClient(dto);
        client.setUser(user);

        this.entityManager.persist(user);
        this.entityManager.persist(client);

        return client;
    }

    /* Optimizing Task Creation */
    private Task createTask(Client client) {
        TaskCreateDto dto = new TaskCreateDto("Task test 1", "test 1 description", "2050-01-01");

        Task task = TaskMapper.toTask(dto);
        task.setDueDate(LocalDate.parse(dto.getDueDate()));
        task.setClient(client);

        this.entityManager.persist(task);

        return task;
    }
}
