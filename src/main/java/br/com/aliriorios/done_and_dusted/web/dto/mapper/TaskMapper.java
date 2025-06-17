package br.com.aliriorios.done_and_dusted.web.dto.mapper;

import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static Task toTask(TaskCreateDto createDto) {
        return mapper.map(createDto, Task.class);
    }

    public static TaskResponseDto toResponseDto(Task task) {
        return mapper.map(task, TaskResponseDto.class);
    }
}
