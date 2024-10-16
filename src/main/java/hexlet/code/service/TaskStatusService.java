package hexlet.code.service;

import hexlet.code.model.TaskStatus;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private static final String DEFAULT_STATUS_NAME = "To Do";

    /**
     * Создание нового статуса задачи.
     *
     * @param taskStatus объект статуса задачи для сохранения
     * @return созданный статус задачи
     */
    @Transactional
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        validateTaskStatus(taskStatus);
        return taskStatusRepository.save(taskStatus);
    }

    /**
     * Получение всех статусов задач.
     *
     * @return список всех статусов задач
     */
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    /**
     * Получение статуса задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @return объект статуса задачи
     * @throws RuntimeException если статус задачи не найден
     */
    public TaskStatus getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
    }

    /**
     * Получение статуса задачи по умолчанию.
     *
     * @return статус по умолчанию
     */
    public TaskStatus getDefaultTaskStatus() {
        return taskStatusRepository.findByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new RuntimeException("Default TaskStatus not found"));
    }

    /**
     * Обновление статуса задачи.
     *
     * @param id         идентификатор существующего статуса задачи
     * @param taskStatus объект с обновленными данными статуса задачи
     * @return обновленный статус задачи
     * @throws RuntimeException если статус задачи не найден
     */
    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatus taskStatus) {
        validateTaskStatus(taskStatus);
        TaskStatus existingTaskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        existingTaskStatus.setName(taskStatus.getName());
        existingTaskStatus.setSlug(taskStatus.getSlug());
        return taskStatusRepository.save(existingTaskStatus);
    }

    /**
     * Частичное обновление статуса задачи.
     * Обновляет только те поля, которые переданы в DTO.
     *
     * @param id                идентификатор существующего статуса задачи
     * @param taskStatusUpdateDto объект с обновляемыми данными статуса задачи
     * @return обновленный статус задачи
     * @throws RuntimeException если статус задачи не найден
     * @throws IllegalArgumentException если DTO не содержит полей для обновления
     */
    public TaskStatus partialUpdateTaskStatus(Long id, TaskStatusUpdateDto taskStatusUpdateDto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task status not found"));

        if (taskStatusUpdateDto.getName() != null) {
            taskStatus.setName(taskStatusUpdateDto.getName());
        }

        if (taskStatusUpdateDto.getSlug() != null) {
            taskStatus.setSlug(taskStatusUpdateDto.getSlug());
        }

        return taskStatusRepository.save(taskStatus);
    }

    /**
     * Удаление статуса задачи по его идентификатору.
     *
     * @param id идентификатор статуса задачи
     * @throws RuntimeException если статус задачи не найден
     */
    @Transactional
    public void deleteTaskStatus(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new RuntimeException("TaskStatus not found");
        }
        taskStatusRepository.deleteById(id);
    }

    /**
     * Валидация объекта TaskStatus.
     *
     * @param taskStatus объект статуса задачи для проверки
     * @throws IllegalArgumentException если поля name или slug пустые
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
