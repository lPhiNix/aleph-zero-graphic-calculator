package com.placeholder.placeholder.db;

import com.placeholder.placeholder.db.models.dto.UserCreationRequest;
import com.placeholder.placeholder.util.enums.AppCode;
import com.placeholder.placeholder.util.messages.ApiResponseFactory;
import com.placeholder.placeholder.util.messages.dto.ApiResponse;
import com.placeholder.placeholder.util.messages.dto.content.EmptyContentResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserCreationRequest userCreationRequest, HttpServletRequest httpServletRequest) {
        logger.info("Registering user: {}", userCreationRequest.username());
        userService.createUser(userCreationRequest);

        ApiResponse<EmptyContentResponse> response = ApiResponseFactory.
                createEmptyContentResponse(httpServletRequest.getRequestURI(), AppCode.CREATED, "User Successfully Registered");

        return new ResponseEntity<>(response, AppCode.CREATED.getStatus());
    }
}