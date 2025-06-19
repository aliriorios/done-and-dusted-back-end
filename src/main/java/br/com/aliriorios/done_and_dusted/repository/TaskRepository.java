package br.com.aliriorios.done_and_dusted.repository;

import br.com.aliriorios.done_and_dusted.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t")
    Page<Task> findAllPageable(Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Task")
    void deleteAllTasks();
}
