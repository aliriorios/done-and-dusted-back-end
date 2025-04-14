package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.service.RegisterService;
import br.com.aliriorios.done_and_dusted.service.UserService;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import br.com.aliriorios.done_and_dusted.web.dto.user.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final RegisterService registerService;

    // POST -----------------------------------------------
    @PostMapping
    public ResponseEntity<ClientResponseDto> register(@Valid @RequestBody RegisterDto createDto) {
        try {
            ClientResponseDto response = registerService.register(createDto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(HttpHeaders.LOCATION, location.toString())
                    .body(response);

        } catch (Exception e) {
            log.error("Api error - ", e);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .build();
        }
    }

    // GET ------------------------------------------------
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        User response = userService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserMapper.toResponseDto(response));
    }

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
