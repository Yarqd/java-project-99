package hexlet.code.repository;

import hexlet.code.model.Task;

import java.util.List;

public interface TaskRepositoryCustom {
    List<Task> findTasksByFilters(String titleCont, Long assigneeId, String status, Long labelId);
}
