package br.com.aliriorios.done_and_dusted.web.dto.client;

import br.com.aliriorios.done_and_dusted.web.dto.user.UserResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientResponseDto {
    private Long id;
    private String name;
    private LocalDate birthday;
    private String phone;
    private String rg;
    private String cpf;

    private UserResponseDto user;
}
