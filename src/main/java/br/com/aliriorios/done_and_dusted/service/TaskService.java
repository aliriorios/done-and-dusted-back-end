package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.entity.enums.TaskStatus;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    @Transactional
    public Task findByIdAndClientId (Long clientId, Long taskId) {
        Task task = taskRepository.findByIdAndClientId(clientId, taskId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Task [id=%s] not found for Client [id=%s]", taskId, clientId))
        );

        taskUpdateStatus(task);

        return task;
    }

    @Transactional
    public Page<TaskResponseDto> findAll (Long clientId, Pageable pageable) {
        Page<Task> taskList = taskRepository.findAllPageable(clientId, pageable);

        for (Task t : taskList) {
            if (!t.getClient().getId().equals(clientId)) {
                throw new AccessDeniedException("Access denied! This task does not belong to that user|client");
            }

            taskUpdateStatus(t);
        }

        return TaskMapper.toPageResponseDto(taskList);
    }

    // PATCH ----------------------------------------------
    @Transactional
    public void updateTask(Long clientId, Long taskId, TaskUpdateDto updateDto) {
        LocalDate dueDate = LocalDate.parse(updateDto.getDueDate());

        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The date of the task must be present or future");
        }

        updateDto.setDueDate(dueDate.toString());

        Task task = findByIdAndClientId(clientId, taskId);
        TaskMapper.updateFromDto(task, updateDto);
    }

    @Transactional
    public Task taskUpdateStatusCompleted(Long clientId, Long taskId) {
        Task task = findByIdAndClientId(clientId, taskId);
        task.setStatus(TaskStatus.COMPLETED);
        return task;
    }

    @Transactional
    private void taskUpdateStatus(Task task) { // CALLING ON FIND ALL
        LocalDate today = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(today, task.getDueDate());

        if (task.getStatus() != TaskStatus.COMPLETED) {
            if (daysLeft <= 5) {
                task.setStatus(TaskStatus.DUE_SOON);
            }

            if (task.getDueDate().isBefore(today)) {
                task.setStatus(TaskStatus.OVERDUE);
            }
        }
    }

    // DELETE ---------------------------------------------
    @Transactional
    public void delete(Long clientId, Long taskId) {
        taskRepository.delete(findByIdAndClientId(clientId, taskId));
    }

    @Transactional
    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }
}
