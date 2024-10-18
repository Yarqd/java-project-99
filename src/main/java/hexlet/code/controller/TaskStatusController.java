package hexlet.code.controller;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusController.class);

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список статусов задач
     */
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        LOGGER.info("Fetching all task statuses.");
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
        LOGGER.info("Fetching task status with id: {}", id);
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
        LOGGER.info("Received POST request to create TaskStatus: {}", taskStatus.toString());

        // Генерация имени, если оно не передано или пустое
        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            taskStatus.setName("Default Name " + System.currentTimeMillis());
        }

        // Генерация slug, если он не передан или пустой
        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            taskStatus.setSlug(taskStatus.getName().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-"));
        }

        TaskStatus createdTaskStatus = taskStatusService.createTaskStatus(taskStatus);
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
        LOGGER.info("Updating task status with id: {}, data: {}", id, taskStatus);

        // Проверяем наличие slug и name. Если они не переданы, сохраняем или генерируем новые значения.
        TaskStatus existingTaskStatus = taskStatusService.getTaskStatusById(id);

        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            taskStatus.setName(existingTaskStatus.getName());
        }

        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            taskStatus.setSlug(existingTaskStatus.getSlug());
        }

        TaskStatus updatedTaskStatus = taskStatusService.updateTaskStatus(id, taskStatus);
        LOGGER.info("Task status updated successfully: {}", updatedTaskStatus);
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
        LOGGER.info("Received request to partially update TaskStatus with id: {}", id);
        LOGGER.info("DTO Name: {}, DTO Slug: {}", taskStatusUpdateDto.getName(), taskStatusUpdateDto.getSlug());
        try {
            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
            LOGGER.info("TaskStatus updated successfully. New values: {}", updatedTaskStatus.toString());
            return ResponseEntity.ok(updatedTaskStatus);
        } catch (RuntimeException e) {
            LOGGER.error("Error during update: {}", e.getMessage());
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
        LOGGER.info("Deleting task status with id: {}", id);
        taskStatusService.deleteTaskStatus(id);
        LOGGER.info("Task status with id: {} deleted successfully.", id);
        return ResponseEntity.noContent().build();
    }
}
