package hexlet.code.controller;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    public void testGetTaskById() throws Exception {
        // Создаем статус задачи
        TaskStatus status = new TaskStatus();
        status.setName("In Progress");
        status.setSlug("in_progress");
        taskStatusRepository.save(status);

        // Создаем задачу с указанным статусом
        Task task = new Task();
        task.setName("Test Task");
        task.setTaskStatus(status);
        taskRepository.save(task);

        // Выполняем запрос на получение задачи по ID
        mockMvc.perform(get("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Проверяем, что задача действительно существует в базе
        Task savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertEquals("Test Task", savedTask.getName());
        assertEquals("In Progress", savedTask.getTaskStatus().getName());
    }

}
