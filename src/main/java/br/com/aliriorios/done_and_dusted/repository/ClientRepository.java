package br.com.aliriorios.done_and_dusted.repository;

import br.com.aliriorios.done_and_dusted.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
