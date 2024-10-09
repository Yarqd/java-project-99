package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс Task представляет задачу в системе, которая имеет статус, исполнителя и может быть связана с метками.
 * Содержит основную информацию о задаче, такую как имя, описание, статус и метки.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer index;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Возвращает идентификатор задачи.
     *
     * @return идентификатор задачи.
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор задачи.
     *
     * @param id идентификатор задачи.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает индекс задачи.
     *
     * @return индекс задачи.
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Устанавливает индекс задачи.
     *
     * @param index индекс задачи.
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Возвращает название задачи.
     *
     * @return название задачи.
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название задачи.
     *
     * @param name название задачи.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает описание задачи.
     *
     * @return описание задачи.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание задачи.
     *
     * @param description описание задачи.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает статус задачи.
     *
     * @return статус задачи.
     */
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    /**
     * Устанавливает статус задачи.
     *
     * @param taskStatus статус задачи.
     */
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * Возвращает исполнителя задачи.
     *
     * @return исполнитель задачи.
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * Устанавливает исполнителя задачи.
     *
     * @param assignee исполнитель задачи.
     */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * Возвращает дату создания задачи.
     *
     * @return дата создания задачи.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Возвращает метки задачи.
     *
     * @return множество меток задачи.
     */
    public Set<Label> getLabels() {
        return labels;
    }

    /**
     * Устанавливает метки задачи.
     *
     * @param labels множество меток задачи.
     */
    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
}
