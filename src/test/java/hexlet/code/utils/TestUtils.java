package hexlet.code.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {

    private static ObjectMapper objectMapper;

    public TestUtils(ObjectMapper objectMapper) {
        TestUtils.objectMapper = objectMapper;
    }

    public static void saveTaskStatus(MockMvc mockMvc, TaskStatus taskStatus) throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated());
    }

    public static TaskStatus getStatusByName(MockMvc mockMvc, String name) throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses")
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        // Десериализуем как массив TaskStatus
        List<TaskStatus> statuses = objectMapper.readValue(body, new TypeReference<>() { });

        // Возвращаем первый элемент списка, если он есть
        if (statuses.isEmpty()) {
            throw new RuntimeException("TaskStatus with name " + name + " not found");
        }

        return statuses.get(0);
    }
}
