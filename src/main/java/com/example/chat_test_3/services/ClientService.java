package com.example.chat_test_3.services;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.models.Role;
import com.example.chat_test_3.repositories.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepo clientRepo;
    private final PasswordEncoder passwordEncoder;

    public void register(Client client) {
        if (clientRepo.findByUsername(client.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Role.ROLE_USER);
        clientRepo.save(client);
    }

    public boolean login(String username, String password) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Client " + username + " not found"));
        try {
            if (passwordEncoder.matches(password, client.getPassword())) {
                return true;
            } else {
                throw new RuntimeException("Password does not match");
            }
        } catch (Exception e) {
            throw new RuntimeException("Password does not match");
        }
    }


}
