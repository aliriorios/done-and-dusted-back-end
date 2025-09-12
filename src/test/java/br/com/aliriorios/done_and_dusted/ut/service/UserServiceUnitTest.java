package br.com.aliriorios.done_and_dusted.ut.service;

import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.entity.enums.Role;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.exception.PasswordInvalidException;
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

import java.util.Optional;

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
        // Arrange
        RegisterDto createDto = new RegisterDto("user.test@email.com", "123456", "User Test");
        User user = UserMapper.toUser(createDto);

        // Mock encode
        when(passwordEncoder.encode(user.getPassword())).thenReturn("$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO");

        // Mock save
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            // Simulating JPA behavior (@Id @GeneratedValue)
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User responseBody = userService.save(user);

        // Assert
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

    @Test
    @DisplayName("Successfully test when everything is OK")
    void findById_Successfully() {
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(1L);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        User responseBody = userService.findById(user.getId());

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getUsername()).isEqualTo("user.test@email.com");
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Test failed, throw exception when the user not found")
    void findById_Failed() {
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(1L);

        when(userRepository.findById(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("User [id=%s] not found.", user.getId())));

        assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("User [id=%s] not founded.", user.getId()));

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Successfully test when everything is OK")
    void findByUsername_Successfully() {
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(1L);

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        User responseBody = userService.findByUsername(user.getUsername());

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getUsername()).isEqualTo("user.test@email.com");
        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    @DisplayName("Test failed, throw exception when the user not found")
    void findByUsername_Failed() {
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(1L);

        when(userRepository.findByUsername(any(String.class)))
                .thenThrow(new EntityNotFoundException(String.format("User [%s] not founded", user.getUsername())));

        assertThatThrownBy(() -> userService.findByUsername(user.getUsername()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User [user.test@email.com] not founded");

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    @DisplayName("Successfully test when everything is OK")
    void findRoleByUsername_Successfully() {
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(1L);
        user.setRole(Role.ROLE_CLIENT);

        when(userRepository.findRoleByUsername(any(String.class))).thenReturn(Role.ROLE_CLIENT);

        Role responseBody = userService.findRoleByUsername(user.getUsername());

        assertThat(responseBody).isNotNull();
        verify(userRepository, times(1)).findRoleByUsername(any(String.class));
    }

    @Test
    @DisplayName("Successfully test when everything is OK")
    void updateUsername() {
        Long id = 1L;
        String newUsername = "New User Test";

        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(id);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        userService.updateUsername(id, newUsername);

        assertThat(user.getUsername()).isEqualTo(newUsername);
        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Test failed, throw exception when the user not found")
    void updateUsername_Failed_UserIdNotFound() {
        Long id = 1L;
        String newUsername = "New User Test";

        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "123456", "User Test"));
        user.setId(id);

        when(userRepository.findById(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("User [id=%s] not founded.", user.getId())));

        assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User [id=1] not founded.");

        verify(userRepository,times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("Successfully test when everything is OK")
    void updatePassword_Successfully() {
        // Arrange
        Long id = 1L;
        String newPassword = "123456";
        String encodedNewPassword = "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO";

        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "111111", "User Test"));
        user.setId(id);

        // Mock findById
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        // Mock matches
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        // Mock encode
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        // Act
        userService.updatePassword(id, user.getPassword(), newPassword, newPassword);

        // Assert
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @Test
    @DisplayName("Test failed, throw exception when currentPassword and confirmPassword unmatched")
    void updatePassword_Failed_UnmatchedConfirmPassword() {
        // Arrange
        Long id = 1L;
        String currentPassword = "111111";
        String confirmPassword = "222222";
        String newPassword = "123456";

        // Assert
        assertThatThrownBy(() -> userService.updatePassword(id, currentPassword, newPassword, confirmPassword))
                .isInstanceOf(PasswordInvalidException.class)
                .hasMessageContaining("New password does not match password confirmation", newPassword, confirmPassword);
    }

    @Test
    @DisplayName("Test failed, throw exception when find by id failed")
    void updatePassword_Failed_UserNotFound() {
        // Arrange
        Long id = 1L;
        String currentPassword = "111111";
        String confirmPassword = "123456";
        String newPassword = "123456";

        // Mock findById
        when(userRepository.findById(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("User [id=%s] not founded.", id)));

        // Act
        assertThatThrownBy(() -> userService.updatePassword(id, currentPassword, newPassword, confirmPassword))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("User [id=%s] not founded", id));
    }

    @Test
    @DisplayName("Test failed, throw exception when current password unmatched with user.getPassword()")
    void updatePassword_Failed_CurrentPasswordUnmatched() {
        // Arrange
        Long id = 1L;
        String currentPassword = "111111";
        String confirmPassword = "123456";
        String newPassword = "123456";

        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "111111", "User Test"));
        user.setId(id);

        // Mock findById
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        // Mock matches
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        // Act
        assertThatThrownBy(() -> userService.updatePassword(id, currentPassword, newPassword, confirmPassword))
                .isInstanceOf(PasswordInvalidException.class)
                .hasMessageContaining("The password entered does not match the user's");
    }

    @Test
    @DisplayName("Successfully test when everything is OK")
    void deleteById_Successfully() {
        // Arrange
        Long id = 1L;
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "111111", "User Test"));
        user.setId(id);

        // Mock
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        // Act
        userService.deleteById(id);

        // Assert
        verify(userRepository, times(1)).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("Test failed, throw exception when user not found to delete")
    void deleteById_Failed_UserNotFound() {
        // Arrange
        Long id = 1L;
        User user = UserMapper.toUser(new RegisterDto("user.test@email.com", "111111", "User Test"));
        user.setId(id);

        // Mock
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> userService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("User [id=%s] not founded.", id));

        // Assert
        verify(userRepository, times(1)).findById(any(Long.class));
    }
}
