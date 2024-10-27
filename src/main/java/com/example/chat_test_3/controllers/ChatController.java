package com.example.chat_test_3.controllers;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.models.Message;
import com.example.chat_test_3.repositories.ClientRepo;
import com.example.chat_test_3.services.ClientService;
import com.example.chat_test_3.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ClientRepo clientRepo;
    private final ClientService clientService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message, Principal principal) {
        if (principal != null) {
            message.setSender(principal.getName());
        } else {
            message.setSender("Anonymous");
        }
        return message;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload Message message, Principal principal) {
        if (principal != null) {
            message.setSender(principal.getName());
        } else {
            message.setSender("Anonymous");
        }

        try {
            // Получаем клиента получателя
            Client receiverClient = clientRepo.findByUsername(message.getReceiver())
                    .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

            // Шифруем сообщение с использованием публичного ключа получателя
            Message encryptedMessageObj = messageService.encryptMessage(message);

            // Логирование отправляемого сообщения
            log.info("Sending encrypted message: {}", encryptedMessageObj);

            // Отправка зашифрованного сообщения пользователю
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver(), "/queue/private", encryptedMessageObj);
        } catch (Exception e) {
            log.error("Error sending private message", e);
        }
    }



    @MessageMapping("/chat.receivePrivateMessage")
    public void receivePrivateMessage(@Payload Message message) {
        try {
            // Дешифруем сообщение
            String decryptedMessage = messageService.decryptMessage(message);

            // Логирование расшифрованного сообщения
            log.info("Received and decrypted message: {}", decryptedMessage);

            // Создание объекта сообщения для отправки
            Message decryptedMessageObj = Message.builder()
                    .content(decryptedMessage)
                    .sender(message.getSender())
                    .receiver(message.getReceiver())
                    .build();

            // Отправка расшифрованного сообщения пользователю
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver(), "/queue/private", decryptedMessageObj);
        } catch (Exception e) {
            log.error("Error receiving and decrypting private message", e);
        }
    }
}
