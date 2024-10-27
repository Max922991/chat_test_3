package com.example.chat_test_3.services;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.repositories.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientDetailsService implements UserDetailsService {
    private final ClientRepo clientRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Client> clientOptional = clientRepo.findByUsername(username);

        if (clientOptional.isEmpty()) {
            throw new UsernameNotFoundException("Client not found with username: " + username);
        }

        Client client = clientOptional.get();

        return new Client(
                client.getId(),
                client.getUsername(),
                client.getPassword(),
                client.getRole(),
                client.getPublicKey(),
                client.getPrivateKey()
        );
    }
}
