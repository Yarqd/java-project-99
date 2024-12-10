package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/**
 * DTO для создания задачи.
 * <p>
 * Этот класс содержит данные для создания задачи.
 * При наследовании добавьте обработку нестандартных случаев использования.
 * </p>
 */
public class TaskCreateDTO {

    private Long index;

    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String status;

    @JsonProperty("taskLabelIds")
    private Set<Long> taskLabelIds;

    /**
     * Получение индекса задачи.
     *
     * @return Индекс задачи.
     */
    public Long getIndex() {
        return index;
    }

    /**
     * Установка индекса задачи.
     *
     * @param index Индекс задачи.
     */
    public void setIndex(Long index) {
        this.index = index;
    }

    /**
     * Получение названия задачи.
     *
     * @return Название задачи.
     */
    public String getName() {
        return name;
    }

    /**
     * Установка названия задачи.
     *
     * @param name Название задачи.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получение описания задачи.
     *
     * @return Описание задачи.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Установка описания задачи.
     *
     * @param description Описание задачи.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Получение ID исполнителя задачи.
     *
     * @return ID исполнителя задачи.
     */
    public Long getAssigneeId() {
        return assigneeId;
    }

    /**
     * Установка ID исполнителя задачи.
     *
     * @param assigneeId ID исполнителя задачи.
     */
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * Получение статуса задачи.
     *
     * @return Статус задачи.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Установка статуса задачи.
     *
     * @param status Статус задачи.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Получение меток задачи.
     *
     * @return Набор меток задачи.
     */
    public Set<Long> getTaskLabelIds() {
        return taskLabelIds;
    }

    /**
     * Установка меток задачи.
     *
     * @param taskLabelIds Набор меток задачи.
     */
    public void setTaskLabelIds(Set<Long> taskLabelIds) {
        this.taskLabelIds = taskLabelIds;
    }
}
