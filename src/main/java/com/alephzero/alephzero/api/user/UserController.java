package com.alephzero.alephzero.api.user;

import com.alephzero.alephzero.api.auth.service.AlephzeroUserDetailService;
import com.alephzero.alephzero.api.user.service.UserService;
import com.alephzero.alephzero.api.util.common.messages.ApiMessageFactory;
import com.alephzero.alephzero.api.util.common.messages.dto.ApiResponse;
import com.alephzero.alephzero.db.basicdto.UserDto;
import com.alephzero.alephzero.db.mappers.UserMapper;
import com.alephzero.alephzero.db.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user-related operations.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;
    private final AlephzeroUserDetailService userDetailsService;
    private final ApiMessageFactory apiMessageFactory;

    private final UserMapper userMapper;

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a response entity containing the user data in a {@link UserDto}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable int id) {
        User user = userService.findUserById(id);
        return apiMessageFactory.response(userMapper.toResponseDtoFromEntity(user)).ok().build();
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return a response entity containing the user data in a {@link UserDto}
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<UserDto>> getUser() {
        User user = userDetailsService.getCurrentUser();
        return apiMessageFactory.response(userMapper.toResponseDtoFromEntity(user)).ok().build();
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return a response entity indicating that the operation was successful with no content
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return apiMessageFactory.response().noContent().build();
    }

    /**
     * Deletes the currently authenticated user.
     *
     * @return a response entity indicating that the operation was successful with no content
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser() {
        User user = userDetailsService.getCurrentUser();
        userService.deleteById(user.getId());
        return apiMessageFactory.response().noContent().build();
    }
}
