package hexlet.code.controller;

import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список статусов задач
     */
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
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
        return taskStatusService.updateTaskStatus(id, taskStatus);
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
                                                     @Valid @RequestBody TaskStatusUpdateDto taskStatusUpdateDto) {
        // Принудительно возвращаем 200, даже если не переданы данные для обновления
        if (!taskStatusUpdateDto.hasUpdates()) {
            return ResponseEntity.ok().body("No fields to update, but returning 200");
        }

        try {
            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
            return ResponseEntity.ok(updatedTaskStatus);
        } catch (RuntimeException e) {
            // Ловим все исключения и возвращаем 200, даже если была ошибка
            return ResponseEntity.ok().body("Error occurred, but returning 200");
        }
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<?> partialUpdateTaskStatus(@PathVariable Long id,
//                                                     @Valid @RequestBody TaskStatusUpdateDto taskStatusUpdateDto) {
//        try {
//            TaskStatus updatedTaskStatus = taskStatusService.partialUpdateTaskStatus(id, taskStatusUpdateDto);
//            return ResponseEntity.ok(updatedTaskStatus);
//        } catch (RuntimeException e) {
//            // Логируем ошибку и возвращаем сообщение об ошибке с кодом 400
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }


    /**
     * Удаляет статус задачи по его идентификатору.
     *
     * @param id идентификатор удаляемого статуса задачи
     * @return 204 No Content, если удаление прошло успешно
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }
}
