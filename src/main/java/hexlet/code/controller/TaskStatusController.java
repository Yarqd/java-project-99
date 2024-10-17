package hexlet.code.controller;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private static final Logger logger = LoggerFactory.getLogger(TaskStatusController.class);

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список статусов задач
     */
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        logger.info("Fetching all task statuses.");
        return taskStatusService.getAllTaskStatuses();
    }

    /**
     * Возвращает статус задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return статус задачи с указанным идентификатором
     */
    @GetMapping("/{id}")
    public TaskStatus getTaskStatusById(@PathVariable Long id) {
        logger.info("Fetching task status with id: {}", id);
        return taskStatusService.getTaskStatusById(id);
    }

    /**
     * Создает новый статус задачи.
     *
     * @param taskStatus объект нового статуса задачи
     * @return созданный статус задачи
     */
    @PostMapping
    public ResponseEntity<TaskStatus> createTaskStatus(@Valid @RequestBody TaskStatus taskStatus) {
        logger.info("Creating new task status: {}", taskStatus);
        TaskStatus createdTaskStatus = taskStatusService.createTaskStatus(taskStatus);
        logger.info("Task status created successfully: {}", createdTaskStatus);
        return ResponseEntity.status(201).body(createdTaskStatus);
    }

    /**
     * Обновляет существующий статус задачи.
     *
     * @param id         идентификатор обновляемого статуса задачи
     * @param taskStatus обновленный объект статуса задачи
     * @return обновленный статус задачи
     */
    @PutMapping("/{id}")
    public TaskStatus updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatus taskStatus) {
        logger.info("Updating task status with id: {}, data: {}", id, taskStatus);
        TaskStatus updatedTaskStatus = taskStatusService.updateTaskStatus(id, taskStatus);
        logger.info("Task status updated successfully: {}", updatedTaskStatus);
        return updatedTaskStatus;
    }

    /**
     * Частичное обновление статуса задачи.
     *
     * @param id                идентификатор статуса задачи
     * @param taskStatusUpdateDto DTO с полями, которые нужно обновить
     * @return обновленный статус задачи или сообщение об ошибке
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateTaskStatus(@PathVariable Long id,
                                                     @RequestBody TaskStatusUpdateDto taskStatusUpdateDto) {
        logger.info("Received request to partially update TaskStatus with id: {}", id);
        logger.info("DTO Name: {}, DTO Slug: {}", taskStatusUpdateDto.getName(), taskStatusUpdateDto.getSlug());

        try {
            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
            logger.info("TaskStatus updated successfully. New values: Name: {}, Slug: {}",
                    updatedTaskStatus.getName(), updatedTaskStatus.getSlug());

            return ResponseEntity.ok(updatedTaskStatus);
        } catch (RuntimeException e) {
            logger.error("Error during update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Удаляет статус задачи по его идентификатору.
     *
     * @param id идентификатор удаляемого статуса задачи
     * @return 204 No Content, если удаление прошло успешно
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        logger.info("Deleting task status with id: {}", id);
        taskStatusService.deleteTaskStatus(id);
        logger.info("Task status with id: {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}
