package br.com.aliriorios.done_and_dusted.web.dto.task;

import br.com.aliriorios.done_and_dusted.entity.enums.TaskStatus;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
}
