package hexlet.code.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login");  // Указываем путь для логина
    }

    /**
     * Попытка аутентификации пользователя.
     *
     * @param request  запрос клиента
     * @param response ответ сервера
     * @return результат аутентификации
     * @throws AuthenticationException в случае ошибки аутентификации
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // Получаем данные из тела запроса
            UserLoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(),
                    UserLoginRequest.class);

            // Создаем токен для аутентификации
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // Возвращаем результат аутентификации
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Успешная аутентификация.
     * Генерация JWT токена и возврат его в ответе.
     *
     * @param request     запрос клиента
     * @param response    ответ сервера
     * @param chain       цепочка фильтров
     * @param authResult  результат аутентификации
     * @throws IOException в случае ошибки ввода-вывода
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        // Генерируем JWT токен
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))  // Токен на 10 дней
                .signWith(SignatureAlgorithm.HS512, "SecretKeyToGenJWTs")  // Подпись токена
                .compact();

        // Возвращаем токен в ответе
        response.addHeader("Authorization", "Bearer " + token);
    }

    // DTO для запроса логина
    static class UserLoginRequest {
        private String username;
        private String password;

        // Геттеры и сеттеры
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
