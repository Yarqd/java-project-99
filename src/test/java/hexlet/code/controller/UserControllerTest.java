package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public final class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String jwtToken;
    private User testUser;

    /**
     * Инициализация перед каждым тестом.
     * Очищает репозиторий, создает тестового пользователя и генерирует JWT токен.
     *
     * @throws Exception если происходит ошибка во время инициализации
     */
    @BeforeEach
    public void setUp() throws Exception {
        userRepository.deleteAll();

        // Создаем тестового пользователя с зашифрованным паролем
        testUser = new User();
        testUser.setFirstName("Initial Name");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password")); // Хешируем пароль
        userRepository.save(testUser);

        // Получаем JWT токен для тестового пользователя
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "test@example.com");
        loginData.put("password", "password");

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn();

        // Извлекаем токен из JSON-ответа
        String responseBody = result.getResponse().getContentAsString();
        jwtToken = "Bearer " + objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<String, String>();
        data.put("firstName", "New name");

        var request = put("/api/users/" + testUser.getId())
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
