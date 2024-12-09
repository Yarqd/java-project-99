package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findBySlug(String slug);

    Optional<TaskStatus> findByName(String name);

    boolean existsBySlug(String slug);

    Optional<TaskStatus> findBySlugIgnoreCase(String slug);
}
