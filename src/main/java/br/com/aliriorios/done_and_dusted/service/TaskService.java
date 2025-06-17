package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
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

    // GET ------------------------------------------------
    @Transactional(readOnly = true)
    public Task findById (Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Task [id=%s] not founded", id))
        );
    }

    // DELETE ---------------------------------------------
    @Transactional
    public void delete(Long id) {
        taskRepository.delete(findById(id));
    }

    @Transactional
    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }
}
