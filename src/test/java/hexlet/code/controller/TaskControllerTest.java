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
        jwtToken = "Bearer " + responseBody.trim(); // Извлекаем токен напрямую как строку
    }

    @Test
    public void testGetTaskById() throws Exception {
        // Создаем статус задачи
        TaskStatus status = new TaskStatus();
        status.setName("In Progress");
        status.setSlug("in_progress");
        taskStatusRepository.save(status);

        // Создаем пользователя-исполнителя
        User assignee = new User();
        assignee.setEmail("assignee@example.com");
        assignee.setPassword(passwordEncoder.encode("password"));
        userRepository.save(assignee);

        // Создаем задачу с указанным статусом и исполнителем
        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(status);
        task.setAssignee(assignee);
        taskRepository.saveAndFlush(task); // Принудительно сохраняем задачу

        // Выполняем GET запрос для получения задачи
        MvcResult result = mockMvc.perform(get("/api/tasks/" + task.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем, что задача возвращена корректно
        String responseBody = result.getResponse().getContentAsString();
        Map<String, Object> responseTask = objectMapper.readValue(responseBody, Map.class);

        assertEquals(task.getId(), Long.valueOf((Integer) responseTask.get("id")));
        assertEquals("Test Task", responseTask.get("title"));
        assertEquals("in_progress", responseTask.get("status"));
        assertEquals(assignee.getId(), Long.valueOf((Integer) responseTask.get("assignee_id")));
    }
}
