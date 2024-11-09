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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final Key signingKey;

    /**
     * Конструктор JwtAuthenticationFilter.
     *
     * @param authenticationManager менеджер аутентификации
     * @param signingKey ключ для подписи JWT
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, Key signingKey) {
        this.authenticationManager = authenticationManager;
        this.signingKey = signingKey;
        setFilterProcessesUrl("/api/login");
    }

    /**
     * Попытка аутентификации пользователя с использованием предоставленных данных.
     *
     * @param request  запрос, содержащий данные пользователя
     * @param response ответ для обработки
     * @return объект Authentication после успешной аутентификации
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
            LOGGER.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Обработка успешной аутентификации пользователя.
     *
     * @param request    запрос пользователя
     * @param response   ответ сервера
     * @param chain      цепочка фильтров
     * @param authResult результат аутентификации
     * @throws IOException если возникла ошибка ввода-вывода
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

        LOGGER.info("User {} authenticated successfully", authResult.getName());
    }

    /**
     * Вспомогательный класс для хранения данных запроса на аутентификацию.
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
