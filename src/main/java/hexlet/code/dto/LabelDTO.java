package hexlet.code.dto;

import java.time.LocalDateTime;

public class LabelDTO {

    private Long id;
    private String name;
    private LocalDateTime createdAt;

    // Пустой конструктор
    public LabelDTO() {
    }

    // Конструктор с параметрами
    public LabelDTO(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * Возвращает идентификатор метки.
     *
     * @return идентификатор метки
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор метки.
     *
     * @param id идентификатор метки
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название метки.
     *
     * @return название метки
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название метки.
     *
     * @param name название метки
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает дату создания метки.
     *
     * @return дата создания метки
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания метки.
     *
     * @param createdAt дата создания метки
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
