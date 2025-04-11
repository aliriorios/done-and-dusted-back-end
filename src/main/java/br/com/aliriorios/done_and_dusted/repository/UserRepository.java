package br.com.aliriorios.done_and_dusted.repository;

import br.com.aliriorios.done_and_dusted.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
