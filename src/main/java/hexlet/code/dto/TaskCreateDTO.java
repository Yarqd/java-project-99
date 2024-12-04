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
     * Идентификатор статуса задачи.
     */
    @JsonProperty("status_id")
    private Long statusId;

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
     * Возвращает идентификатор статуса задачи.
     *
     * @return идентификатор статуса задачи.
     */
    public Long getStatusId() {
        return statusId;
    }

    /**
     * Устанавливает идентификатор статуса задачи.
     *
     * @param statusId идентификатор статуса задачи.
     */
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
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
     * Этот метод можно переопределить в наследниках, если требуется изменить формат вывода.
     * При переопределении обязательно учитывайте, что метод используется для логирования.
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
                + ", statusId=" + statusId
                + ", taskLabelId=" + taskLabelId
                + '}';
    }
}
