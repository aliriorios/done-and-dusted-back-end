package br.com.aliriorios.done_and_dusted.repository.projection;

import br.com.aliriorios.done_and_dusted.entity.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface TaskProjection {
    Long getId();
    String getName();
    String getDescription();

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate getDueDate();
    TaskStatus getStatus();
}
