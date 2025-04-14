package br.com.aliriorios.done_and_dusted.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RegisterDto {
    @NotBlank
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[a-z0-9..-]+\\.[a-z]{2,}$", message = "E-mail format is invalid.")
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String clientName;
}
