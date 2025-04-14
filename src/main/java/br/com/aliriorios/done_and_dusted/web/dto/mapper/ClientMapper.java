package br.com.aliriorios.done_and_dusted.web.dto.mapper;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {
    public static Client toClient(RegisterDto createDto) {
        return new ModelMapper().map(createDto, Client.class);
    }

    public static ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto responseDto = new ModelMapper().map(client, ClientResponseDto.class);

        if (client.getUser() != null) {
            responseDto.setUser(UserMapper.toResponseDto(client.getUser()));
        }

        return responseDto;
    }
}
