package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Возвращает список всех меток.
     * @return Список DTO всех меток
     */
    @GetMapping
    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(label -> new LabelDTO(label.getId(), label.getName(), label.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает метку по ее идентификатору.
     * @param id Идентификатор метки
     * @return DTO метки или 404, если метка не найдена
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        return labelRepository.findById(id)
                .map(label -> ResponseEntity.ok(new LabelDTO(label.getId(), label.getName(), label.getCreatedAt())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создает новую метку.
     * @param label Объект метки для создания
     * @return DTO созданной метки с кодом 201
     */
    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(@RequestBody Label label) {
        Label savedLabel = labelRepository.save(label);
        return ResponseEntity.status(201).body(new LabelDTO(savedLabel.getId(), savedLabel.getName(),
                savedLabel.getCreatedAt()));
    }

    /**
     * Обновляет существующую метку.
     * @param id Идентификатор метки
     * @param updatedLabel Обновленные данные метки
     * @return DTO обновленной метки или 404, если метка не найдена
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id, @RequestBody Label updatedLabel) {
        return labelRepository.findById(id)
                .map(label -> {
                    label.setName(updatedLabel.getName());
                    Label savedLabel = labelRepository.save(label);
                    return ResponseEntity.ok(new LabelDTO(savedLabel.getId(), savedLabel.getName(),
                            savedLabel.getCreatedAt()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаляет метку, если она не связана с задачами.
     * @param id Идентификатор метки
     * @return Статус 204 при успешном удалении, 400 если метка связана с задачами, или 404 если метка не найдена
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long id) {
        return labelRepository.findById(id)
                .map(label -> {
                    // Проверка, связана ли метка с задачами
                    if (!label.getTasks().isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body("Нельзя удалить метку, она связана с задачами.");
                    }

                    labelRepository.delete(label);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
