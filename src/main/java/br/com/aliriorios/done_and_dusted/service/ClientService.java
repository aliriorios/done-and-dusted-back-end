package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.entity.Client;
import br.com.aliriorios.done_and_dusted.exception.EntityNotFoundException;
import br.com.aliriorios.done_and_dusted.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    // POST -----------------------------------------------
    @Transactional
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    // GET -----------------------------------------------
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

    // DELETE ---------------------------------------------
    @Transactional
    public void deleteById(Long userId) {
        Client client = findByUserId(userId);
        clientRepository.deleteById(client.getId());
    }
}
