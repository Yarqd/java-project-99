package hexlet.code.demo.repository;

import hexlet.code.demo.model.Task;

import java.util.List;

public interface TaskRepositoryCustom {
    List<Task> findTasksByFilters(String titleCont, Long assigneeId, String status, Long labelId);
}
