package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

/**
 * Контроллер для управления метками.
 * Предоставляет методы для получения списка меток, создания, обновления и удаления меток.
 */
@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    private LabelService labelService;

    /**
     * Получает список всех меток.
     *
     * @return HTTP-ответ с списком меток и заголовком X-Total-Count
     */
    @GetMapping
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        LOGGER.info("Fetching all labels");
        List<LabelDTO> labels = labelService.getAllLabels();
        LOGGER.info("Found {} labels", labels.size());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    /**
     * Получает метку по идентификатору.
     *
     * @param id идентификатор метки
     * @return HTTP-ответ с меткой или 404, если метка не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        LOGGER.info("Fetching label with ID: {}", id);
        LabelDTO label = labelService.getLabelById(id);
        return ResponseEntity.ok(label);
    }

    /**
     * Создает новую метку.
     *
     * @param labelDTO объект метки
     * @return HTTP-ответ с созданной меткой и статусом 201
     */
    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(@RequestBody LabelDTO labelDTO) {
        LOGGER.info("Creating new label: {}", labelDTO.getName());
        LabelDTO createdLabel = labelService.createLabel(labelDTO);
        return ResponseEntity.status(201).body(createdLabel);
    }

    /**
     * Обновляет метку по идентификатору.
     *
     * @param id идентификатор метки
     * @param labelDTO объект с новыми данными метки
     * @return HTTP-ответ с обновленной меткой или 404, если метка не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id, @RequestBody LabelDTO labelDTO) {
        LOGGER.info("Updating label with ID: {}", id);
        LabelDTO updated = labelService.updateLabel(id, labelDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Удаляет метку по идентификатору.
     *
     * @param id идентификатор метки
     * @return HTTP-ответ с статусом 204 или сообщение об ошибке
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long id) {
        LOGGER.info("Deleting label with ID: {}", id);
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}
