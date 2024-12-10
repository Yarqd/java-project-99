package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
    @Query("SELECT COUNT(t) FROM Task t")
    int countAllTasks();
}
