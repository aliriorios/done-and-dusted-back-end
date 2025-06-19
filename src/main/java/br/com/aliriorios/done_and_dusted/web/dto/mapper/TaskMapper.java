package br.com.aliriorios.done_and_dusted.web.dto.mapper;

import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static Task toTask(TaskCreateDto createDto) {
        return mapper.map(createDto, Task.class);
    }

    public static TaskResponseDto toResponseDto(Task task) {
        return mapper.map(task, TaskResponseDto.class);
    }

    public static Page<TaskResponseDto> toPageResponseDto(Page<Task> taskList) {
        return taskList.map(task ->
                mapper.map(task, TaskResponseDto.class)
        );
    }

    public static void updateFromDto(Task task, TaskUpdateDto updateDto) {
        if (updateDto.getDescription().isEmpty()) {
            updateDto.setDescription(task.getDescription());
        }

        task.setDueDate(LocalDate.parse(updateDto.getDueDate()));

        mapper.map(updateDto, task);
    }
}
