package com.example.chat_test_3.controllers;

import com.example.chat_test_3.models.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
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
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(), "/queue/private", message);
    }
}
