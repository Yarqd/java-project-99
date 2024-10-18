package hexlet.code.integration;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskStatus taskStatus;

    @BeforeEach
    final void setUp() {
        taskStatusRepository.deleteAll();
        taskStatus = new TaskStatus();
        taskStatus.setName("New");
        taskStatus.setSlug("new");
    }

    @Test
    public void testCreateTaskStatus() throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(taskStatus.getName()));

        // Проверка, что статус был сохранен в базе
        assertTrue(taskStatusRepository.existsBySlug("new"));
    }

    @Test
    public void testPartialUpdateTaskStatus() throws Exception {
        // Сохранение нового статуса задачи
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated());

        // Получение сохраненного статуса
        TaskStatus savedStatus = taskStatusRepository.findBySlug(taskStatus.getSlug()).orElseThrow();

        // Данные для частичного обновления
        var data = new HashMap<String, String>();
        data.put("name", "new_name");

        // Выполнение запроса на частичное обновление
        var request = put("/api/task_statuses/{id}", savedStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        // Проверка обновленных данных в ответе
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.get("name")),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug())
        );

        // Проверка данных в базе
        var updatedStatus = taskStatusRepository.findById(savedStatus.getId()).orElseThrow();
        assertEquals(data.get("name"), updatedStatus.getName());
        assertEquals(taskStatus.getSlug(), updatedStatus.getSlug());
    }
}
