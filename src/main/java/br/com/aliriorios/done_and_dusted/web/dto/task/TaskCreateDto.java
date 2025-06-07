package br.com.aliriorios.done_and_dusted.web.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TaskCreateDto {
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Date format needs to be: yyyy-MM-dd"
    )
    private String dueDate;
}
