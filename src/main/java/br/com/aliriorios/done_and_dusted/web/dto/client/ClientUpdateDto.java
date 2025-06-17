package br.com.aliriorios.done_and_dusted.web.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ClientUpdateDto {
    @NotBlank
    private String name;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Date format needs to be: yyyy-MM-dd"
    )
    private String birthday;


    @Pattern(
            regexp = "^(|\\d{11}$)",
            message = "Phone must contain exactly 11 numeric digits"
    )
    private String phone;

    @Pattern(
            regexp = "^(|\\d{9}$)",
            message = "CPF must contain exactly 11 numeric digits"
    )
    private String rg;

    @Pattern(
            regexp = "^(|\\d{11}$)",
            message = "CPF must contain exactly 11 numeric digits"

    )
    private String cpf;

    @NotBlank
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", message = "E-mail format is invalid.")
    private String newUsername;

    // IMPLEMENTAR UMA FORMA DE NÃO SUBSTITUIR OS DADOS DO BANCO CASO VENHA "" NO DTO
}
