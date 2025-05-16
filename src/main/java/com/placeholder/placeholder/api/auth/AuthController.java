package com.placeholder.placeholder.api.auth;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.util.common.auth.UserDetailsService;
import com.placeholder.placeholder.api.util.common.auth.base.JwtFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final JwtFacade jwtService;
}
