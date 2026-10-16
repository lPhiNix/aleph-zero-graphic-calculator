package com.alephzero.alephzero.api.v1.user;

import com.alephzero.alephzero.core.services.auth.AlephzeroUserDetailService;
import com.alephzero.alephzero.core.services.user.UserService;
import com.alephzero.alephzero.commons.messages.dto.ApiResponse;
import com.alephzero.alephzero.api.v1.user.dto.UserDto;
import com.alephzero.alephzero.api.v1.user.mapper.UserMapper;
import com.alephzero.alephzero.core.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	private final UserMapper userMapper;

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param id the ID of the user to retrieve
	 * @return a response entity containing the user data in a {@link UserDto}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
		User user = userService.findUserById(id);
		return ResponseEntity.ok(userMapper.toResponseDtoFromEntity(user));
	}

	/**
	 * Retrieves the currently authenticated user.
	 *
	 * @return a response entity containing the user data in a {@link UserDto}
	 */
	@GetMapping()
	public ResponseEntity<UserDto> getUser() {
		User user = userDetailsService.getCurrentUser();
		return ResponseEntity.ok(userMapper.toResponseDtoFromEntity(user));
	}

	/**
	 * Deletes a user by their ID.
	 *
	 * @param id the ID of the user to delete
	 * @return a response entity indicating that the operation was successful with
	 *         no content
	 */
	@PostMapping("/delete/{id}")
	public ResponseEntity<Void> deleteUserById(@PathVariable int id) {
		userService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes the currently authenticated user.
	 *
	 * @return a response entity indicating that the operation was successful with
	 *         no content
	 */
	@PostMapping("/delete")
	public ResponseEntity<Void> deleteCurrentUser() {
		User user = userDetailsService.getCurrentUser();
		userService.deleteById(user.getId());
		return ResponseEntity.noContent().build();
	}
}
