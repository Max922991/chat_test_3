package com.example.chat_test_3.services;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.models.Message;
import com.example.chat_test_3.repositories.ClientRepo;
import com.example.chat_test_3.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ClientRepo clientRepo;


    public Message encryptMessage(Message message) throws Exception {
        Optional<Client> clientOpt = clientRepo.findByUsername(message.getReceiver());
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Receiver not found");
        }

        Client client = clientOpt.get();

        PublicKey publicKey = EncryptionUtil.importPublicKey(client.getPublicKey());

        SecretKey secretKey = EncryptionUtil.generateAESKey();

        String encryptedMessage = EncryptionUtil.encrypt(message.getContent(), secretKey);
        String encryptedAesKey = EncryptionUtil.encryptAESKey(secretKey, publicKey);

        return Message.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .receiver(message.getReceiver())
                .encryptedKey(encryptedAesKey)
                .encryptedContent(encryptedMessage)
                .build();
    }

    public String decryptMessage(Message message) throws Exception {
        Optional<Client> clientOpt = clientRepo.findByUsername(message.getReceiver());
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Receiver not found");
        }

        Client client = clientOpt.get();
        PrivateKey privateKey = EncryptionUtil.importPrivateKey(client.getPrivateKey());

        SecretKey aesKey = EncryptionUtil.decryptAESKey(message.getEncryptedKey(), privateKey);
        return EncryptionUtil.decrypt(message.getEncryptedContent(), aesKey);
    }
}






