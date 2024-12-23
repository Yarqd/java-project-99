package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

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
    private Label label;

    /**
     * Метод, который выполняется перед каждым тестом для инициализации необходимых данных.
     * Очищает репозитории, создает нового пользователя, получает JWT токен и создает метку.
     */
    @BeforeEach
    public void setUp() throws Exception {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем тестового пользователя
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        userRepository.save(testUser);

        // Получаем JWT токен
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
        jwtToken = "Bearer " + responseBody.trim(); // Извлекаем токен напрямую как строку

        // Создаем метку для тестов
        label = new Label("Bug");
        labelRepository.save(label);
    }

    @Test
    public void testGetLabelById() throws Exception {
        mockMvc.perform(get("/api/labels/" + label.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("Bug"));
    }

    @Test
    public void testGetAllLabels() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Bug"));
    }

    @Test
    public void testCreateLabel() throws Exception {
        LabelDTO newLabelDTO = new LabelDTO(null, "Feature", null);

        mockMvc.perform(post("/api/labels")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLabelDTO))) // Отправляем DTO
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Feature"));

        // Проверка наличия новой метки в базе данных
        Label savedLabel = labelRepository.findByName("Feature").orElseThrow();
        assertEquals("Feature", savedLabel.getName());
    }


    @Test
    public void testUpdateLabel() throws Exception {
        // Создаем DTO вместо Label
        LabelDTO updatedLabelDTO = new LabelDTO(null, "Updated Bug", null);

        mockMvc.perform(put("/api/labels/" + label.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLabelDTO))) // Отправляем DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bug"));

        // Проверка обновленного значения в базе данных
        Label savedLabel = labelRepository.findById(label.getId()).orElseThrow();
        assertEquals("Updated Bug", savedLabel.getName());
    }


    @Test
    public void testDeleteLabel() throws Exception {
        mockMvc.perform(delete("/api/labels/" + label.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/" + label.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteLabelWithTasks() throws Exception {
        // Создаем статус задачи
        TaskStatus taskStatus = new TaskStatus("Draft", "draft");
        taskStatusRepository.save(taskStatus);

        // Создаем метку
        Label newLabel = new Label("New Feature");
        labelRepository.save(newLabel);

        // Создаем задачу, связанную с новой меткой
        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.getLabels().add(newLabel);
        taskRepository.save(task);

        // Пытаемся удалить метку, связанную с задачами
        mockMvc.perform(delete("/api/labels/" + newLabel.getId())
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Нельзя удалить метку, она связана с задачами.")));
    }

}
