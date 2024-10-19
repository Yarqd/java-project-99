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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController { // Убрали final с класса

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Возвращает список задач с возможностью фильтрации по заголовку, назначенному пользователю, статусу и метке.
     *
     * @param titleCont  часть заголовка задачи
     * @param assigneeId идентификатор исполнителя задачи
     * @param status     статус задачи
     * @param labelId    идентификатор метки задачи
     * @return список задач, удовлетворяющих условиям фильтрации
     */
    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId) {
        LOGGER.info("Fetching tasks with filters - Title: {}, Assignee ID: {}, Status: {}, Label ID: {}",
                titleCont, assigneeId, status, labelId);
        return taskRepository.findTasksByFilters(titleCont, assigneeId, status, labelId);
    }

    /**
     * Возвращает задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача с указанным идентификатором или 404, если задача не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    LOGGER.info("Found task: {}", task);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Создает новую задачу. Если статус задачи не указан, присваивает статус по умолчанию.
     *
     * @param task объект задачи
     * @return созданная задача
     */
    @PostMapping
    public ResponseEntity<Object> createTask(@RequestBody Task task) {
        LOGGER.info("Received POST request to create Task: {}", task);

        // Проверка обязательных полей
        if (task.getName() == null || task.getName().isEmpty()) {
            String errorMessage = "Task name is required";
            LOGGER.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        if (task.getDescription() == null || task.getDescription().isEmpty()) {
            String errorMessage = "Task description is required";
            LOGGER.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // Устанавливаем дефолтный статус, если не указан
        if (task.getTaskStatus() == null) {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            if (defaultStatus == null) {
                String errorMessage = "Default TaskStatus is not available";
                LOGGER.error(errorMessage);
                return ResponseEntity.badRequest().body(errorMessage);
            }
            task.setTaskStatus(defaultStatus);
            LOGGER.info("Task status was missing. Setting default status: {}", defaultStatus);
        }

        Task createdTask = taskRepository.save(task);
        LOGGER.info("Task created successfully: {}", createdTask);
        return ResponseEntity.status(201).body(createdTask);
    }

    /**
     * Обновляет данные существующей задачи.
     *
     * @param id          идентификатор задачи
     * @param updatedTask обновленные данные задачи
     * @return обновленная задача или 404, если задача не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        LOGGER.info("Received PUT request to update Task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    LOGGER.info("Found task: {}", task);
                    if (updatedTask.getName() != null) {
                        task.setName(updatedTask.getName());
                        LOGGER.info("Updated task name to: {}", updatedTask.getName());
                    }
                    if (updatedTask.getDescription() != null) {
                        task.setDescription(updatedTask.getDescription());
                        LOGGER.info("Updated task description to: {}", updatedTask.getDescription());
                    }
                    if (updatedTask.getTaskStatus() != null) {
                        task.setTaskStatus(updatedTask.getTaskStatus());
                        LOGGER.info("Updated task status to: {}", updatedTask.getTaskStatus());
                    }
                    task.setAssignee(updatedTask.getAssignee());
                    LOGGER.info("Updated task assignee to: {}", updatedTask.getAssignee());
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
     * @return 204 No Content, если задача успешно удалена, или 404, если задача не найдена
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        LOGGER.info("Received DELETE request to remove Task with ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    LOGGER.info("Task deleted successfully: {}", task);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    LOGGER.warn("Task with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
