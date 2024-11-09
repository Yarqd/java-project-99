package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления пользователями.
 * Предоставляет эндпоинты для создания, получения и обновления пользователей.
 */
@RestController
@RequestMapping("/api/users")
public final class UserController {

    @Autowired
    private UserService userService;

    /**
     * Создание нового пользователя с дефолтной ролью "USER".
     *
     * @param userCreateDTO DTO с данными для создания пользователя
     * @return созданный пользователь и статус 201 (Created)
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO user = userService.createUserWithRoles(userCreateDTO, List.of("USER"));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return список пользователей и статус 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return данные о пользователе и статус 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id идентификатор пользователя
     * @param updates карта с изменяемыми данными пользователя
     * @param authentication аутентификация текущего пользователя
     * @return обновленный пользователь и статус 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        UserResponseDTO updatedUser = userService.updateUser(id, updates, currentUsername);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
