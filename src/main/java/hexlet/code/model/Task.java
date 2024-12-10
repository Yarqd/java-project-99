package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс Task представляет задачу в системе, которая имеет статус, исполнителя и может быть связана с метками.
 */
@Entity
@Table(name = "tasks")
public final class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer index;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @JsonProperty("assignee_id")
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @JsonProperty("title")
    @Column(nullable = false)
    private String name;

    @JsonProperty("content")
    @Column
    private String description;

    @JsonProperty("status")
    @ManyToOne(optional = false)
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @JsonProperty("taskLabelIds")
    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
}
