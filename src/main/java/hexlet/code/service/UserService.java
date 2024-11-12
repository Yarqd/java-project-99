package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserResponseDTO;
import hexlet.code.model.Role;
import hexlet.code.model.User;
import hexlet.code.repository.RoleRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Создание нового пользователя с заданными ролями.
     *
     * @param userDTO данные для создания пользователя
     * @param roleNames список имен ролей
     * @return данные о созданном пользователе в виде DTO
     */
    @Transactional
    public UserResponseDTO createUserWithRoles(UserCreateDTO userDTO, List<String> roleNames) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
            roles.add(role);
        }
        user.setRoles(roles);
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Обновление данных пользователя.
     *
     * @param id идентификатор пользователя
     * @param updates данные для обновления пользователя
     * @param currentUsername имя текущего пользователя для проверки прав
     * @return обновленные данные о пользователе в виде DTO
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, Map<String, Object> updates, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Обновление полей
        if (updates.containsKey("firstName")) {
            user.setFirstName((String) updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            user.setLastName((String) updates.get("lastName"));
        }
        if (updates.containsKey("password")) {
            user.setPassword(passwordEncoder.encode((String) updates.get("password")));
        }

        userRepository.save(user);
        return convertToResponseDTO(user);
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
