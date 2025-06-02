package com.placeholder.placeholder.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class FormController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }
}
