package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Модель для представления статусов задач.
 */
@Entity
@Table(name = "task_statuses")
public class TaskStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Конструктор по умолчанию.
     */
    public TaskStatus() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param name название статуса
     * @param slug уникальный идентификатор статуса
     */
    public TaskStatus(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    /**
     * Устанавливает дату создания статуса перед сохранением.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    /**
     * Возвращает идентификатор статуса.
     *
     * @return идентификатор статуса
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает название статуса.
     *
     * @return название статуса
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает новое название статуса.
     *
     * @param name новое название статуса
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает уникальный идентификатор статуса.
     *
     * @return уникальный идентификатор статуса
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Устанавливает новый уникальный идентификатор статуса.
     *
     * @param slug новый уникальный идентификатор
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Возвращает дату создания статуса.
     *
     * @return дата создания статуса
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
