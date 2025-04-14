package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.entity.User;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserService userService;
    private final ClientService clientService;

    // SAVE -----------------------------------------------
    @Transactional
    public ClientResponseDto register (@Valid @RequestBody RegisterDto createDto) {
        User user = userService.save(UserMapper.toUser(createDto));

        Client client = ClientMapper.toClient(createDto);
        client.setUser(user);

        client = clientService.save(client);

        return ClientMapper.toResponseDto(client);
    }
}
