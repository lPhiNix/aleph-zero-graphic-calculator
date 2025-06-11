package com.alephzero.alephzero.api.user;

import com.alephzero.alephzero.api.user.service.UserService;
import com.alephzero.alephzero.api.util.common.messages.ApiResponseFactory;
import com.alephzero.alephzero.api.util.common.messages.dto.ApiResponse;
import com.alephzero.alephzero.db.basicdto.UserDto;
import com.alephzero.alephzero.db.mappers.UserMapper;
import com.alephzero.alephzero.db.models.User;
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
        return apiResponseFactory.ok(userMapper.toResponseDtoFromEntity(user));
    }
}