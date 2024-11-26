package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public final class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        LOGGER.info("Creating new user: {}", userCreateDTO.getEmail());
        UserResponseDTO user = userService.createUserWithRoles(userCreateDTO, List.of("USER"));
        LOGGER.info("User created successfully: {}", user);
        return ResponseEntity.status(201).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        LOGGER.info("Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        LOGGER.info("Found {} users", users.size());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        LOGGER.info("Fetching user by ID: {}", id);
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        LOGGER.info("Updating user with ID: {} by {}", id, currentUsername);
        UserResponseDTO updatedUser = userService.updateUser(id, updates, currentUsername);
        LOGGER.info("User updated successfully: {}", updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication)
            throws AccessDeniedException {
        String currentUsername = authentication.getName();
        LOGGER.info("Deleting user with ID: {} by {}", id, currentUsername);
        userService.deleteUser(id, currentUsername);
        return ResponseEntity.noContent().build();
    }
}
