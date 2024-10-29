package hexlet.code.dto;

import java.time.Instant;

public class LabelDTO {

    private Long id;
    private String name;
    private Instant createdAt;

    // Пустой конструктор
    public LabelDTO() {
    }

    // Конструктор с параметрами
    public LabelDTO(Long id, String name, Instant createdAt) {
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
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания метки.
     *
     * @param createdAt дата создания метки
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
