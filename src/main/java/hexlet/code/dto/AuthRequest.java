package hexlet.code.dto;

/**
 * DTO для передачи данных аутентификации.
 */
public final class AuthRequest {

    private String username;
    private String password;

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param username имя пользователя.
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
}
