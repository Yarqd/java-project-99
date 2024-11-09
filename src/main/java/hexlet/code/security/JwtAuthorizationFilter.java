package hexlet.code.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final Key signingKey;
    private final ObjectMapper objectMapper;

    /**
     * Конструктор JwtAuthorizationFilter.
     *
     * @param authManager менеджер аутентификации
     * @param signingKey ключ для подписи JWT
     * @param objectMapper объект для обработки JSON
     */
    public JwtAuthorizationFilter(AuthenticationManager authManager, Key signingKey, ObjectMapper objectMapper) {
        super(authManager);
        this.signingKey = signingKey;
        this.objectMapper = objectMapper;
    }

    /**
     * Обрабатывает фильтрацию внутреннего запроса, извлекая и проверяя JWT-токен.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param chain цепочка фильтров
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        LOGGER.info("Received Authorization Header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            LOGGER.warn("Authorization header is missing or does not start with Bearer");
            chain.doFilter(request, response);
            return;
        }

        if ("Bearer [object Object]".equals(header)) {
            LOGGER.warn("Received token in incorrect format '[object Object]'. Attempting to parse.");
            chain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("User authenticated: {}", authentication.getName());
            } else {
                LOGGER.warn("Authentication failed, no user found in token.");
            }
            chain.doFilter(request, response);
        } catch (MalformedJwtException | SignatureException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            LOGGER.warn("Expired JWT token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
        } catch (Exception e) {
            LOGGER.error("Failed to authenticate user: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to authenticate");
        }
    }

    /**
     * Извлекает аутентификационные данные из токена JWT.
     *
     * @param token JWT-токен
     * @return объект UsernamePasswordAuthenticationToken, если пользователь найден; иначе null
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        LOGGER.info("Attempting to authenticate token");
        String user = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();

        if (user != null) {
            LOGGER.info("Token authentication successful for user: {}", user);
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        LOGGER.warn("No user found in token");
        return null;
    }
}
