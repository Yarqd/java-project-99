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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с задачами.
 */
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

    @PostMapping
    public ResponseEntity<Object> createTask(@RequestBody TaskCreateDTO taskCreateDTO) {
        LOGGER.info("Creating new task: {}", taskCreateDTO);

        if (taskCreateDTO.getName() == null || taskCreateDTO.getName().isEmpty()) {
            LOGGER.error("Task name is required");
            return ResponseEntity.badRequest().body("Task name is required");
        }

        Task task = new Task();
        task.setName(taskCreateDTO.getName());
        task.setDescription(taskCreateDTO.getDescription());
        task.setIndex(taskCreateDTO.getIndex());

        if (taskCreateDTO.getAssigneeId() != null) {
            User assignee = getAssignee(taskCreateDTO.getAssigneeId());
            if (assignee == null) {
                return ResponseEntity.badRequest().body("Assignee not found");
            }
            task.setAssignee(assignee);
        }

        if (taskCreateDTO.getStatus() != null) {
            TaskStatus taskStatus = taskStatusService.getTaskStatusByName(taskCreateDTO.getStatus());
            if (taskStatus == null) {
                LOGGER.error("TaskStatus not found with name: {}", taskCreateDTO.getStatus());
                return ResponseEntity.badRequest().body("TaskStatus not found");
            }
            task.setTaskStatus(taskStatus);
        } else {
            TaskStatus defaultStatus = taskStatusService.getDefaultTaskStatus();
            task.setTaskStatus(defaultStatus);
        }

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
        try {
            return userService.findUserById(assigneeId);
        } catch (RuntimeException e) {
            LOGGER.error("Assignee not found with ID: {}", assigneeId);
            return null;
        }
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
