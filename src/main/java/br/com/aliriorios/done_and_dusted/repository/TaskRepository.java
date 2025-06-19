package br.com.aliriorios.done_and_dusted.repository;

import br.com.aliriorios.done_and_dusted.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select t from Task t where t.id = :taskId and t.client.id = :clientId")
    Optional<Task> findByIdAndClientId(@Param("clientId") Long clientId, @Param("taskId") Long taskId);

    @Query("select t from Task t where t.client.id = :clientId")
    Page<Task> findAllPageable(@Param("clientId") Long clientId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Task")
    void deleteAllTasks();
}
