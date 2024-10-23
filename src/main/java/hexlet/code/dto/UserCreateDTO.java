package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserCreateDTO {

    private Long id; // Добавляем поле id

    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email обязателен")
    private String email;

    private String firstName;
    private String lastName;

    @Size(min = 3, message = "Пароль должен содержать как минимум 3 символа")
    private String password;

    // Геттеры и сеттеры для id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Остальные геттеры и сеттеры для других полей
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCreateDTO{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", password='[PROTECTED]'"
                + '}';
    }
}
