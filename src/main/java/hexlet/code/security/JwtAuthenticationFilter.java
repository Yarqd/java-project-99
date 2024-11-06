package hexlet.code.security;

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
import java.security.Key;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final Key signingKey;

    /**
     * Конструктор JwtAuthenticationFilter.
     * @param authenticationManager менеджер аутентификации для проверки учетных данных
     * @param signingKey ключ для подписи JWT токенов
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, Key signingKey) {
        this.authenticationManager = authenticationManager;
        this.signingKey = signingKey;
        setFilterProcessesUrl("/api/login");  // Указываем путь для логина
    }

    /**
     * Попытка аутентификации пользователя на основе переданных учетных данных.
     * Этот метод не предназначен для расширения.
     *
     * @param request HTTP-запрос, содержащий учетные данные пользователя
     * @param response HTTP-ответ
     * @return объект Authentication, представляющий аутентификационные данные пользователя
     * @throws AuthenticationException если аутентификация не удалась
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            UserLoginRequest loginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Обработка успешной аутентификации, создавая и отправляя JWT токен в ответе.
     * Этот метод является final и не должен быть переопределен.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ, в который добавляется токен
     * @param chain цепочка фильтров
     * @param authResult результат аутентификации, содержащий данные о пользователе
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected final void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain chain, Authentication authResult) throws IOException {
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

    /**
     * Вспомогательный класс для получения данных пользователя из запроса.
     */
    static class UserLoginRequest {
        private String username;
        private String password;

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
