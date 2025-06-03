package br.com.aliriorios.done_and_dusted.web.dto.client;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ClientUpdateDto {
    @NotBlank
    private String name;

    private LocalDate birthday;
    private String phone;
    private String rg;
    private String cpf;
}
