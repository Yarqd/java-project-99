package hexlet.code.service;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Создание нового статуса задачи.
     *
     * @param taskStatus объект статуса задачи для сохранения
     * @return созданный статус задачи
     */
    @Transactional
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
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
     * Обновление статуса задачи.
     *
     * @param id идентификатор существующего статуса задачи
     * @param taskStatus объект с обновленными данными статуса задачи
     * @return обновленный статус задачи
     * @throws RuntimeException если статус задачи не найден
     */
    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatus taskStatus) {
        TaskStatus existingTaskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        existingTaskStatus.setName(taskStatus.getName());
        existingTaskStatus.setSlug(taskStatus.getSlug());
        return taskStatusRepository.save(existingTaskStatus);
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
}
