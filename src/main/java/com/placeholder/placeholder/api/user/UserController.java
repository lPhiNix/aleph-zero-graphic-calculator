package com.placeholder.placeholder.api.user;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.user.dto.UserCreationRequest;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.content.responses.SimpleResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ApiResponseFactory apiResponseFactory;

    @Autowired
    public UserController(UserService userService, ApiResponseFactory apiResponseFactory) {
        this.userService = userService;
        this.apiResponseFactory = apiResponseFactory;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SimpleResponse>> register(@RequestBody @Valid UserCreationRequest userCreationRequest) {
        logger.info("Registering user: {}", userCreationRequest.username());
        userService.createUser(userCreationRequest);

        SimpleResponse response = new SimpleResponse("User successfuly created");
        return apiResponseFactory.ok(response);
    }
}