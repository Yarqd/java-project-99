package hexlet.code.app.repository;

import hexlet.code.app.model.Task;

import java.util.List;

public interface TaskRepositoryCustom {
    List<Task> findTasksByFilters(String titleCont, Long assigneeId, String status, Long labelId);
}
