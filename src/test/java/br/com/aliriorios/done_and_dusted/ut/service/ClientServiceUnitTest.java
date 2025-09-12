package br.com.aliriorios.done_and_dusted.ut.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.repository.ClientRepository;
import br.com.aliriorios.done_and_dusted.service.ClientService;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceUnitTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("Test successfully when everything is OK")
    void saveClient_Successfully() {
        // Arrange
        Client client = createEntities();

        // Mock save
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        // Act
        Client responseBody = clientService.save(client);

        // Assert
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("Client Test");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    @DisplayName("Test successfully when everything is OK")
    void findById_Successfully() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findById(any(Long.class))).thenReturn(Optional.of(client));

        // Act
        Client responseBody = clientService.findById(client.getId());

        // Assert
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("Client Test");
        verify(clientRepository, times(1)).findById(client.getId());
    }

    @Test
    @DisplayName("Test failed when Client not found")
    void findById_Failed() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findById(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("Client [id=%s] not found.", client.getId())));

        // Act
        assertThatThrownBy(() -> clientService.findById(client.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Client [id=%s] not found.", client.getId()));

        // Assert
        verify(clientRepository, times(1)).findById(client.getId());
    }

    @Test
    @DisplayName("Test Successfully when is everything OK")
    void findByUserId_Successfully() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findByUserId(any(Long.class))).thenReturn(Optional.of(client));

        // Act
        Client responseBody = clientService.findByUserId(client.getUser().getId());

        // Assert
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getId()).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("Client Test");
        verify(clientRepository, times(1)).findByUserId(client.getUser().getId());
    }

    @Test
    @DisplayName("Test failed when Client not found")
    void findByUserId_Failed() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findByUserId(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("Client with User [id=%s] not found.", client.getUser().getId())));

        // Act
        assertThatThrownBy(() -> clientService.findByUserId(client.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Client with User [id=%s] not found.", client.getId()));

        // Assert
        verify(clientRepository, times(1)).findByUserId(client.getUser().getId());
    }

    @Test
    @DisplayName("Test successfully when everything is OK")
    void updateProfile_Successfully() {
        // Arrange
        Client client = createEntities();
        ClientUpdateDto updateDto = new ClientUpdateDto("Update Name", "2000-01-01", "12345678900", "123456789", "12345678911", client.getUser().getUsername());

        // Mock
        when(clientRepository.findByUserId(any(Long.class))).thenReturn(Optional.of(client));

        // Act
        clientService.updateProfile(client.getUser().getId(), updateDto);

        // Assert
        assertThat(client.getName()).isEqualTo("Update Name");
        verify(clientRepository, times(1)).findByUserId(client.getUser().getId());
    }

    @Test
    @DisplayName("Test failed when Client not found")
    void updateProfile_Failed_ClientNotFound() {
        // Arrange
        Client client = createEntities();
        ClientUpdateDto updateDto = new ClientUpdateDto("Update Name", "2000-01-01", "12345678900", "123456789", "12345678911", client.getUser().getUsername());

        // Mock
        when(clientRepository.findByUserId(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("Client with User [id=%s] not found.", client.getUser().getId())));

        // Act
        assertThatThrownBy(() -> clientService.findByUserId(client.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Client with User [id=%s] not found.", client.getId()));

        // Assert
        verify(clientRepository, times(1)).findByUserId(client.getUser().getId());
    }

    // The DataIntegrityViolation exception in this method will never be thrown, because I have no way to simulate since I don't use .save()

    @Test
    @DisplayName("Test successfully when everything is OK")
    void deleteById_Successfully() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findByUserId(any(Long.class))).thenReturn(Optional.of(client));

        // Act
        clientService.deleteById(client.getUser().getId());

        // Assert
        verify(clientRepository, times(1)).deleteById(client.getUser().getId());
    }

    @Test
    @DisplayName("Test failed when Client not found")
    void deleteById_Failed() {
        // Arrange
        Client client = createEntities();

        // Mock
        when(clientRepository.findByUserId(any(Long.class)))
                .thenThrow(new EntityNotFoundException(String.format("Client with User [id=%s] not found.", client.getUser().getId())));

        // Act
        assertThatThrownBy(() -> clientService.deleteById(client.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("Client with User [id=%s] not found.", client.getUser().getId()));

        // Assert
        verify(clientRepository, times(1)).findByUserId(client.getUser().getId());
    }

    /* Creating User to Optimize the Creation Process */
    private Client createEntities() {
        RegisterDto createDto = new RegisterDto("user.test@email.com", "$2a$12$FqgCHaIfbdV5zdJ7i8NVEOL1XMybVlH9L3Kt3Owb1ED1NFKqOCxyO", "Client Test");
        User user = UserMapper.toUser(createDto);
        user.setId(1L);

        Client client = ClientMapper.toClient(createDto);
        client.setId(1L);
        client.setUser(user);

        return client;
    }
}
