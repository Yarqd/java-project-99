package hexlet.code.demo.controller;

import hexlet.code.demo.dto.LabelDTO;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @GetMapping
    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(label -> new LabelDTO(label.getId(), label.getName(), label.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        return labelRepository.findById(id)
                .map(label -> ResponseEntity.ok(new LabelDTO(label.getId(), label.getName(), label.getCreatedAt())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(@RequestBody Label label) {
        Label savedLabel = labelRepository.save(label);
        return ResponseEntity.status(201).body(new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.getCreatedAt()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id, @RequestBody Label updatedLabel) {
        return labelRepository.findById(id)
                .map(label -> {
                    label.setName(updatedLabel.getName());
                    Label savedLabel = labelRepository.save(label);
                    return ResponseEntity.ok(new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.getCreatedAt()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long id) {
        return labelRepository.findById(id)
                .map(label -> {
                    // Проверка, связана ли метка с задачами
                    if (!label.getTasks().isEmpty()) {
                        return ResponseEntity.badRequest()
                                .body("Cannot delete label, it is associated with tasks.");
                    }

                    labelRepository.delete(label);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
