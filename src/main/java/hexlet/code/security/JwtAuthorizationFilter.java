package hexlet.code.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    /**
     * Фильтрует запросы, проверяя наличие и корректность JWT токена в заголовке Authorization.
     *
     * @param request запрос
     * @param response ответ
     * @param chain цепочка фильтров
     * @throws IOException если возникает ошибка ввода-вывода
     * @throws ServletException если возникает ошибка фильтрации
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        // Проверяем наличие заголовка Authorization и корректность токена
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Получаем аутентификацию из токена
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        // Устанавливаем аутентификацию в контексте безопасности, если она найдена
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Продолжаем выполнение цепочки фильтров
        chain.doFilter(request, response);
    }

    /**
     * Извлекает аутентификацию из JWT токена.
     *
     * @param request запрос
     * @return объект аутентификации или null, если токен некорректный
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            // Парсим JWT токен и извлекаем пользователя
            String user = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor("SecretKeyToGenJWTs".getBytes(StandardCharsets.UTF_8)))
                    // Ключ подписи
                    .build()
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();

            // Если пользователь найден, возвращаем объект аутентификации
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
