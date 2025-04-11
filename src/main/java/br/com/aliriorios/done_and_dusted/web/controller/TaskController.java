package br.com.aliriorios.done_and_dusted.web.controller;

import br.com.aliriorios.done_and_dusted.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // POST -----------------------------------------------

    // GET ------------------------------------------------

    // PUT ------------------------------------------------

    // PATCH ----------------------------------------------

    // DELETE ---------------------------------------------
}
