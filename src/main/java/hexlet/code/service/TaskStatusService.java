package hexlet.code.service;

import hexlet.code.model.TaskStatus;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.repository.TaskStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusService.class);

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        LOGGER.info("Initializing TaskStatusService with TaskStatusRepository: {}", taskStatusRepository);
        this.taskStatusRepository = taskStatusRepository;
    }

    private static final String DEFAULT_STATUS_NAME = "To Do";

    @Transactional
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        LOGGER.info("Creating task status: {}", taskStatus);
        validateTaskStatus(taskStatus);
        TaskStatus savedStatus = taskStatusRepository.save(taskStatus);
        LOGGER.info("Task status created: {}", savedStatus);
        return savedStatus;
    }

    public List<TaskStatus> getAllTaskStatuses() {
        LOGGER.info("Service: Fetching all task statuses from the repository");
        List<TaskStatus> statuses = taskStatusRepository.findAll();
        LOGGER.info("Service: Retrieved {} task statuses from the repository", statuses.size());
        return statuses;
    }

    public TaskStatus getTaskStatusById(Long id) {
        LOGGER.info("Fetching task status by ID: {}", id);
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Task status not found with ID: {}", id);
                    return new RuntimeException("TaskStatus not found");
                });
    }

    public TaskStatus getDefaultTaskStatus() {
        LOGGER.info("Fetching default task status: {}", DEFAULT_STATUS_NAME);
        return taskStatusRepository.findByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> {
                    LOGGER.error("Default TaskStatus not found: {}", DEFAULT_STATUS_NAME);
                    return new RuntimeException("Default TaskStatus not found");
                });
    }

    @Transactional
    public TaskStatus updateTaskStatus(Long id, TaskStatus taskStatus) {
        LOGGER.info("Updating task status with ID: {}", id);
        validateTaskStatus(taskStatus);
        TaskStatus existingTaskStatus = getTaskStatusById(id);
        existingTaskStatus.setName(taskStatus.getName());
        existingTaskStatus.setSlug(taskStatus.getSlug());
        TaskStatus updatedStatus = taskStatusRepository.save(existingTaskStatus);
        LOGGER.info("Updated task status: {}", updatedStatus);
        return updatedStatus;
    }

    @Transactional
    public TaskStatus partialUpdateTaskStatus(Long id, TaskStatusUpdateDto taskStatusUpdateDto) {
        LOGGER.info("Partially updating task status with ID: {}", id);
        TaskStatus taskStatus = getTaskStatusById(id);

        if (taskStatusUpdateDto.getName() != null) {
            taskStatus.setName(taskStatusUpdateDto.getName());
        }

        if (taskStatusUpdateDto.getSlug() != null) {
            taskStatus.setSlug(taskStatusUpdateDto.getSlug());
        }

        if (!taskStatusUpdateDto.hasUpdates()) {
            LOGGER.warn("No updates provided for task status with ID: {}", id);
            throw new IllegalArgumentException("No fields provided for update");
        }

        TaskStatus updatedStatus = taskStatusRepository.save(taskStatus);
        LOGGER.info("Partially updated task status: {}", updatedStatus);
        return updatedStatus;
    }

    @Transactional
    public void deleteTaskStatus(Long id) {
        LOGGER.info("Deleting task status with ID: {}", id);
        if (!taskStatusRepository.existsById(id)) {
            LOGGER.error("TaskStatus not found with ID: {}", id);
            throw new RuntimeException("TaskStatus not found");
        }
        taskStatusRepository.deleteById(id);
        LOGGER.info("Task status with ID: {} deleted successfully.", id);
    }

    private void validateTaskStatus(TaskStatus taskStatus) {
        if (taskStatus.getName() == null || taskStatus.getName().isEmpty()) {
            LOGGER.error("TaskStatus validation failed: name is null or empty.");
            throw new IllegalArgumentException("TaskStatus name must not be null or empty");
        }
        if (taskStatus.getSlug() == null || taskStatus.getSlug().isEmpty()) {
            LOGGER.error("TaskStatus validation failed: slug is null or empty.");
            throw new IllegalArgumentException("TaskStatus slug must not be null or empty");
        }
    }
}
