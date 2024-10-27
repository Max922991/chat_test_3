package com.example.chat_test_3.controllers;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("client", new Client());
        return "register";
    }

    @PostMapping("/register")
    public String register(Client client, Model model) throws NoSuchAlgorithmException {
        try {
            clientService.register(client);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        try {
            clientService.login(username, password);
            return "redirect:/chat";
        } catch (AuthenticationException e) {
            return "redirect:/login?error";
        }
    }
}
