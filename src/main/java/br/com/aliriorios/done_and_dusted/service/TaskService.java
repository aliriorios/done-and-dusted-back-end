package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.entity.enums.TaskStatus;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import br.com.aliriorios.done_and_dusted.repository.projection.TaskProjection;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    @Transactional
    public Page<TaskResponseDto> findAll (Pageable pageable) {
        Page<Task> taskList = taskRepository.findAllPageable(pageable);

        for (Task t : taskList) {
            taskUpdateStatus(t);
        }

        return TaskMapper.toPageResponseDto(taskList);
    }

    // PATCH ----------------------------------------------
    @Transactional
    public void updateTask(Long id, TaskUpdateDto updateDto) {
        Task task = findById(id);
        TaskMapper.updateFromDto(task, updateDto);
    }

    @Transactional
    public Task taskUpdateStatusCompleted(Long id) {
        Task task = findById(id);
        task.setStatus(TaskStatus.COMPLETED);
        return task;
    }

    @Transactional
    private void taskUpdateStatus(Task task) { // CALLING ON FIND ALL
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, task.getDueDate());

        if (daysLeft <= 5) {
            task.setStatus(TaskStatus.DUE_SOON);
        }

        if (task.getDueDate().isBefore(today)) {
            task.setStatus(TaskStatus.OVERDUE);
        }
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
