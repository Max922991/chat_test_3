package com.example.chat_test_3.services;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.models.Role;
import com.example.chat_test_3.repositories.ClientRepo;
import com.example.chat_test_3.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepo clientRepo;
    private final PasswordEncoder passwordEncoder;

    public void register(Client client) throws Exception {
        if (clientRepo.findByUsername(client.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        KeyPair keyPair = EncryptionUtil.generateKeyPair();
        client.setPublicKey(EncryptionUtil.exportPublicKey(keyPair.getPublic()));
        client.setPrivateKey(EncryptionUtil.exportPrivateKey(keyPair.getPrivate()));

        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole(Role.ROLE_USER);

        clientRepo.save(client);
    }

    public void login(String username, String password) {
        Client client = clientRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Client " + username + " not found"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new RuntimeException("Password does not match");
        }
    }
}
