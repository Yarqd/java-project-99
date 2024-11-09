package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс User представляет сущность пользователя, которая хранится в базе данных.
 * Содержит основные данные о пользователе, такие как имя, фамилия, email, пароль и роли.
 */
@Entity
@Table(name = "users")
public final class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    @Column(updatable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Конструктор с параметрами.
     *
     * @param email     email пользователя
     * @param firstName имя пользователя
     * @param lastName  фамилия пользователя
     * @param password  пароль пользователя
     */
    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     * Пустой конструктор по умолчанию для JPA.
     */
    public User() {
    }

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return id пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя пользователя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     *
     * @return фамилия пользователя
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия пользователя
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Возвращает дату создания записи пользователя.
     *
     * @return дата создания записи
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания записи пользователя.
     *
     * @param createdAt дата создания записи
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Возвращает дату последнего обновления записи пользователя.
     *
     * @return дата последнего обновления
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Устанавливает дату последнего обновления записи пользователя.
     *
     * @param updatedAt дата последнего обновления
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Возвращает набор ролей пользователя.
     *
     * @return набор ролей пользователя
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Устанавливает набор ролей пользователя.
     *
     * @param roles набор ролей пользователя
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Метод, вызываемый перед сохранением новой записи в базу данных,
     * устанавливает дату создания и обновления.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Метод, вызываемый перед обновлением записи в базе данных,
     * обновляет дату обновления.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
