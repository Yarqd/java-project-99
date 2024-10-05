package hexlet.code.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Класс User представляет сущность пользователя, которая хранится в базе данных.
 * Содержит основные данные о пользователе, такие как имя, фамилия, email и пароль.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    /**
     * Конструктор с параметрами.
     *
     * @param email email пользователя
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @param password пароль пользователя
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
     * @return идентификатор пользователя.
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя пользователя.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     *
     * @return фамилия пользователя.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия пользователя.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email пользователя.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Возвращает дату создания пользователя.
     *
     * @return дата создания пользователя.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания пользователя.
     *
     * @param createdAt дата создания пользователя.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Возвращает дату обновления пользователя.
     *
     * @return дата обновления пользователя.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Устанавливает дату обновления пользователя.
     *
     * @param updatedAt дата обновления пользователя.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Метод, вызываемый перед сохранением новой записи в базу данных,
     * устанавливает дату создания и обновления.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Метод, вызываемый перед обновлением записи в базе данных,
     * обновляет дату обновления.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
