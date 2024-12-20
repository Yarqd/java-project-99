package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserService userService;

    /**
     * Получение списка задач.
     *
     * @return Список задач в формате JSON.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        LOGGER.info("Fetching all tasks");
        List<Map<String, Object>> tasks = taskRepository.findAll()
                .stream()
                .peek(task -> Hibernate.initialize(task.getLabels())) // Инициализация labels
                .map(this::formatTaskResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    /**
     * Получение задачи по ID.
     *
     * @param id ID задачи.
     * @return Задача в формате JSON.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);
        Task task = taskRepository.findTaskWithLabelsById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Hibernate.initialize(task.getLabels()); // Инициализация labels
        return ResponseEntity.ok(formatTaskResponse(task));
    }

    /**
     * Создание новой задачи.
     *
     * @param taskCreateDTO DTO с данными для создания задачи.
     * @return Созданная задача в формате JSON.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Object> createTask(@RequestBody @Valid TaskCreateDTO taskCreateDTO) {
        LOGGER.info("Creating new task: {}", taskCreateDTO);

        Task task = new Task();
        task.setName(taskCreateDTO.getName());
        task.setDescription(taskCreateDTO.getDescription());

        // Устанавливаем индекс задачи
        if (taskCreateDTO.getIndex() == null) {
            int nextIndex = (int) taskRepository.count() + 1;
            task.setIndex(nextIndex);
        } else {
            task.setIndex(taskCreateDTO.getIndex().intValue());
        }

        // Устанавливаем исполнителя задачи
        if (taskCreateDTO.getAssigneeId() != null) {
            User assignee = getAssignee(taskCreateDTO.getAssigneeId());
            if (assignee == null) {
                return ResponseEntity.badRequest().body("Assignee not found");
            }
            task.setAssignee(assignee);
        }

        // Устанавливаем статус задачи
        if (taskCreateDTO.getStatus() != null) {
            TaskStatus taskStatus = taskStatusService.getTaskStatusBySlug(taskCreateDTO.getStatus());
            task.setTaskStatus(taskStatus);
        } else {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            task.setTaskStatus(defaultStatus);
        }

        // Устанавливаем метки задачи
        if (taskCreateDTO.getTaskLabelIds() != null && !taskCreateDTO.getTaskLabelIds().isEmpty()) {
            Set<Label> labels = taskCreateDTO.getTaskLabelIds().stream()
                    .map(id -> labelRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Label not found: " + id)))
                    .collect(Collectors.toSet());
            task.setLabels(labels);
        }

        Task createdTask = taskRepository.save(task);
        LOGGER.info("Task created successfully: {}", createdTask);

        return ResponseEntity.status(201).body(formatTaskResponse(createdTask));
    }

    /**
     * Обновление задачи по ID.
     *
     * @param id            ID задачи.
     * @param taskCreateDTO DTO с данными для обновления задачи.
     * @return Обновлённая задача в формате JSON.
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> updateTask(@PathVariable Long id, @RequestBody @Valid TaskCreateDTO taskCreateDTO) {
        LOGGER.info("Updating task with ID: {}", id);

        // Проверяем существование задачи
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        // Обновляем поля задачи только если они переданы
        if (taskCreateDTO.getName() != null) {
            existingTask.setName(taskCreateDTO.getName());
        }
        if (taskCreateDTO.getDescription() != null) {
            existingTask.setDescription(taskCreateDTO.getDescription());
        }
        if (taskCreateDTO.getStatus() != null) {
            TaskStatus taskStatus = taskStatusService.getTaskStatusBySlug(taskCreateDTO.getStatus());
            existingTask.setTaskStatus(taskStatus);
        }
        if (taskCreateDTO.getTaskLabelIds() != null && !taskCreateDTO.getTaskLabelIds().isEmpty()) {
            Set<Label> labels = taskCreateDTO.getTaskLabelIds().stream()
                    .map(labelId -> labelRepository.findById(labelId)
                            .orElseThrow(() -> new RuntimeException("Label not found: " + labelId)))
                    .collect(Collectors.toSet());
            existingTask.setLabels(labels);
        }
        if (taskCreateDTO.getAssigneeId() != null) {
            User assignee = getAssignee(taskCreateDTO.getAssigneeId());
            existingTask.setAssignee(assignee);
        }

        // Сохраняем изменения
        Task updatedTask = taskRepository.save(existingTask);
        LOGGER.info("Task updated successfully: {}", updatedTask);

        return ResponseEntity.ok(formatTaskResponse(updatedTask));
    }

    /**
     * Удаление задачи по ID.
     *
     * @param id ID задачи.
     * @return Сообщение об успешном удалении.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        LOGGER.info("Deleting task with ID: {}", id);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        taskRepository.delete(existingTask);
        LOGGER.info("Task deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    private User getAssignee(Long assigneeId) {
        return userService.findUserById(assigneeId);
    }

    private Map<String, Object> formatTaskResponse(Task task) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", task.getId());
        response.put("index", task.getIndex());
        response.put("createdAt", task.getCreatedAt());
        response.put("assignee_id", task.getAssignee() != null ? task.getAssignee().getId() : null);
        response.put("title", task.getName());
        response.put("content", task.getDescription());
        response.put("status", task.getTaskStatus() != null ? task.getTaskStatus().getSlug() : null);
        response.put("taskLabelIds", task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        return response;
    }
}
