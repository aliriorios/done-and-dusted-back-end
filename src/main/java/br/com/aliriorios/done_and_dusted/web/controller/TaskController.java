package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.entity.enums.TaskStatus;
import br.com.aliriorios.done_and_dusted.jwt.JwtUserDetails;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.service.TaskService;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/tasks")
@RequiredArgsConstructor
@EnableMethodSecurity
public class TaskController {
    private final TaskService taskService;
    private final ClientService clientService;

    // POST -----------------------------------------------
    @PostMapping
    public ResponseEntity<TaskResponseDto> save(@AuthenticationPrincipal JwtUserDetails userDetails, @Valid @RequestBody TaskCreateDto createDto) {
        Client client = clientService.findByUserId(userDetails.getId());
        Task task = taskService.save(client, createDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toString())
                .body(TaskMapper.toResponseDto(task));
    }

    // GET ------------------------------------------------
    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskResponseDto> findById(@PathVariable Long id) {
        Task task = taskService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    // PATCH ----------------------------------------------
    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> update (@PathVariable Long id, @Valid @RequestBody TaskUpdateDto updateDto) {
        taskService.updateTask(id, updateDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping(value = "/update-status-completed/{id}")
    public ResponseEntity<TaskResponseDto> taskUpdateStatusCompleted (@PathVariable Long id) {
        Task task = taskService.taskUpdateStatusCompleted(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    // DELETE ---------------------------------------------
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTasks() {
        taskService.deleteAllTasks();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
