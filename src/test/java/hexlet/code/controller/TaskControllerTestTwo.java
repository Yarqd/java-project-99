package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTestTwo {

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
    private Task task;
    private TaskStatus taskStatus;
    private User user;

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
        user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

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

        // Создаем задачу для тестов
        taskStatus = new TaskStatus("in_progress", "in_progress");
        taskStatusRepository.save(taskStatus);

        task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.setAssignee(user);
        taskRepository.saveAndFlush(task); // Принудительно сохраняем и загружаем задачу
    }

    @Test
    public void testFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", jwtToken)
                        .param("status", "in_progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        assertEquals("in_progress", taskRepository.findAll().get(0).getTaskStatus().getSlug());
    }

    @Test
    public void testFilterByAllParams() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", jwtToken)
                        .param("titleCont", "Test Task")
                        .param("assigneeId", user.getId().toString())
                        .param("status", "in_progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testFilterByAssignee() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", jwtToken)
                        .param("assigneeId", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testFilterByTitle() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", jwtToken)
                        .param("titleCont", "Test Task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
