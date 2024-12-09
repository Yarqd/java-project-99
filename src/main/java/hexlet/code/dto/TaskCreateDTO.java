package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object для создания задачи.
 * Содержит данные, необходимые для создания задачи.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорирует любые неизвестные поля
public class TaskCreateDTO {

    /**
     * Индекс задачи.
     */
    @JsonProperty("index")
    private Integer index;

    /**
     * Название задачи.
     */
    @JsonProperty("title")
    private String name;

    /**
     * Описание задачи.
     */
    @JsonProperty("content")
    private String description;

    /**
     * Идентификатор исполнителя.
     */
    @JsonProperty("assignee_id")
    private Long assigneeId;

    /**
     * Статус задачи (например, "draft", "to_be_fixed").
     */
    @JsonProperty("status")
    private String status;

    /**
     * Идентификатор метки задачи.
     */
    @JsonProperty("task_label")
    private Long taskLabelId;

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
     * Возвращает идентификатор исполнителя.
     *
     * @return идентификатор исполнителя.
     */
    public Long getAssigneeId() {
        return assigneeId;
    }

    /**
     * Устанавливает идентификатор исполнителя.
     *
     * @param assigneeId идентификатор исполнителя.
     */
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * Возвращает статус задачи.
     *
     * @return статус задачи.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус задачи.
     *
     * @param status статус задачи.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Возвращает идентификатор метки задачи.
     *
     * @return идентификатор метки задачи.
     */
    public Long getTaskLabelId() {
        return taskLabelId;
    }

    /**
     * Устанавливает идентификатор метки задачи.
     *
     * @param taskLabelId идентификатор метки задачи.
     */
    public void setTaskLabelId(Long taskLabelId) {
        this.taskLabelId = taskLabelId;
    }

    /**
     * Переопределяет метод toString.
     *
     * @return строковое представление объекта TaskCreateDTO.
     */
    @Override
    public String toString() {
        return "TaskCreateDTO{"
                + "index=" + index
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", assigneeId=" + assigneeId
                + ", status='" + status + '\''
                + ", taskLabelId=" + taskLabelId
                + '}';
    }
}
