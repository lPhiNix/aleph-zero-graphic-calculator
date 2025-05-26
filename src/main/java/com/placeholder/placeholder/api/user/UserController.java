package com.placeholder.placeholder.api.user;

import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.user.dto.UserCreationRequest;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.api.util.common.messages.dto.content.responses.SimpleResponse;
import com.placeholder.placeholder.db.basicdto.UserDto;
import com.placeholder.placeholder.db.mappers.UserMapper;
import com.placeholder.placeholder.db.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ApiResponseFactory apiResponseFactory;
    private final UserMapper userMapper;

    // Example endpoint for gettin the identification of a user.
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable int id) {
        User user = userService.findUserById(id);
        return apiResponseFactory.ok(userMapper.toDto(user));
    }
}