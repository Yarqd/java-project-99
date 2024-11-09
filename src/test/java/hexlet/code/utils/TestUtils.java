package hexlet.code.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {

    private static ObjectMapper objectMapper;

    public TestUtils(ObjectMapper objectMapper) {
        TestUtils.objectMapper = objectMapper;
    }

    public static void saveTaskStatus(MockMvc mockMvc, TaskStatus taskStatus, String jwtToken) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/task_statuses")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated());
    }

    public static TaskStatus getStatusByName(MockMvc mockMvc, String name, String jwtToken) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/task_statuses")
                        .header("Authorization", jwtToken)
                        .param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<TaskStatus> statuses = objectMapper.readValue(body, new TypeReference<>() {

        });

        if (statuses.isEmpty()) {
            throw new RuntimeException("TaskStatus with name " + name + " not found");
        }

        return statuses.get(0);
    }
}
