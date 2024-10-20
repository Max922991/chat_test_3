package com.example.chat_test_3.controllers;

import com.example.chat_test_3.models.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ChatController {


    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(Message message, Principal principal) {
        if (principal != null) {
            message.setSender(principal.getName());
        } else {
            message.setSender("Anonymous");
        }
        return message;
    }
}
