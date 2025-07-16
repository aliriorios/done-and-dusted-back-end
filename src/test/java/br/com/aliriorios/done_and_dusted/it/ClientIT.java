package br.com.aliriorios.done_and_dusted.it;

import br.com.aliriorios.done_and_dusted.JwtAuthentication;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
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
public class ClientIT {
    @Autowired
    WebTestClient testClient;

    @Test
    public void findById_SuccessfullyFindClient_ReturnClientWithStatus200() {
        ClientResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/clients/5")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(5);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Edward");
    }

    @Test
    public void findById_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/5")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void findById_ForbiddenClient_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/5")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void findById_ClientNotFound_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/1000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void findDetailsByUserId_SuccessfullyFindClient_ReturnClientWithStatus200() {
        ClientResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(200)
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(5);
        org.assertj.core.api.Assertions.assertThat(responseBody.getName()).isEqualTo("Edward");
    }

    @Test
    public void findDetailsByUserId_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }

    @Test
    public void findDetailsByUserId_ForbiddenUser_ReturnErrorMessageWithStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clients/details")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updateProfile_SuccessfullyUpdate_ReturnStatus204() {
        testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "545454545", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(204);
    }

    @Test
    public void updateProfile_InvalidNameValidation_ReturnErrorMessageWithStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("", "2005-05-05", "74999555555", "545454545", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_InvalidBirthdayValidation_ReturnErrorMessageWithStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05", "74999555555", "545454545", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_InvalidPhoneValidation_ReturnErrorMessageWithStatus400() {
        // Phone pattern (less than)
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74", "545454545", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // Phone pattern (more than)
        responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "749995555550000", "545454545", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_InvalidRgValidation_ReturnErrorMessageWithStatus400() {
        // RG pattern (less than)
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "54", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // RG pattern (more than)
        responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "5454545450000", "54545454545", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_InvalidCpfValidation_ReturnErrorMessageWithStatus400() {
        // RG pattern (less than)
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "545454545", "54", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // RG pattern (more than)
        responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "545454545", "545454545450000", "newEdward@email.com"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_InvalidNewUsernameValidation_ReturnErrorMessageWithStatus400() {
        // NewUsername blank
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "545454545", "54545454545", ""))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        // NewUsername pattern
        responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "edward@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientUpdateDto("Edward", "2005-05-05", "74999555555", "545454545", "54545454545", "newEdward@"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void updateProfile_UnauthorizedUser_ReturnErrorMessageWithStatus401() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/clients/update-profile")
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(401);
    }


}
