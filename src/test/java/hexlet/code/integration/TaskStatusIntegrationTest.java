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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
