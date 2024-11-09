package hexlet.code.controller;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Контроллер для работы с задачами (Tasks).
 * Предоставляет методы для получения, создания, обновления и удаления задач.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Получает список задач с возможностью фильтрации.
     *
     * @param titleCont  часть названия задачи для поиска
     * @param assigneeId ID исполнителя задачи
     * @param status     статус задачи
     * @param labelId    ID метки задачи
     * @return список задач, соответствующих фильтрам
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getTasks(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId) {
        LOGGER.info("Fetching tasks with filters - Title: {}, Assignee ID: {}, Status: {}, Label ID: {}",
                titleCont, assigneeId, status, labelId);
        List<Task> tasks = taskRepository.findTasksByFilters(titleCont, assigneeId, status, labelId);
        LOGGER.info("Found {} tasks matching the filters", tasks.size());
        return tasks;
    }

    /**
     * Получает задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return объект задачи или статус 404, если задача не найдена
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    LOGGER.info("Task found: {}", task);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Создает новую задачу.
     *
     * @param task объект задачи для создания
     * @return созданная задача с кодом состояния 201 или сообщение об ошибке
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> createTask(@RequestBody Task task) {
        LOGGER.info("Received POST request to create Task: {}", task);

        if (task.getName() == null || task.getName().isEmpty()) {
            LOGGER.error("Task name is required");
            return ResponseEntity.badRequest().body("Task name is required");
        }
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            LOGGER.error("Task description is required");
            return ResponseEntity.badRequest().body("Task description is required");
        }

        if (task.getTaskStatus() == null) {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            if (defaultStatus == null) {
                LOGGER.error("Default TaskStatus is not available");
                return ResponseEntity.badRequest().body("Default TaskStatus is not available");
            }
            task.setTaskStatus(defaultStatus);
            LOGGER.info("Setting default status for task: {}", defaultStatus);
        }

        Task createdTask = taskRepository.save(task);
        LOGGER.info("Task created successfully: {}", createdTask);
        return ResponseEntity.status(201).body(createdTask);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id          идентификатор задачи
     * @param updatedTask обновленные данные задачи
     * @return обновленная задача или статус 404, если задача не найдена
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        LOGGER.info("Received PUT request to update Task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    LOGGER.info("Task found: {}", task);
                    task.setName(updatedTask.getName());
                    task.setDescription(updatedTask.getDescription());
                    task.setTaskStatus(updatedTask.getTaskStatus());
                    task.setAssignee(updatedTask.getAssignee());
                    Task savedTask = taskRepository.save(task);
                    LOGGER.info("Task updated successfully: {}", savedTask);
                    return ResponseEntity.ok(savedTask);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return статус 204, если задача удалена, или 404, если задача не найдена
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        LOGGER.info("Received DELETE request to remove Task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    LOGGER.info("Task with ID: {} deleted successfully", id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
