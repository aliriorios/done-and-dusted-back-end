package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.Task;
import br.com.aliriorios.done_and_dusted.jwt.JwtUserDetails;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.service.TaskService;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.TaskMapper;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskCreateDto;
import br.com.aliriorios.done_and_dusted.web.dto.task.TaskResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        Task task = TaskMapper.toTask(createDto);
        task.setClient(client);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TaskMapper.toResponseDto(taskService.save(task)));
    }

    // GET ------------------------------------------------

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
