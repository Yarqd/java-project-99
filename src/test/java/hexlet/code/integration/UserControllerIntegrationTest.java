package hexlet.code.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateDTO userCreateDTO;

    /**
     * Метод, который выполняется перед каждым тестом, для очистки репозитория
     * и инициализации объекта UserCreateDTO для использования в тестах.
     */
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setFirstName("Test");
        userCreateDTO.setLastName("User");
        userCreateDTO.setPassword("password");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userCreateDTO.getEmail()));

        // Проверка, что пользователь сохранен в базе
        User savedUser = userRepository.findByEmail(userCreateDTO.getEmail()).orElseThrow();
        assertEquals(userCreateDTO.getEmail(), savedUser.getEmail());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testGetAllUsers() throws Exception {
        userRepository.save(convertToUser(userCreateDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(userCreateDTO.getEmail()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testGetUserById() throws Exception {
        User user = userRepository.save(convertToUser(userCreateDTO));

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userCreateDTO.getEmail()));
    }

    private User convertToUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(userCreateDTO.getPassword());
        return user;
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testCreateUserWithInvalidEmail() throws Exception {
        userCreateDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());
    }
}
