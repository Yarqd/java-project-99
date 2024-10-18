package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Создание нового пользователя.
     *
     * @param userCreateDTO DTO с данными для создания пользователя
     * @return созданный пользователь и статус 201 (Created)
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        System.out.println("Received POST request to create User: " + userCreateDTO);
        UserResponseDTO user = userService.createUser(userCreateDTO);
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
        return new ResponseEntity<>(users, HttpStatus.OK);  // Возвращаем статус 200 OK
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
        return new ResponseEntity<>(user, HttpStatus.OK);  // Возвращаем статус 200 OK
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id идентификатор пользователя
     * @param userUpdateDTO DTO с данными для обновления пользователя
     * @return обновленный пользователь и статус 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);  // Возвращаем статус 200 OK
    }

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return статус 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Возвращаем статус 204 NO CONTENT
    }
}
