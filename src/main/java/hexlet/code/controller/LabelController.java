package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    private LabelRepository labelRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<LabelDTO> getAllLabels() {
        LOGGER.info("Fetching all labels");
        List<LabelDTO> labels = labelRepository.findAll().stream()
                .map(label -> new LabelDTO(label.getId(), label.getName(), label.getCreatedAt()))
                .collect(Collectors.toList());
        LOGGER.info("Found {} labels", labels.size());
        return labels;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        LOGGER.info("Fetching label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    LabelDTO labelDTO = new LabelDTO(label.getId(), label.getName(), label.getCreatedAt());
                    LOGGER.info("Label found: {}", labelDTO);
                    return ResponseEntity.ok(labelDTO);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Label with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> createLabel(@RequestBody Label label) {
        LOGGER.info("Received POST request to create Label: {}", label);
        Label savedLabel = labelRepository.save(label);
        LabelDTO labelDTO = new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.getCreatedAt());
        LOGGER.info("Label created successfully: {}", labelDTO);
        return ResponseEntity.status(201).body(labelDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id, @RequestBody Label updatedLabel) {
        LOGGER.info("Received PUT request to update Label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    LOGGER.info("Label found: {}", label);
                    label.setName(updatedLabel.getName());
                    Label savedLabel = labelRepository.save(label);
                    LabelDTO labelDTO = new LabelDTO(savedLabel.getId(), savedLabel.getName(), savedLabel.getCreatedAt());
                    LOGGER.info("Label updated successfully: {}", labelDTO);
                    return ResponseEntity.ok(labelDTO);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Label with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteLabel(@PathVariable Long id) {
        LOGGER.info("Received DELETE request to remove Label with ID: {}", id);
        return labelRepository.findById(id)
                .map(label -> {
                    if (!label.getTasks().isEmpty()) {
                        LOGGER.warn("Label with ID: {} is associated with tasks and cannot be deleted", id);
                        return ResponseEntity.badRequest().body("Нельзя удалить метку, она связана с задачами.");
                    }
                    labelRepository.delete(label);
                    LOGGER.info("Label with ID: {} deleted successfully", id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    LOGGER.warn("Label with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
