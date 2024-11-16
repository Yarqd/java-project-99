package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO для передачи данных аутентификации.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AuthRequest {

    private String username;
    private String password;
    private String firstName; // Добавлено поле firstName
    private String lastName; // Добавлено поле lastName

    /**
     * Возвращает username пользователя.
     *
     * @return username пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает username пользователя.
     *
     * @param username username пользователя.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Возвращает пароль.
     *
     * @return пароль.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль.
     *
     * @param password пароль.
     */
    public void setPassword(String password) {
        this.password = password;
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
}
