package br.com.aliriorios.done_and_dusted.ut.service;

import br.com.aliriorios.done_and_dusted.repository.TaskRepository;
import br.com.aliriorios.done_and_dusted.service.TaskService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;


}
