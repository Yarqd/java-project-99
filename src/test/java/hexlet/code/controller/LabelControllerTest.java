package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

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

    /**
     * Метод, который выполняется перед каждым тестом для инициализации необходимых данных.
     * Очищает репозитории и создает новую метку для использования в тестах.
     */
    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();

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

        // Проверка наличия новой метки в базе данных
        Label savedLabel = labelRepository.findByName("Feature").orElseThrow();
        assertEquals("Feature", savedLabel.getName());
    }

    @Test
    public void testUpdateLabel() throws Exception {
        Label updatedLabel = new Label("Updated Bug");

        mockMvc.perform(put("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLabel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bug"));

        // Проверка обновленного значения в базе данных
        Label savedLabel = labelRepository.findById(label.getId()).orElseThrow();
        assertEquals("Updated Bug", savedLabel.getName());
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
        TaskStatus taskStatus = new TaskStatus("Draft", "draft");
        taskStatusRepository.save(taskStatus);

        Label newLabel = new Label("New Feature");
        labelRepository.save(newLabel);

        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(taskStatus);
        task.getLabels().add(newLabel);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/labels/" + newLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Нельзя удалить метку, она связана с задачами.")));
    }
}
