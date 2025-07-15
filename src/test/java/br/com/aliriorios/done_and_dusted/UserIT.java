package br.com.aliriorios.done_and_dusted;

import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.user.UserResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.user.UserUpdatePasswordDto;
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
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {
    @Autowired
    WebTestClient testClient;

    @Test
    public void createUser_UsernameAndPasswordValidate_ReturnCreatedUserWithStatus201() {
        UserResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("tody@email.com", "123456", "Tody Silva"))
                .exchange()
                .expectStatus().isEqualTo(201)
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody().getUser();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("tody@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

    @Test
    public void createUser_InvalidUserName_ReturnMessageErrorWithStatus400() {
        // Empty username
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("", "123456", "Tody Silva"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Username invalid format
        responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("tody@", "123456", "Tody Silva"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void createUser_InvalidPassword_ReturnMessageErrorWithStatus400() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("tody@email.com", "", "Tody Silva"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void createUser_InvalidClientName_ReturnMessageErrorWithStatus400() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("tody@email.com", "123456", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void createUser_InvalidUsernameDuplicateConflict_ReturnMessageErrorWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RegisterDto("bob@email.com", "123456", "Tody Silva"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void findById_SuccessfullyTest_ReturnUserWithStatus200() {
        // ADMIN can find any user
        UserResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/users/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("julia@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");

        // CLIENT can find only own user
        responseBody = testClient
                .get()
                .uri("/api/v1/users/10")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "julia@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("julia@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

    @Test
    public void findById_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/10")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void findById_ForbiddenUserClientSearchOtherClient_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/2")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "julia@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findById_NotFound_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updatePassword_SuccessfullyUpdated_ReturnStatus204() {
        // ADMIN
        testClient
                .patch()
                .uri("/api/v1/users/update-password/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(204);

        // CLIENT
        testClient
                .patch()
                .uri("/api/v1/users/update-password/2")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(204);
    }

    @Test
    public void updatePassword_WrongOrEmptyPassword_ReturnErrorMessageWithStatus400() {
        // Unmatched current password
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("000000", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Unmatched confirm password
        responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", "000000"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updatePassword_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/update-password/2")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void updatePassword_ForbiddenUserOwnId_ReturnErrorMessageWithStatus403() {
        // ADMIN
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/5")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456")) // admin id == 1
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        // CLIENT
        responseBody = testClient
                .patch()
                .uri("/api/v1/users/update-password/5")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "julia@email.com", "123456")) // julia id == 10
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserUpdatePasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void delete_SuccessfullyDeleted_ReturnStatus204() {
        // ADMIN
        testClient
                .delete()
                .uri("/api/v1/users/1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(204);
    }
}
