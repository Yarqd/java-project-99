package hexlet.code.controller;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String jwtToken;

    /**
     * Метод, который выполняется перед каждым тестом для инициализации необходимых данных.
     * Очищает репозитории, создает нового пользователя, получает JWT токен и создает метку.
     */
    @BeforeEach
    public void setUp() throws Exception {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем тестового пользователя и получаем JWT токен
        User testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testuser@example.com");
        loginData.put("password", "password");

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        jwtToken = "Bearer " + objectMapper.readTree(responseBody).get("token").asText();
    }

    @Test
    public void testGetTaskById() throws Exception {
        // Создаем статус задачи
        TaskStatus status = new TaskStatus();
        status.setName("In Progress");
        status.setSlug("in_progress");
        taskStatusRepository.save(status);

        // Создаем задачу с указанным статусом
        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(status);
        taskRepository.saveAndFlush(task); // Принудительно сохраняем и загружаем задачу

        // Загрузка ленивых связей перед проверкой
        task = taskRepository.findById(task.getId()).orElseThrow();

        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Проверяем, что задача действительно существует в базе
        Task savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals("Test Task", savedTask.getName());
        assertEquals("In Progress", savedTask.getTaskStatus().getName());
    }
}
