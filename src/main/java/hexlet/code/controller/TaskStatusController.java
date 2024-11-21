package hexlet.code.controller;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для управления статусами задач (TaskStatus).
 */
@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusController.class);

    private final TaskStatusService taskStatusService;

    /**
     * Конструктор для внедрения зависимости TaskStatusService.
     *
     * @param taskStatusService сервис для управления статусами задач
     */
    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    /**
     * Получение всех статусов задач с заголовком X-Total-Count для фронтенда.
     *
     * @param name фильтр по имени статуса задачи (опционально)
     * @return список статусов задач
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<List<TaskStatus>> getAllTaskStatuses(
            @RequestParam(value = "name", required = false) String name) {
        LOGGER.info("Fetching all task statuses");
        List<TaskStatus> taskStatuses = taskStatusService.getAllTaskStatuses();

        if (name != null) {
            taskStatuses = taskStatuses.stream()
                    .filter(status -> name.equals(status.getName()))
                    .toList();
        }

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    /**
     * Получение статуса задачи по ID.
     *
     * @param id идентификатор статуса задачи
     * @return статус задачи или 404, если не найден
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<TaskStatus> getTaskStatusById(@PathVariable Long id) {
        LOGGER.info("Fetching task status with ID: {}", id);
        TaskStatus taskStatus = taskStatusService.getTaskStatusById(id);
        return ResponseEntity.ok(taskStatus);
    }

    /**
     * Создание нового статуса задачи.
     *
     * @param taskStatus объект статуса задачи
     * @return созданный статус задачи с кодом 201
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<TaskStatus> createTaskStatus(@RequestBody TaskStatus taskStatus) {
        LOGGER.info("Creating task status: {}", taskStatus);
        TaskStatus createdTaskStatus = taskStatusService.createTaskStatus(taskStatus);
        return ResponseEntity.status(201).body(createdTaskStatus);
    }

    /**
     * Полное обновление статуса задачи.
     *
     * @param id идентификатор статуса задачи
     * @param updatedTaskStatus объект с обновленными данными статуса задачи
     * @return обновленный статус задачи или 404, если не найден
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<TaskStatus> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatus updatedTaskStatus) {
        LOGGER.info("Updating task status with id: {}, data: {}", id, updatedTaskStatus);

        TaskStatus existingTaskStatus = taskStatusService.getTaskStatusById(id);

        if (updatedTaskStatus.getName() != null && !updatedTaskStatus.getName().isEmpty()) {
            existingTaskStatus.setName(updatedTaskStatus.getName());
        }

        if (updatedTaskStatus.getSlug() != null && !updatedTaskStatus.getSlug().isEmpty()) {
            existingTaskStatus.setSlug(updatedTaskStatus.getSlug());
        } else {
            LOGGER.info("Slug is not provided, keeping the current value: {}", existingTaskStatus.getSlug());
        }

        TaskStatus savedTaskStatus = taskStatusService.updateTaskStatus(id, existingTaskStatus);
        return ResponseEntity.ok(savedTaskStatus);
    }

    /**
     * Частичное обновление статуса задачи.
     *
     * @param id идентификатор статуса задачи
     * @param taskStatusUpdateDto DTO для частичного обновления
     * @return обновленный статус задачи или ошибка
     */
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<?> partialUpdateTaskStatus(
            @PathVariable Long id,
            @RequestBody TaskStatusUpdateDto taskStatusUpdateDto) {
        LOGGER.info("Partially updating task status with ID: {}", id);
        try {
            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
            return ResponseEntity.ok(updatedTaskStatus);
        } catch (RuntimeException e) {
            LOGGER.error("Error updating task status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удаление статуса задачи по ID.
     *
     * @param id идентификатор статуса задачи
     * @return статус 204 при успешном удалении
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public final ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        LOGGER.info("Deleting task status with ID: {}", id);
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }
}
