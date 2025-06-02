package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Contains all operations related to a client's resources")
public class ClientController {
    private final ClientService clientService;

    // GET ------------------------------------------------
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientResponseDto> findById (@PathVariable Long id) {
        Optional<Client> response = Optional.ofNullable(clientService.findById(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ClientMapper.toResponseDto(response.get()));
    }

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
