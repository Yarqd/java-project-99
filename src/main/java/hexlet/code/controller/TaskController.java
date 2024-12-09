package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с задачами (Tasks).
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private UserService userService;

    /**
     * Получает список задач.
     *
     * @return список задач
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        LOGGER.info("Fetching all tasks");
        List<Map<String, Object>> tasks = taskRepository.findAll()
                .stream()
                .map(this::formatTaskResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    /**
     * Получает задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача или статус 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);

        return taskRepository.findById(id)
                .map(task -> {
                    Map<String, Object> response = formatTaskResponse(task); // Формируем ответ
                    return ResponseEntity.ok().body(response); // Возвращаем как ResponseEntity
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build(); // Возвращаем статус 404
                });
    }

    /**
     * Создает новую задачу.
     *
     * @param taskCreateDTO DTO задачи для создания
     * @return созданная задача
     */
    @PostMapping
    public ResponseEntity<Object> createTask(@RequestBody TaskCreateDTO taskCreateDTO) {
        LOGGER.info("Creating new task: {}", taskCreateDTO);

        if (taskCreateDTO.getName() == null || taskCreateDTO.getName().isEmpty()) {
            LOGGER.error("Task name is required");
            return ResponseEntity.badRequest().body("Task name is required");
        }

        Task task = new Task();
        task.setName(taskCreateDTO.getName());
        task.setDescription(taskCreateDTO.getDescription());
        task.setIndex(taskCreateDTO.getIndex());

        // Устанавливаем исполнителя
        if (taskCreateDTO.getAssigneeId() != null) {
            User assignee = getAssignee(taskCreateDTO.getAssigneeId());
            if (assignee == null) {
                return ResponseEntity.badRequest().body("Assignee not found");
            }
            task.setAssignee(assignee);
        }

        // Устанавливаем статус задачи
        if (taskCreateDTO.getStatus() != null) {
            TaskStatus taskStatus = taskStatusService.getTaskStatusByName(taskCreateDTO.getStatus());
            if (taskStatus == null) {
                LOGGER.error("TaskStatus not found with name: {}", taskCreateDTO.getStatus());
                return ResponseEntity.badRequest().body("TaskStatus not found");
            }
            task.setTaskStatus(taskStatus);
        } else {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            task.setTaskStatus(defaultStatus);
        }

        Task createdTask = taskRepository.save(task);
        LOGGER.info("Task created successfully: {}", createdTask);

        return ResponseEntity.status(201).body(formatTaskResponse(createdTask));
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return статус 204 или 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        LOGGER.info("Deleting task with ID: {}", id);
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

    /**
     * Форматирует задачу для возврата клиенту.
     *
     * @param task задача
     * @return отформатированная задача
     */
    private Map<String, Object> formatTaskResponse(Task task) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", task.getId());
        response.put("index", task.getIndex());
        response.put("createdAt", task.getCreatedAt());
        response.put("assignee_id", task.getAssignee() != null ? task.getAssignee().getId() : null);
        response.put("title", task.getName());
        response.put("content", task.getDescription());
        response.put("status", task.getTaskStatus() != null ? task.getTaskStatus().getName() : null);
        response.put("labels", task.getLabels().stream().map(label -> label.getName()).collect(Collectors.toList()));
        return response;
    }

    /**
     * Получает исполнителя задачи по его идентификатору.
     *
     * @param assigneeId идентификатор исполнителя
     * @return объект User или null, если пользователь не найден
     */
    private User getAssignee(Long assigneeId) {
        try {
            return userService.findUserById(assigneeId);
        } catch (RuntimeException e) {
            LOGGER.error("Assignee not found with ID: {}", assigneeId);
            return null;
        }
    }
}
