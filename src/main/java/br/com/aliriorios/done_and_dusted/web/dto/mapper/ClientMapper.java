package br.com.aliriorios.done_and_dusted.web.dto.mapper;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {
    private static final ModelMapper mapper = new ModelMapper();

    public static Client toClient(RegisterDto createDto) {
        return mapper.map(createDto, Client.class);
    }

    public static void updateFromDto(Client client, ClientUpdateDto updateDto) {
        if (updateDto.getBirthday().isEmpty()) {
            updateDto.setBirthday(client.getBirthday().toString());
        }

        if (updateDto.getPhone().isEmpty()) {
            updateDto.setPhone(client.getPhone());
        }

        if (updateDto.getRg().isEmpty()) {
            updateDto.setRg(client.getRg());
        }

        if (updateDto.getCpf().isEmpty()) {
            updateDto.setCpf(client.getCpf());
        }

        client.setBirthday(LocalDate.parse(updateDto.getBirthday()));

        mapper.map(updateDto, client);
    }

    public static ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto responseDto = mapper.map(client, ClientResponseDto.class);

        if (client.getUser() != null) {
            responseDto.setUser(UserMapper.toResponseDto(client.getUser()));
        }

        return responseDto;
    }
}
