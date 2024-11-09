package hexlet.code.controller;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;

/**
 * Контроллер для управления статусами задач (TaskStatus).
 * Предоставляет методы для создания, обновления, удаления и получения статусов задач.
 */
@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusController.class);

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Получает список всех статусов задач.
     *
     * @return список статусов задач
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TaskStatus> getAllTaskStatuses() {
        LOGGER.info("Fetching all task statuses.");
        return taskStatusService.getAllTaskStatuses();
    }

    /**
     * Получает статус задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return объект TaskStatus
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TaskStatus getTaskStatusById(@PathVariable Long id) {
        LOGGER.info("Fetching task status with id: {}", id);
        return taskStatusService.getTaskStatusById(id);
    }

    /**
     * Создает новый статус задачи.
     *
     * @param taskStatus объект TaskStatus для создания
     * @return созданный объект TaskStatus
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskStatus> createTaskStatus(@Valid @RequestBody TaskStatus taskStatus) {
        LOGGER.info("Received POST request to create TaskStatus: {}", taskStatus);

        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            taskStatus.setName("Default Name " + System.currentTimeMillis());
        }

        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            taskStatus.setSlug(taskStatus.getName().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-"));
        }

        TaskStatus createdTaskStatus = taskStatusService.createTaskStatus(taskStatus);
        return ResponseEntity.status(201).body(createdTaskStatus);
    }

    /**
     * Обновляет существующий статус задачи.
     *
     * @param id         идентификатор статуса задачи
     * @param taskStatus обновленные данные TaskStatus
     * @return обновленный объект TaskStatus
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public TaskStatus updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatus taskStatus) {
        LOGGER.info("Updating task status with id: {}, data: {}", id, taskStatus);

        TaskStatus existingTaskStatus = taskStatusService.getTaskStatusById(id);

        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            taskStatus.setName(existingTaskStatus.getName());
        }

        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            taskStatus.setSlug(existingTaskStatus.getSlug());
        }

        return taskStatusService.updateTaskStatus(id, taskStatus);
    }

    /**
     * Частичное обновление статуса задачи.
     *
     * @param id                 идентификатор статуса задачи
     * @param taskStatusUpdateDto данные для частичного обновления TaskStatus
     * @return обновленный объект TaskStatus или сообщение об ошибке
     */
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> partialUpdateTaskStatus(@PathVariable Long id,
                                                     @RequestBody TaskStatusUpdateDto taskStatusUpdateDto) {
        LOGGER.info("Received request to partially update TaskStatus with id: {}", id);
        try {
            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
            return ResponseEntity.ok(updatedTaskStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удаляет статус задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return статус 204 при успешном удалении
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        LOGGER.info("Deleting task status with id: {}", id);
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }
}
