package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.exception.CpfUniqueViolationException;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.exception.RgUniqueViolationException;
import br.com.aliriorios.done_and_dusted.repository.ClientRepository;
import br.com.aliriorios.done_and_dusted.web.dto.client.ClientUpdateDto;
import br.com.aliriorios.done_and_dusted.web.dto.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    // POST -----------------------------------------------
    @Transactional
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    // GET ------------------------------------------------
    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client [id=%s] not founded.", id))
        );
    }

    @Transactional(readOnly = true)
    public Client findByUserId(Long id) {
        return clientRepository.findByUserId(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Client [id=%s] not founded.", id))
        );
    }

    // PATCH ----------------------------------------------
    @Transactional
    public void updateProfile(Long id, ClientUpdateDto updateDto) {

        try {
            Client client = findByUserId(id);
            ClientMapper.updateFromDto(client, updateDto);

            LocalDate newBirthday = LocalDate.parse(updateDto.getBirthday());
            client.setBirthday(newBirthday);

        } catch (DataIntegrityViolationException e) {
            String message = e.getRootCause() != null ? e.getRootCause().getMessage() : "";

            if (message.contains("uk_client_rg")) {
                throw new RgUniqueViolationException(String.format("RG [%s] already registered", updateDto.getRg()));

            } else if (message.contains("uk_client_cpf")) {
                throw new CpfUniqueViolationException(String.format("CPF [%s] already registered", updateDto.getCpf()));
            }
        }
    }

    // DELETE ---------------------------------------------
    @Transactional
    public void deleteById(Long userId) {
        Client client = findByUserId(userId);
        clientRepository.deleteById(client.getId());
    }
}
