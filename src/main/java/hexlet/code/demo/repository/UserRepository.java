package hexlet.code.demo.repository;

import hexlet.code.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Метод для поиска пользователя по email
    Optional<User> findByEmail(String email);
}
