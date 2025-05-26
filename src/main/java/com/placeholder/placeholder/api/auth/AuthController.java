package com.placeholder.placeholder.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }



}
