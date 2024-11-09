package hexlet.code.repository;

import hexlet.code.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Поиск роли по ее имени.
     * @param name имя роли.
     * @return Опционально возвращает роль, если она найдена.
     */
    Optional<Role> findByName(String name);
}
