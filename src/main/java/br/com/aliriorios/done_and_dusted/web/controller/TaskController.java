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
import br.com.aliriorios.done_and_dusted.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@RestController
@RequestMapping(value = "/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Contains all of a user's task-related operations")
@EnableMethodSecurity
public class TaskController {
    private final TaskService taskService;
    private final ClientService clientService;

    // POST -----------------------------------------------
    @PostMapping
    @Operation(
            summary = "Create a new task", description = "Feature to create a new task - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created resource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request (invalid JSON or empty required fields)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
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
    @Operation(
            summary = "Find a task by client id", description = "Searches only for tasks that belong to the user who is logged in - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Task or User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TaskResponseDto> findByClientId(@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        Task task = taskService.findByIdAndClientId(clientService.findByUserId(userDetails.getId()).getId(), taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    @GetMapping(value = "/findAll")
    @Operation(
            summary = "Find all task", description = "Finds all user tasks in a paginated way, limiting the amount of data at a time - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            parameters = {
                    @Parameter(in = QUERY, name = "size", description = "Represents the total number of elements (default value = 10)"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "All tasks found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageableDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request (invalid sort value)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<PageableDto> findAll(@AuthenticationPrincipal JwtUserDetails userDetails, @Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        Page<TaskResponseDto> taskList = taskService.findAll(clientService.findByUserId(userDetails.getId()).getId(), pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageableMapper.toPageableDto(taskList));
    }

    // PATCH ----------------------------------------------
    @PatchMapping(value = "/{taskId}")
    @Operation(
            summary = "Update task", description = "Feature to update specific data for an existing task - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing or formatted update dto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> update (@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId, @Valid @RequestBody TaskUpdateDto updateDto) {
        taskService.updateTask(clientService.findById(userDetails.getId()).getId(), taskId, updateDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping(value = "/update-status-completed/{id}")
    @Operation(
            summary = "Task completed", description = "Updates the task when it is completed - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TaskResponseDto> taskUpdateStatusCompleted (@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        Task task = taskService.taskUpdateStatusCompleted(clientService.findById(userDetails.getId()).getId(), taskId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TaskMapper.toResponseDto(task));
    }

    // DELETE ---------------------------------------------
    @DeleteMapping(value = "/{taskId}")
    @Operation(
            summary = "Delete a task", description = "Feature to delete an existing task - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful deletion"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal JwtUserDetails userDetails, @PathVariable Long taskId) {
        taskService.delete(clientService.findById(userDetails.getId()).getId(), taskId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping
    @Operation(
            summary = "Delete all tasks", description = "Deletes all tasks for a user - Request requires the use of a Bearer JWT Token - Restricted CLIENT access",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful deletion"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> deleteAllTasks() {
        taskService.deleteAllTasks();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
