package hexlet.code.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.model.Task;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.repository.LabelRepository;
import hexlet.code.demo.repository.TaskRepository;
import hexlet.code.demo.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

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
    private ObjectMapper objectMapper;

    private Label label;

    @BeforeEach
    public void setUp() {
        // Очищаем все репозитории перед каждым тестом, чтобы избежать конфликта с инициализацией данных
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();

        // Добавляем метку для использования в тестах
        label = new Label("Bug");
        labelRepository.save(label);
    }

    @Test
    public void testGetLabelById() throws Exception {
        mockMvc.perform(get("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("Bug"));
    }

    @Test
    public void testGetAllLabels() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Bug"));
    }

    @Test
    public void testCreateLabel() throws Exception {
        Label newLabel = new Label("Feature");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLabel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Feature"));
    }

    @Test
    public void testUpdateLabel() throws Exception {
        Label updatedLabel = new Label("Updated Bug");

        mockMvc.perform(put("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLabel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bug"));
    }

    @Test
    public void testDeleteLabel() throws Exception {
        mockMvc.perform(delete("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteLabelWithTasks() throws Exception {
        // Создаем статус задачи
        TaskStatus taskStatus = new TaskStatus("Draft", "draft");
        taskStatusRepository.save(taskStatus);

        // Создаем новую метку для задачи, чтобы избежать конфликта с инициализированными метками
        Label newLabel = new Label("New Feature");
        labelRepository.save(newLabel);

        // Создаем задачу и связываем ее с меткой
        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.getLabels().add(newLabel); // Связываем задачу с новой меткой
        taskRepository.save(task);

        // Пытаемся удалить метку, которая связана с задачей
        mockMvc.perform(delete("/api/labels/" + newLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cannot delete label, it is associated with tasks.")));
    }
}
