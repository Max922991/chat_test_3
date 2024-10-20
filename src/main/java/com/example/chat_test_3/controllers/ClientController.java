package com.example.chat_test_3.controllers;

import com.example.chat_test_3.models.Client;
import com.example.chat_test_3.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PreAuthorize("hasAuthority('Role_USER')")
    public String register(Client client, Model model) {
        try {
            clientService.register(client);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
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
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/chat";
        } catch (AuthenticationException e) {
            return "redirect:/login?error";
        }
    }
}
