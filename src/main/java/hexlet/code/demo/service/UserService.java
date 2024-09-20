package hexlet.code.demo.service;

import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.dto.UserResponseDTO;
import hexlet.code.demo.dto.UserUpdateDTO;
import hexlet.code.demo.model.User;
import hexlet.code.demo.repository.UserRepository;
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

    // Создание пользователя
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Хеширование пароля
        userRepository.save(user);
        return convertToResponseDTO(user);
    }

    // Получение всех пользователей
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Получение пользователя по ID
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new RuntimeException("User not found")); // Здесь выбрасываем исключение
                                                                            // если пользователь не найден
    }

    // Обновление пользователя
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // Здесь выбрасываем исключение
                                                                            // если пользователь не найден

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

    // Удаление пользователя
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found"); // Проверяем существование пользователя перед удалением
        }
        userRepository.deleteById(id);
    }

    // Преобразование User в UserResponseDTO
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
