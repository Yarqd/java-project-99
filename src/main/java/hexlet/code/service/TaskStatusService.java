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
 *
 * Этот класс не предназначен для расширения. Все методы предоставляют доступ к бизнес-логике
 * и защищены от некорректного использования.
 */
@Service
public class TaskStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusService.class);
    private static final String DEFAULT_STATUS_NAME = "To Do";
    private static final String DEFAULT_SLUG = "to-do";

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * Создаёт новый статус задачи. Метод рекомендуется использовать в контроллере.
     * @param taskStatus объект статуса задачи
     * @return созданный статус задачи
     */
    @Transactional
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        LOGGER.info("Creating task status: {}", taskStatus);
        validateTaskStatus(taskStatus);
        return taskStatusRepository.save(taskStatus);
    }

    /**
     * Возвращает список всех статусов задач. Метод используется для получения данных.
     * @return список всех статусов задач
     */
    public List<TaskStatus> getAllTaskStatuses() {
        LOGGER.info("Fetching all task statuses");
        return taskStatusRepository.findAll();
    }

    /**
     * Получает статус задачи по ID. Метод выбросит исключение, если статус не найден.
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
     * Получает статус задачи по имени. Метод выбросит исключение, если статус не найден.
     * @param name имя статуса задачи
     * @return найденный статус задачи
     * @throws RuntimeException если статус задачи не найден
     */
    public TaskStatus getTaskStatusByName(String name) {
        LOGGER.info("Fetching task status by name: {}", name);
        return taskStatusRepository.findByName(name)
                .orElseThrow(() -> {
                    LOGGER.error("Task status not found with name: {}", name);
                    return new RuntimeException("TaskStatus not found with name: " + name);
                });
    }

    /**
     * Возвращает статус задачи по умолчанию. Если статус не найден, он создаётся.
     * @return статус задачи по умолчанию
     */
    public TaskStatus getDefaultTaskStatus() {
        LOGGER.info("Fetching default task status: {}", DEFAULT_STATUS_NAME);
        return taskStatusRepository.findByName(DEFAULT_STATUS_NAME)
                .orElseGet(() -> {
                    LOGGER.warn("Default TaskStatus not found. Creating new one.");
                    TaskStatus defaultStatus = new TaskStatus(DEFAULT_STATUS_NAME, DEFAULT_SLUG);
                    return taskStatusRepository.save(defaultStatus);
                });
    }

    /**
     * Полностью обновляет статус задачи. Все поля будут перезаписаны.
     * @param id идентификатор статуса задачи
     * @param taskStatus объект с новыми данными
     * @return обновлённый статус задачи
     */
    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatus taskStatus) {
        LOGGER.info("Updating task status with ID: {}", id);
        validateTaskStatus(taskStatus);
        TaskStatus existingTaskStatus = getTaskStatusById(id);
        existingTaskStatus.setName(taskStatus.getName());
        existingTaskStatus.setSlug(taskStatus.getSlug());
        return taskStatusRepository.save(existingTaskStatus);
    }

    /**
     * Частично обновляет статус задачи. Поля, которые не переданы, остаются неизменными.
     * @param id идентификатор статуса задачи
     * @param taskStatusUpdateDto объект с изменяемыми данными
     * @return обновлённый статус задачи
     */
    @Transactional
    public TaskStatus partialUpdateTaskStatus(Long id, TaskStatusUpdateDto taskStatusUpdateDto) {
        LOGGER.info("Partially updating task status with ID: {}", id);
        TaskStatus existingTaskStatus = getTaskStatusById(id);

        // Обновляем name, если оно передано
        if (taskStatusUpdateDto.getName() != null && !taskStatusUpdateDto.getName().isEmpty()) {
            existingTaskStatus.setName(taskStatusUpdateDto.getName());
        }

        // Если slug не передан, сохраняем старое значение
        if (taskStatusUpdateDto.getSlug() != null && !taskStatusUpdateDto.getSlug().isEmpty()) {
            existingTaskStatus.setSlug(taskStatusUpdateDto.getSlug());
        } else {
            LOGGER.info("Slug not provided in request. Retaining existing slug: {}", existingTaskStatus.getSlug());
        }

        // Проверка обновлённого объекта
        validateTaskStatus(existingTaskStatus);

        // Сохранение изменений
        TaskStatus updatedStatus = taskStatusRepository.save(existingTaskStatus);
        LOGGER.info("Partially updated task status: {}", updatedStatus);
        return updatedStatus;
    }


    /**
     * Удаляет статус задачи по ID. Метод выбросит исключение, если статус не найден.
     * @param id идентификатор статуса задачи
     * @throws RuntimeException если статус задачи не найден
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

    /**
     * Валидация статуса задачи. Проверяет, что имя и slug не являются пустыми.
     * @param taskStatus статус задачи для проверки
     * @throws IllegalArgumentException если имя или slug некорректны
     */
    private void validateTaskStatus(TaskStatus taskStatus) {
        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            throw new IllegalArgumentException("TaskStatus name must not be null or empty");
        }
        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            throw new IllegalArgumentException("TaskStatus slug must not be null or empty");
        }
    }
}
