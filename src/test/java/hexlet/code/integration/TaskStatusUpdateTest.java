//package hexlet.code.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import hexlet.code.model.TaskStatus;
//import hexlet.code.model.User;
//import hexlet.code.repository.TaskStatusRepository;
//import hexlet.code.repository.UserRepository;
//import hexlet.code.utils.TestUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class TaskStatusUpdateTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TaskStatusRepository taskStatusRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    private TestUtils testUtils;
//    private String jwtToken;
//    private TaskStatus taskStatus;
//
//    /**
//     * Метод, который выполняется перед каждым тестом для инициализации необходимых данных.
//     * Очищает репозитории, создает нового пользователя, получает JWT токен и создает метку.
//     */
//    @BeforeEach
//    public void setUp() throws Exception {
//        objectMapper.registerModule(new JavaTimeModule());
//        testUtils = new TestUtils(objectMapper);
//
//        taskStatusRepository.deleteAll();
//        userRepository.deleteAll();
//
//        // Создаем тестового пользователя и получаем JWT токен
//        User user = new User();
//        user.setEmail("testuser@example.com");
//        user.setPassword(passwordEncoder.encode("password"));
//        userRepository.save(user);
//
//        Map<String, String> loginData = new HashMap<>();
//        loginData.put("username", "testuser@example.com");
//        loginData.put("password", "password");
//
//        MvcResult result = mockMvc.perform(post("/api/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginData)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String responseBody = result.getResponse().getContentAsString();
//        jwtToken = "Bearer " + objectMapper.readTree(responseBody).get("token").asText();
//
//        // Инициализация TaskStatus
//        taskStatus = new TaskStatus("Initial Name", "initial-slug");
//
//        // Сохраняем TaskStatus через TestUtils с jwtToken
//        testUtils.saveTaskStatus(mockMvc, taskStatus, jwtToken);
//    }
//
//    @Test
//    public void testUpdateTaskStatusName() throws Exception {
//        // Получаем сохранённый TaskStatus
//        var status = testUtils.getStatusByName(mockMvc, taskStatus.getName(), jwtToken);
//
//        // Данные для обновления
//        var data = new HashMap<String, String>();
//        data.put("name", "new_name");
//
//        // Выполняем запрос на обновление
//        var request = put("/api/task_statuses/{id}", status.getId())
//                .header("Authorization", jwtToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(data));
//
//        var result = mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Проверка ответа
//        var body = result.getResponse().getContentAsString();
//        assertThatJson(body).and(
//                v -> v.node("name").isEqualTo(data.get("name")),
//                v -> v.node("slug").isEqualTo(taskStatus.getSlug())
//        );
//
//        // Проверка обновлённых данных в базе
//        var actualStatus = testUtils.getStatusByName(mockMvc, data.get("name"), jwtToken);
//        assertEquals(data.get("name"), actualStatus.getName());
//        assertEquals(taskStatus.getSlug(), actualStatus.getSlug());
//    }
//}
