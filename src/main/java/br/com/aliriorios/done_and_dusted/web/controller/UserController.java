package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // POST -----------------------------------------------

    // GET ------------------------------------------------

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
