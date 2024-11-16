package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public final class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        jwtToken = getAdminToken();
    }

    private String getAdminToken() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "hexlet@example.com");
        loginData.put("password", "qwerty");

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn();

        return "Bearer " + result.getResponse().getContentAsString().trim();
    }

    @Order(1)
    @Test
    void testCreate() throws Exception {
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPassword("newpassword");

        var request = post("/api/users")
                .header("Authorization", jwtToken)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("password").isAbsent(),
                v -> v.node("id").isPresent(),
                v -> v.node("firstName").isEqualTo(newUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(newUser.getLastName()),
                v -> v.node("email").isEqualTo(newUser.getEmail()),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Order(2)
    @Test
    void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Order(3)
    @Test
    void testShow() throws Exception {
        var userId = 1; // ID тестового пользователя
        var request = get("/api/users/{id}", userId)
                .header("Authorization", jwtToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("email").isEqualTo("hexlet@example.com"),
                v -> v.node("firstName").isEqualTo("Admin"),
                v -> v.node("lastName").isEqualTo("User"),
                v -> v.node("createdAt").isPresent(),
                v -> v.node("password").isAbsent()
        );
    }

    // Закомментированный тест сохраняется, исправлено только форматирование.
    // Проверь тест на работоспособность и корректность данных.
//    @Order(4)
//    @Test
//    void testUpdate() throws Exception {
//        var userId = 1; // ID тестового пользователя
//        var updatedData = new HashMap<String, String>();
//        updatedData.put("firstName", "Updated");
//        updatedData.put("lastName", "User");
//
//        var request = put("/api/users/{id}", userId)
//                .header("Authorization", jwtToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(updatedData));
//
//        var result = mockMvc.perform(request)
//                .andExpect(status().isOk())
//                .andReturn();
//
//        var body = result.getResponse().getContentAsString();
//
//        assertThatJson(body).and(
//                v -> v.node("email").isEqualTo("hexlet@example.com"),
//                v -> v.node("firstName").isEqualTo(updatedData.get("firstName")),
//                v -> v.node("lastName").isEqualTo(updatedData.get("lastName"))
//        );
//    }

    @Order(5)
    @Test
    void testDelete() throws Exception {
        var userId = 1; // ID тестового пользователя
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());
    }
}
