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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<Map<String, Object>>> getTasks() {
        LOGGER.info("Fetching all tasks");
        List<Map<String, Object>> tasks = taskRepository.findAll()
                .stream()
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
    public ResponseEntity<Object> getTaskById(@PathVariable Long id) {
        LOGGER.info("Fetching task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return ResponseEntity.ok(formatTaskResponse(task));
    }

    /**
     * Создание новой задачи.
     *
     * @param taskCreateDTO DTO с данными для создания задачи.
     * @return Созданная задача в формате JSON.
     */
    @PostMapping
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
            TaskStatus taskStatus = taskStatusService.getTaskStatusByName(taskCreateDTO.getStatus());
            if (taskStatus == null) {
                LOGGER.error("TaskStatus not found: {}", taskCreateDTO.getStatus());
                return ResponseEntity.badRequest().body("TaskStatus not found: " + taskCreateDTO.getStatus());
            }
            task.setTaskStatus(taskStatus);
        } else {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            if (defaultStatus == null) {
                LOGGER.error("Default TaskStatus not found");
                return ResponseEntity.internalServerError().body("Default TaskStatus not found");
            }
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
        response.put("status", task.getTaskStatus() != null ? task.getTaskStatus().getName() : null);
        response.put("labels", task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        return response;
    }
}
