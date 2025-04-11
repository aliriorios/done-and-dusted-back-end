package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;
}
