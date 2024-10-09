package hexlet.code.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "labels")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(mappedBy = "labels", fetch = FetchType.EAGER) // Жадная загрузка
    private Set<Task> tasks = new HashSet<>();

    // Конструктор с параметром
    public Label(String name) {
        this.name = name;
    }

    // Пустой конструктор
    public Label() {
    }

    /**
     * Возвращает идентификатор метки.
     * @return идентификатор метки
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор метки.
     * @param id идентификатор метки
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название метки.
     * @return название метки
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название метки.
     * @param name название метки
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает дату создания метки.
     * @return дата создания метки
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Возвращает задачи, связанные с меткой.
     * @return задачи, связанные с меткой
     */
    public Set<Task> getTasks() {
        return tasks;
    }

    /**
     * Устанавливает задачи, связанные с меткой.
     * @param tasks задачи, связанные с меткой
     */
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
