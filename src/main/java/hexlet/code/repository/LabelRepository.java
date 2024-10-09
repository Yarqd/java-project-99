package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {

    @EntityGraph(attributePaths = "tasks")  // Подгружаем связанные задачи
    Optional<Label> findById(Long id);
}
