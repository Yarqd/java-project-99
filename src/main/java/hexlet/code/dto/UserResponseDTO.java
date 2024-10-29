package hexlet.code.dto;

import java.time.Instant;

/**
 * Класс UserResponseDTO представляет собой объект передачи данных (DTO) для пользователя.
 * Этот класс предназначен для передачи данных о пользователе между слоями приложения.
 */
public class UserResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Instant createdAt;

    /**
     * Возвращает идентификатор пользователя.
     * @return идентификатор пользователя.
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     * @param id идентификатор пользователя.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     * @return имя пользователя.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     * @param firstName имя пользователя.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     * @return фамилия пользователя.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     * @param lastName фамилия пользователя.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает email пользователя.
     * @return email пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     * @param email email пользователя.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает дату создания пользователя.
     * @return дата создания пользователя.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает дату создания пользователя.
     * @param createdAt дата создания пользователя.
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
