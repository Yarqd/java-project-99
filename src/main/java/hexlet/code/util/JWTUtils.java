package hexlet.code.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component
public class JWTUtils {

    private final Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Генерация JWT токена.
     *
     * @param username имя пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 дней
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Извлечение имени пользователя из JWT токена.
     *
     * @param token JWT токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Проверка токена на подлинность и актуальность.
     *
     * @param token JWT токен
     * @param userDetails объект пользователя из UserDetailsService
     * @return true если токен действителен, иначе false
     */
    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Проверка срока действия токена.
     *
     * @param token JWT токен
     * @return true если токен истек, иначе false
     */
    private boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    /**
     * Получение claims (требований) из токена.
     *
     * @param token JWT токен
     * @return Claims объект, содержащий данные токена
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
