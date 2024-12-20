package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления пользователями.
 * Все методы данного класса предназначены для выполнения операций CRUD с пользователями.
 * Класс объявлен как final, так как он не предназначен для наследования.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Создает нового пользователя.
     *
     * @param userCreateDTO DTO с данными для создания пользователя.
     * @return Ответ с данными созданного пользователя и статусом 201.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        LOGGER.info("Creating new user: {}", userCreateDTO.getEmail());
        UserResponseDTO user = userService.createUserWithRoles(userCreateDTO, List.of("USER"));
        LOGGER.info("User created successfully: {}", user);
        return ResponseEntity.status(201).body(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return Ответ с данными всех пользователей и статусом 200.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        LOGGER.info("Fetching all users");
        List<UserResponseDTO> users = userService.getAllUsers();
        LOGGER.info("Found {} users", users.size());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    /**
     * Возвращает данные пользователя по его ID.
     *
     * @param id Идентификатор пользователя.
     * @return Ответ с данными пользователя и статусом 200.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        LOGGER.info("Fetching user by ID: {}", id);
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Обновляет данные пользователя.
     * Доступ к обновлению ограничен только самим пользователем.
     *
     * @param id Идентификатор пользователя.
     * @param updates Карта с обновляемыми данными.
     * @param authentication Объект аутентификации текущего пользователя.
     * @return Ответ с обновленными данными пользователя и статусом 200.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userService.isCurrentUser(#id)")
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

    /**
     * Удаляет пользователя по его ID.
     * Доступ к удалению ограничен только самим пользователем.
     *
     * @param id Идентификатор пользователя.
     * @param authentication Объект аутентификации текущего пользователя.
     * @return Ответ со статусом 204.
     * @throws AccessDeniedException Если текущий пользователь не имеет прав на удаление.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isCurrentUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication)
            throws AccessDeniedException {
        String currentUsername = authentication.getName();
        LOGGER.info("Deleting user with ID: {} by {}", id, currentUsername);
        userService.deleteUser(id, currentUsername);
        return ResponseEntity.noContent().build();
    }
}
