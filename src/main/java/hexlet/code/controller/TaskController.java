package hexlet.code.controller;

import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    /**
     * Получает список задач с заголовком X-Total-Count для фронтенда.
     *
     * @return список задач с заголовком для пагинации
     */
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        LOGGER.info("Fetching all tasks");
        List<Task> tasks = taskRepository.findAll();

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
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Создает новую задачу.
     *
     * @param task объект задачи для создания
     * @return созданная задача с кодом состояния 201
     */
    @PostMapping
    public ResponseEntity<Object> createTask(@RequestBody Task task) {
        LOGGER.info("Creating new task: {}", task);

        if (task.getName() == null || task.getName().isEmpty()) {
            LOGGER.error("Task name is required");
            return ResponseEntity.badRequest().body("Task name is required");
        }

        if (task.getTaskStatus() == null) {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            if (defaultStatus == null) {
                LOGGER.error("Default TaskStatus is not available");
                return ResponseEntity.badRequest().body("Default TaskStatus is not available");
            }
            task.setTaskStatus(defaultStatus);
            LOGGER.info("Setting default TaskStatus: {}", defaultStatus);
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
     * @return обновленная задача или статус 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        LOGGER.info("Updating task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
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
}
