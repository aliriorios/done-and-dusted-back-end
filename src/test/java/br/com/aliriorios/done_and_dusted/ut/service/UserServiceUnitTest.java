package br.com.aliriorios.done_and_dusted.ut.service;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.exception.UsernameUniqueViolationException;
import br.com.aliriorios.done_and_dusted.repository.UserRepository;
import br.com.aliriorios.done_and_dusted.service.UserService;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock // Emulates dependencies | Don't retort anything | They do nothing | Less cost to the project
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Injects the MOCKS into the actual dependencies for this scenario
    private UserService userService;

    @Test
    @DisplayName("Test performed successfully when everything is OK")
    void save_Successfully() {
        RegisterDto createDto = new RegisterDto("user.test@email.com", "123456", "User Test");
        User user = UserMapper.toUser(createDto);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            // Simulating JPA behavior (@Id @GeneratedValue)
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User responseBody = userService.save(user);

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getUsername()).isEqualTo("user.test@email.com");
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("It should throw exception when the username already exists")
    void save_Failed() {
        RegisterDto createDto = new RegisterDto("user.test@email.com", "123456", "User Test");
        User user = UserMapper.toUser(createDto);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO");

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(String.format("Username [%s] already registered", user.getUsername())));

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(UsernameUniqueViolationException.class)
                .hasMessageContaining("Username [user.test@email.com] already registered");

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
