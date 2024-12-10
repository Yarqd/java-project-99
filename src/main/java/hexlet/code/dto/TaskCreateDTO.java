package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/**
 * Data Transfer Object для создания задачи.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateDTO {

    @JsonProperty("index")
    private Integer index;

    @JsonProperty("title")
    private String name;

    @JsonProperty("content")
    private String description;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("task_label_ids")
    private Set<Long> taskLabelIds;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Long> getTaskLabelIds() {
        return taskLabelIds;
    }

    public void setTaskLabelIds(Set<Long> taskLabelIds) {
        this.taskLabelIds = taskLabelIds;
    }
}
