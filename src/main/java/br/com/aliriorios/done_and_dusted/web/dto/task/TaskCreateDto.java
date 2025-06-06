package br.com.aliriorios.done_and_dusted.web.dto.task;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TaskCreateDto {
    @NotBlank
    private String name;

    private String description;

    @NotNull // Refazer a validação
    @FutureOrPresent(message = "The date of the task must be present or future")
    private LocalDate dueDate;
}
