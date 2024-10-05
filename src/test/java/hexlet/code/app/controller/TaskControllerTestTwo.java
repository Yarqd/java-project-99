package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

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

    private Task task;
    private TaskStatus taskStatus;
    private User user;

    /**
     * Метод, который выполняется перед каждым тестом для инициализации необходимых данных.
     * Очищает репозитории и создает новую метку для использования в тестах.
     */
    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        taskStatus = new TaskStatus("in_progress", "in_progress");
        taskStatusRepository.save(taskStatus);

        user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        userRepository.save(user);

        task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.setAssignee(user);
        taskRepository.save(task);

        // Проверка сохраненной задачи и её статуса
        System.out.println("Task after save: " + taskRepository.findAll());
        System.out.println("Task status after save: " + task.getTaskStatus().getName());

        // Инициализация ленивых коллекций, если необходимо
        Hibernate.initialize(task.getLabels());
    }

    @Test
    public void testFilterByStatus() throws Exception {
        // Проверка запроса фильтрации по статусу
        System.out.println("Executing filter by status: in_progress");

        mockMvc.perform(get("/api/tasks")
                        .param("status", "in_progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testFilterByAllParams() throws Exception {
        System.out.println("Executing filter by all params: title='Test Task', assigneeId=" + user.getId()
                + ", status='in_progress'");

        mockMvc.perform(get("/api/tasks")
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
                        .param("assigneeId", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testFilterByTitle() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "Test Task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
