package hexlet.code.demo.repository;

import hexlet.code.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {
}
