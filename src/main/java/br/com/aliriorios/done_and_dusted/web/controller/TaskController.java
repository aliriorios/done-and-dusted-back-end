package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.jwt.JwtUserDetails;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.service.TaskService;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.PageableMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.pageable.PageableDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskUpdateDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping(value = "/{taskId}")
    public ResponseEntity<TaskResponseDto> findByClientId(@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        Task task = taskService.findByIdAndClientId(clientService.findByUserId(userDetails.getId()).getId(), taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<PageableDto> findAll(@AuthenticationPrincipal JwtUserDetails userDetails, @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        Page<TaskResponseDto> taskList = taskService.findAll(clientService.findByUserId(userDetails.getId()).getId(), pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageableMapper.toPageableDto(taskList));
    }

    // PATCH ----------------------------------------------
    @PatchMapping(value = "/{taskId}")
    public ResponseEntity<Void> update (@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId, @Valid @RequestBody TaskUpdateDto updateDto) {
        taskService.updateTask(clientService.findById(userDetails.getId()).getId(), taskId, updateDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping(value = "/update-status-completed/{id}")
    public ResponseEntity<TaskResponseDto> taskUpdateStatusCompleted (@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        Task task = taskService.taskUpdateStatusCompleted(clientService.findById(userDetails.getId()).getId(), taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    // DELETE ---------------------------------------------
    @DeleteMapping(value = "/{taskId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        taskService.delete(clientService.findById(userDetails.getId()).getId(), taskId);
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
