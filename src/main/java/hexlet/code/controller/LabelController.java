package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.stream.Collectors;

/**
 * Контроллер для управления метками.
 * Предоставляет методы для получения списка меток, создания, обновления и удаления меток.
 * Класс финализирован, чтобы предотвратить нежелательное наследование.
 */
@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Получает список всех меток.
     *
     * @return HTTP-ответ с списком меток и заголовком X-Total-Count
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        LOGGER.info("Fetching all labels");
        List<LabelDTO> labels = labelRepository.findAll().stream()
                .map(label -> new LabelDTO(label.getId(), label.getName(), label.getCreatedAt()))
                .collect(Collectors.toList());
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        LOGGER.info("Fetching label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    LabelDTO labelDTO = new LabelDTO(label.getId(), label.getName(), label.getCreatedAt());
                    return ResponseEntity.ok(labelDTO);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Создает новую метку.
     *
     * @param label объект метки
     * @return HTTP-ответ с созданной меткой и статусом 201
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> createLabel(@RequestBody Label label) {
        LOGGER.info("Creating new label: {}", label);
        Label savedLabel = labelRepository.save(label);
        LabelDTO labelDTO = new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.getCreatedAt());
        return ResponseEntity.status(201).body(labelDTO);
    }

    /**
     * Обновляет метку по идентификатору.
     *
     * @param id идентификатор метки
     * @param updatedLabel объект с новыми данными метки
     * @return HTTP-ответ с обновленной меткой или 404, если метка не найдена
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id, @RequestBody Label updatedLabel) {
        LOGGER.info("Updating label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    label.setName(updatedLabel.getName());
                    Label savedLabel = labelRepository.save(label);
                    LabelDTO labelDTO = new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.
                            getCreatedAt());
                    return ResponseEntity.ok(labelDTO);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Удаляет метку по идентификатору.
     *
     * @param id идентификатор метки
     * @return HTTP-ответ с статусом 204 или сообщение об ошибке
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteLabel(@PathVariable Long id) {
        LOGGER.info("Deleting label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    if (!label.getTasks().isEmpty()) {
                        return ResponseEntity.badRequest().body("Нельзя удалить метку, она связана с задачами.");
                    }
                    labelRepository.delete(label);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
