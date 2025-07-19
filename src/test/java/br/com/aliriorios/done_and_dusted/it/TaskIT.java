package br.com.aliriorios.done_and_dusted.it;

import br.com.aliriorios.done_and_dusted.JwtAuthentication;
import br.com.aliriorios.done_and_dusted.web.dto.pageable.PageableDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import br.com.aliriorios.done_and_dusted.web.exception.ErrorMessage;
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

    @Test
    public void createTask_InvalidNameValidation_ReturnErrorMessageWithStatus400() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("", "Task 6 Test Description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void createTask_InvalidDueDateValidation_ReturnErrorMessageWithStatus400() {
        // Not blank
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Date is before than today
        responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", "2020-01-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Date format yyyy-MM-dd
        responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", "2020-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void createTask_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void createTask_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/tasks")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("Task 6", "Task 6 Test Description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findByClientId_SuccessfullyFindTask_ReturnTaskWithStatus200() {
        TaskResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(TaskResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(3);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Task 3");
    }

    @Test
    public void findByClientId_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/tasks/3")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void findByClientId_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findByClientId_NotFound_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/tasks/1000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findAll_SuccessfullyFind_ReturnAllWithStatus200() {
        // Default URI Pageable
        PageableDto responseBody = testClient
                .get()
                .uri("/api/v1/tasks/findAll")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(5); // Current page elements
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1); // 5 elements, 2 per page (size=2), 3 pages
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0); // Number of current page
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(10); // Number of elements per page

        // Custom URI Pageable
        responseBody = testClient
                .get()
                .uri("/api/v1/tasks/findAll?size=2&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2); // Current page elements
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(3); // 5 elements, 2 per page (size=2), 3 pages
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0); // Number of current page
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(2); // Number of elements per page
    }

    /*
    * 400 BAD_REQUEST
    * Pageable always ignores wrong parameters in the URI
    * */

    @Test
    public void findAll_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/tasks/findAll")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void findAll_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/tasks/findAll")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void update_SuccessfullyUpdateTask_ReturnStatus204() {
        // Bob (client_id=2) task_id<=5
        testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskUpdateDto("New Task 3 Name", "New task 3 description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(204);
    }

    @Test
    public void update_InvalidNameValidation_ReturnErrorMessageWithStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("", "Task 3 Test Description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void update_InvalidDueDateValidation_ReturnErrorMessageWithStatus400() {
        // Not blank
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("New Task 3 Name", "Task 3 Test Description", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Date is before than today
        responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("New Task 3 Name", "Task 3 Test Description", "2020-01-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Date invalid format
        responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskCreateDto("New Task 3 Name", "Task 3 Test Description", "2030-01"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void update_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskUpdateDto("New Task 3 Name", "New task 3 description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void update_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskUpdateDto("New Task 3 Name", "New task 3 description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void update_TaskNotFound_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new TaskUpdateDto("New Task 100 Name", "New task 100 description", "2030-01-01"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateStatusCompleted_SuccessfullyUpdate_ReturnTaskWithStatus200() {
        TaskResponseDto responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/update-status-completed/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(TaskResponseDto.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void updateStatusCompleted_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/update-status-completed/3")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void updateStatusCompleted_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/update-status-completed/3")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updateStatusCompleted_TaskNotFound_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/tasks/update-status-completed/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }
}
