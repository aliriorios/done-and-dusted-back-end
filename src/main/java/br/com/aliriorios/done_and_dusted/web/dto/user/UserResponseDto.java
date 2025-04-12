package br.com.aliriorios.done_and_dusted.web.dto.user;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserResponseDto {
    private Long id;
    private String username;
    private String role;
}
