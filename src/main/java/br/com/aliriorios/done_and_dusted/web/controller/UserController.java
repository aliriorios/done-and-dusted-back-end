package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.service.RegisterService;
import br.com.aliriorios.done_and_dusted.service.UserService;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import br.com.aliriorios.done_and_dusted.web.dto.user.UserResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.user.UserUpdatePasswordDto;
import br.com.aliriorios.done_and_dusted.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Contains all operations related to the resources to a user's resources")
@EnableMethodSecurity
public class UserController {
    private final UserService userService;
    private final RegisterService registerService;

    // POST -----------------------------------------------
    @PostMapping
    @Operation (
            summary = "Create a new user", description = "Feature to create a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created resource", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request (invalid JSON or empty required fields)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "User e-mail already registered in the system", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    public ResponseEntity<ClientResponseDto> register(@Valid @RequestBody RegisterDto createDto) {
        ClientResponseDto response = registerService.register(createDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getUser().getId())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toString())
                .body(response);
    }

    // GET ------------------------------------------------
    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Find a user by id", description = "Feature to find an existing user via id - Requisition requires a Bearer Token - Restricted access to ADMIN or CLIENT (own client id)",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENT') AND #id == authentication.principal.id)")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserMapper.toResponseDto(user));
    }

    // PATCH ----------------------------------------------
    @PatchMapping(value = "/update-password/{id}")
    @Operation(
            summary = "Update password", description = "Update a user's password - Requisition requires a Bearer Token",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Misinformed or formatted passwords or id", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> updatePassword (@PathVariable Long id, @Valid @RequestBody UserUpdatePasswordDto dto) {
        userService.updatePassword(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    // DELETE ---------------------------------------------
    @DeleteMapping(value = "/{id}")
    @Operation(
            summary = "Delete an user", description = "Feature to delete an existing user via id - Requisition requires a Bearer Token",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful deletion"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT') AND (#id == authentication.principal.id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registerService.deleteAccount(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
