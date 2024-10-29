package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

@Entity
@Table(name = "TasksStatuses")
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Конструктор по умолчанию
    public TaskStatus() {
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
        this.createdAt = Instant.now();
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
    public Instant getCreatedAt() {
        return createdAt;
    }
}
