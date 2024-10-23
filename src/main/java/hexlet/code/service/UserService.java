package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Создание нового пользователя на основе переданных данных.
     *
     * @param userDTO данные для создания пользователя
     * @return данные о созданном пользователе в виде DTO
     */
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userDTO) {
        User user = new User();

        // Если id не null, устанавливаем его, иначе оно генерируется автоматически
        if (userDTO.getId() != null) {
            user.setId(userDTO.getId());
        }

        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Хеширование пароля
        userRepository.save(user);
        return convertToResponseDTO(user);
    }

    /**
     * Получение списка всех пользователей.
     *
     * @return список всех пользователей в виде DTO
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получение пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return данные о пользователе в виде DTO
     */
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new RuntimeException("User not found")); // Исключение, если пользователь не найден
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id идентификатор пользователя
     * @param userDTO данные для обновления пользователя
     * @return обновленные данные о пользователе в виде DTO
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // Исключение, если пользователь не найден

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);
        return convertToResponseDTO(user);
    }

    /**
     * Удаление пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found"); // Исключение, если пользователь не найден
        }
        userRepository.deleteById(id);
    }

    /**
     * Преобразование сущности пользователя в DTO.
     *
     * @param user объект пользователя
     * @return DTO с данными пользователя
     */
    public UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setCreatedAt(user.getCreatedAt());
        return responseDTO;
    }
}
