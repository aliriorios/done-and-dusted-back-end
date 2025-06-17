package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.entity.enums.Role;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.exception.PasswordInvalidException;
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

    // POST -----------------------------------------------
    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new UsernameUniqueViolationException(String.format("Username [%s] already registered", user.getUsername()));
        }
    }

    // GET -----------------------------------------------
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User [id=%s] not founded.", id))
        );
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User [%s] not founded.", username))
        );
    }

    @Transactional(readOnly = true)
    public Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }

    // PATCH ----------------------------------------------
    @Transactional
    public void updateUsername(Long id, String newUsername) {
        User user = findById(id);
        user.setUsername(newUsername);
    }

    @Transactional
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("New password does not match password confirmation.");
        }

        User user = findById(id);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("The password entered does not match the user's");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    // DELETE ---------------------------------------------
    @Transactional
    public void deleteById(Long id) {
        User user = findById(id);
        userRepository.deleteById(user.getId());
    }
}
