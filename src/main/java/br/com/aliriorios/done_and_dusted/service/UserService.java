package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.entity.enums.Role;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.exception.UsernameUniqueViolationException;
import br.com.aliriorios.done_and_dusted.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new UsernameUniqueViolationException(String.format("Username [%s] already registered", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User [%s] not founded.", username))
        );
    }

    public Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
