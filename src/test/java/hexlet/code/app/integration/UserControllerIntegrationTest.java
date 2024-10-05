package hexlet.code.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(userCreateDTO.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userCreateDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userCreateDTO.getLastName()));
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

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testUpdateUser() throws Exception {
        User user = userRepository.save(convertToUser(userCreateDTO));

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setPassword("new-password");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userUpdateDTO.getEmail()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER"})
    public void testDeleteUser() throws Exception {
        User user = userRepository.save(convertToUser(userCreateDTO));

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
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
