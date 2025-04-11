package br.com.aliriorios.done_and_dusted.service;

import br.com.aliriorios.done_and_dusted.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

}
