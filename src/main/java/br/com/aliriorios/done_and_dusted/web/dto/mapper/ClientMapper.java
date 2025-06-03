package br.com.aliriorios.done_and_dusted.web.dto.mapper;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.web.dto.RegisterDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientResponseDto;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {
    private static final ModelMapper mapper = new ModelMapper();

    // Conditions to only change the data in the database if they are passed in the DTO
    static {
        // Avoids overwriting existing data with NULL
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        // Converts "" fields to NULL
        Converter<String, String> emptyStringToNull = new Converter<String, String>() {
            @Override
            public String convert(MappingContext<String, String> context) {
                String source = context.getSource();
                if (source == null || source.trim().isEmpty()) {
                    return null;
                }
                return source;
            }
        };
        mapper.addConverter(emptyStringToNull);
    }

    public static Client toClient(RegisterDto createDto) {
        return mapper.map(createDto, Client.class);
    }

    public static void updateFromDto(Client client, ClientUpdateDto updateDto) {
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
