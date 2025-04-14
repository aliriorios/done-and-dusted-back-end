package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.service.RegisterService;
import br.com.aliriorios.done_and_dusted.service.UserService;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Api error - ", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    // GET ------------------------------------------------

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
