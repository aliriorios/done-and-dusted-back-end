package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
}
