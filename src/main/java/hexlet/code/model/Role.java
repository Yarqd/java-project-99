package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Класс Role представляет сущность роли пользователя в системе.
 * Содержит идентификатор и название роли.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Конструктор по умолчанию.
     */
    public Role() {

    }

    /**
     * Конструктор с параметром.
     *
     * @param name название роли
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Возвращает идентификатор роли.
     *
     * @return идентификатор роли
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор роли.
     *
     * @param id идентификатор роли
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название роли.
     *
     * @return название роли
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название роли.
     *
     * @param name название роли
     */
    public void setName(String name) {
        this.name = name;
    }
}
