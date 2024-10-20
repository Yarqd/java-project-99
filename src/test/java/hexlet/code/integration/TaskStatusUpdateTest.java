package hexlet.code.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;  // Используем инжекцию

    private TaskStatus taskStatus;

    /**
     * Метод, который выполняется перед каждым тестом, для очистки репозитория
     * и инициализации объекта UserCreateDTO для использования в тестах.
     */
    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        taskStatusRepository.deleteAll();

        // Инициализация TaskStatus
        taskStatus = new TaskStatus("Initial Name", "initial-slug");

        // Передаем инжектированный ObjectMapper в TestUtils
        new TestUtils(objectMapper);
    }

    @Test
    public void testUpdateTaskStatusName() throws Exception {
        // Сохраняем TaskStatus через TestUtils
        TestUtils.saveTaskStatus(mockMvc, taskStatus);

        // Получаем сохранённый TaskStatus
        var status = TestUtils.getStatusByName(mockMvc, taskStatus.getName());

        // Данные для обновления
        var data = new HashMap<String, String>();
        data.put("name", "new_name");

        // Выполняем запрос на обновление
        var request = put("/api/task_statuses/{id}", status.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        // Проверка ответа
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(data.get("name")),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug())
        );

        // Проверка обновлённых данных в базе
        var actualStatus = TestUtils.getStatusByName(mockMvc, data.get("name"));
        assertEquals(data.get("name"), actualStatus.getName());
        assertEquals(taskStatus.getSlug(), actualStatus.getSlug());
    }
}
