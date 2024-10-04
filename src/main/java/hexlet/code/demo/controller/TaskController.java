package hexlet.code.demo.controller;

import hexlet.code.demo.model.Task;
import hexlet.code.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Возвращает список задач с возможностью фильтрации по заголовку, назначенному пользователю, статусу и метке.
     *
     * @param titleCont  часть заголовка задачи
     * @param assigneeId идентификатор исполнителя задачи
     * @param status     статус задачи
     * @param labelId    идентификатор метки задачи
     * @return список задач, удовлетворяющих условиям фильтрации
     */
    @GetMapping
    public List<Task> getTasks(
            @RequestParam(required = false) String titleCont,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long labelId) {
        return taskRepository.findTasksByFilters(titleCont, assigneeId, status, labelId);
    }

    /**
     * Возвращает задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача с указанным идентификатором или 404, если задача не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создает новую задачу.
     *
     * @param task объект задачи
     * @return созданная задача
     */
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    /**
     * Обновляет данные существующей задачи.
     *
     * @param id          идентификатор задачи
     * @param updatedTask обновленные данные задачи
     * @return обновленная задача или 404, если задача не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setName(updatedTask.getName());
                    task.setDescription(updatedTask.getDescription());
                    task.setTaskStatus(updatedTask.getTaskStatus());
                    task.setAssignee(updatedTask.getAssignee());
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return 204 No Content, если задача успешно удалена, или 404, если задача не найдена
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
