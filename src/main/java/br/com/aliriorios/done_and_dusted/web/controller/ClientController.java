package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.jwt.JwtUserDetails;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.service.UserService;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import br.com.aliriorios.done_and_dusted.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Contains all operations related to a client's resources")
@EnableMethodSecurity
public class ClientController {
    private final ClientService clientService;
    private final UserService userService;

    // GET ------------------------------------------------
    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Find a client by id", description = "Feature to find an existing client via id - Requisition requires a Bearer Token - Restricted access to ADMIN",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Client without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDto> findById (@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClientMapper.toResponseDto(client));
    }

    @GetMapping(value = "/details")
    @Operation(
            summary = "Find client data", description = "Feature to fetch authenticated user data - Requisition requires a Bearer Token - Restricted access to CLIENT",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Client without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> findDetails (@AuthenticationPrincipal JwtUserDetails userDetails) {
        Client client = clientService.findByUserId(userDetails.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClientMapper.toResponseDto(client));
    }

    // PATCH ----------------------------------------------
    @PatchMapping(value = "/update-profile")
    @Operation(
            summary = "Update client data", description = "Feature to update all client data and username (from User) - Requisition requires a Bearer Token - Restricted access to CLIENT",
            security = @SecurityRequirement(name = "Security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Update profile successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing or formatted update dto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User without permission to access this feature", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "User/Client not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> updateProfile (@AuthenticationPrincipal JwtUserDetails userDetails, @Valid @RequestBody ClientUpdateDto updateDto) {
        userService.updateUsername(userDetails.getId(), updateDto.getNewUsername());

        clientService.updateProfile(userDetails.getId(), updateDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}