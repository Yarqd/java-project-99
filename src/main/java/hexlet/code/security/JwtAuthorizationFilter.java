package hexlet.code.security;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final Key signingKey;

    /**
     * Конструктор JwtAuthorizationFilter.
     * @param authManager менеджер аутентификации, передаваемый для базовой инициализации
     * @param signingKey ключ для проверки подписи JWT токенов
     */
    public JwtAuthorizationFilter(AuthenticationManager authManager, Key signingKey) {
        super(authManager);
        this.signingKey = signingKey;
    }

    /**
     * Выполняет фильтрацию запроса для проверки JWT токена.
     * Этот метод является final и не должен быть переопределен.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param chain цепочка фильтров
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка в сервлете
     */
    @Override
    protected final void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    /**
     * Извлекает и проверяет токен, возвращая аутентификационные данные.
     *
     * @param token токен авторизации в формате "Bearer ..."
     * @return объект UsernamePasswordAuthenticationToken, если аутентификация успешна, или null, если неудачна
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String user = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();

        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        return null;
    }
}
