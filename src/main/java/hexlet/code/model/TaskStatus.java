package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Конструктор по умолчанию
    public TaskStatus() {
        // оставляем пустым, так как createdAt будет заполняться через @PrePersist
    }

    // Конструктор с параметрами
    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    /**
     * Устанавливает дату создания перед сохранением сущности.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Возвращает идентификатор статуса задачи.
     * @return идентификатор статуса задачи
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает название статуса задачи.
     * @return название статуса задачи
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название статуса задачи.
     * @param name название статуса задачи
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает slug (уникальный идентификатор) статуса задачи.
     * @return slug статуса задачи
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Устанавливает slug статуса задачи.
     * @param slug slug статуса задачи
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Возвращает дату создания статуса задачи.
     * @return дата создания статуса задачи
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
