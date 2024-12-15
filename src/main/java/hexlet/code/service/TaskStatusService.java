package hexlet.code.service;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления статусами задач (TaskStatus).
 */
@Service
public class TaskStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusService.class);

    private static final String DEFAULT_STATUS_NAME = "Default";
    private static final String DEFAULT_SLUG = "default";

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * Создаёт новый статус задачи.
     *
     * @param taskStatus объект статуса задачи
     * @return созданный статус задачи
     */
    @Transactional
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        LOGGER.info("Creating task status: {}", taskStatus);

        // Проверяем только slug, поле name может быть null
        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            throw new IllegalArgumentException("TaskStatus slug must not be null or empty");
        }

        return taskStatusRepository.save(taskStatus);
    }

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список всех статусов задач
     */
    public List<TaskStatus> getAllTaskStatuses() {
        LOGGER.info("Fetching all task statuses");
        return taskStatusRepository.findAll();
    }

    /**
     * Получает статус задачи по ID.
     *
     * @param id идентификатор статуса задачи
     * @return найденный статус задачи
     * @throws RuntimeException если статус задачи не найден
     */
    public TaskStatus getTaskStatusById(Long id) {
        LOGGER.info("Fetching task status by ID: {}", id);
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Task status not found with ID: {}", id);
                    return new RuntimeException("TaskStatus not found");
                });
    }

    /**
     * Получает статус задачи по slug.
     *
     * @param slug уникальный идентификатор статуса задачи
     * @return найденный статус задачи
     * @throws RuntimeException если статус задачи не найден
     */
    public TaskStatus getTaskStatusBySlug(String slug) {
        LOGGER.info("Fetching task status by slug: {}", slug);
        return taskStatusRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> {
                    LOGGER.error("Task status not found for slug: {}", slug);
                    return new RuntimeException("TaskStatus not found: " + slug);
                });
    }

    /**
     * Получает статус задачи по имени.
     *
     * @param name имя статуса задачи
     * @return найденный статус задачи или null, если статус не найден
     */
    public TaskStatus getTaskStatusByName(String name) {
        LOGGER.info("Fetching task status by name: {}", name);
        return taskStatusRepository.findByName(name).orElse(null);
    }

    /**
     * Возвращает статус задачи по умолчанию. Если статус не найден, он создаётся.
     *
     * @return статус задачи по умолчанию
     */
    @Transactional
    public TaskStatus getDefaultTaskStatus() {
        LOGGER.info("Fetching default task status: {}", DEFAULT_STATUS_NAME);
        return taskStatusRepository.findBySlug(DEFAULT_SLUG)
                .orElseGet(() -> {
                    LOGGER.warn("Default TaskStatus not found. Creating new one.");
                    TaskStatus defaultStatus = new TaskStatus(null, DEFAULT_SLUG);
                    return taskStatusRepository.save(defaultStatus);
                });
    }

    /**
     * Полностью обновляет статус задачи.
     *
     * @param id         идентификатор статуса задачи
     * @param taskStatus объект с новыми данными
     * @return обновлённый статус задачи
     */
    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatus taskStatus) {
        LOGGER.info("Updating task status with ID: {}", id);

        TaskStatus existingTaskStatus = getTaskStatusById(id);

        // Обновляем только переданные поля
        if (taskStatus.getName() != null) {
            existingTaskStatus.setName(taskStatus.getName());
        }

        if (taskStatus.getSlug() != null && !taskStatus.getSlug().isEmpty()) {
            existingTaskStatus.setSlug(taskStatus.getSlug());
        }

        return taskStatusRepository.save(existingTaskStatus);
    }

    /**
     * Частично обновляет статус задачи.
     *
     * @param id                   идентификатор статуса задачи
     * @param taskStatusUpdateDto объект с изменяемыми данными
     * @return обновлённый статус задачи
     */
    @Transactional
    public TaskStatus partialUpdateTaskStatus(Long id, TaskStatusUpdateDto taskStatusUpdateDto) {
        LOGGER.info("Partially updating task status with ID: {}", id);

        TaskStatus existingTaskStatus = getTaskStatusById(id);

        // Обновляем только переданные поля
        if (taskStatusUpdateDto.getName() != null) {
            existingTaskStatus.setName(taskStatusUpdateDto.getName());
        }

        if (taskStatusUpdateDto.getSlug() != null && !taskStatusUpdateDto.getSlug().isEmpty()) {
            existingTaskStatus.setSlug(taskStatusUpdateDto.getSlug());
        }

        return taskStatusRepository.save(existingTaskStatus);
    }

    /**
     * Удаляет статус задачи по ID.
     *
     * @param id идентификатор статуса задачи
     */
    @Transactional
    public void deleteTaskStatus(Long id) {
        LOGGER.info("Deleting task status with ID: {}", id);
        if (!taskStatusRepository.existsById(id)) {
            throw new RuntimeException("TaskStatus not found");
        }
        taskStatusRepository.deleteById(id);
        LOGGER.info("Task status with ID: {} deleted successfully", id);
    }
}
