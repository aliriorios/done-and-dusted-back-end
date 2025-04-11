package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    // POST -----------------------------------------------

    // GET ------------------------------------------------

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
