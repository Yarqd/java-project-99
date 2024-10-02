package hexlet.code.demo.dto;

import java.time.LocalDateTime;

public class LabelDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    // Конструкторы
    public LabelDTO() {
    }

    public LabelDTO(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
