package br.com.aliriorios.done_and_dusted.repository;

import br.com.aliriorios.done_and_dusted.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
