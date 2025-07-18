package br.com.aliriorios.done_and_dusted.it;

import br.com.aliriorios.done_and_dusted.JwtAuthentication;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/tasks/tasks-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TaskIT {
    @Autowired
    WebTestClient testClient;

    @Test
    public void createTask_SuccessfullySave_ReturnStatus201() {
        TaskResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(201)
                .expectBody(TaskResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(46);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Task 6");
    }
}
