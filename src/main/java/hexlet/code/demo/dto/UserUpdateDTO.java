package hexlet.code.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @Email(message = "Неверный формат email")
    private String email;

    private String firstName;
    private String lastName;

    @Size(min = 3, message = "Пароль должен содержать как минимум 3 символа")
    private String password;

    /**
     * Возвращает email пользователя.
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает email пользователя.
     * @param email новый email пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает имя пользователя.
     * @return имя пользователя
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя пользователя.
     * @param firstName новое имя пользователя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     * @return фамилия пользователя
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию пользователя.
     * @param lastName новая фамилия пользователя
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает пароль пользователя.
     * @return пароль пользователя
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя.
     * @param password новый пароль пользователя
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
