package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    // POST -----------------------------------------------
    @Transactional
    public Task save(Client client, TaskCreateDto createDto) {
        LocalDate dueDate = LocalDate.parse(createDto.getDueDate());

        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The date of the task must be present or future");
        }

        Task task = TaskMapper.toTask(createDto);
        task.setClient(client);
        task.setDueDate(dueDate);

        return taskRepository.save(task);
    }
}
