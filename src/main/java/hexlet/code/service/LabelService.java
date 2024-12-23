package hexlet.code.service;

import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления метками.
 */
@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Получает список всех меток.
     *
     * @return Список DTO меток.
     */
    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает метку по идентификатору.
     *
     * @param id Идентификатор метки.
     * @return DTO метки.
     * @throws RuntimeException Если метка не найдена.
     */
    public LabelDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found with ID: " + id));
        return new LabelDTO(label.getId(), label.getName(), label.getCreatedAt());
    }

    /**
     * Создает новую метку.
     *
     * @param labelDTO DTO метки.
     * @return DTO созданной метки.
     */
    public LabelDTO createLabel(LabelDTO labelDTO) {
        Label label = new Label();
        label.setName(labelDTO.getName());
        Label savedLabel = labelRepository.save(label);
        return convertToDTO(savedLabel);
    }

    /**
     * Обновляет существующую метку.
     *
     * @param id Идентификатор метки.
     * @param labelDTO DTO метки с обновленными данными.
     * @return DTO обновленной метки.
     * @throws RuntimeException Если метка не найдена.
     */
    public LabelDTO updateLabel(Long id, LabelDTO labelDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with ID: " + id));
        label.setName(labelDTO.getName());
        Label savedLabel = labelRepository.save(label);
        return convertToDTO(savedLabel);
    }

    /**
     * Удаляет метку.
     *
     * @param id Идентификатор метки.
     * @throws RuntimeException Если метка не найдена или связана с задачами.
     */
    public void deleteLabel(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Метка не найдена"));
        if (!label.getTasks().isEmpty()) {
            throw new RuntimeException("Нельзя удалить метку, она связана с задачами.");
        }
        labelRepository.delete(label);
    }

    /**
     * Преобразует объект Label в DTO.
     *
     * @param label Объект метки.
     * @return DTO метки.
     */
    private LabelDTO convertToDTO(Label label) {
        return new LabelDTO(label.getId(), label.getName(), label.getCreatedAt());
    }
}
