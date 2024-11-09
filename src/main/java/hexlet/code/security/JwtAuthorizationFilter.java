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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final Key signingKey;
    private final ObjectMapper objectMapper;

    public JwtAuthorizationFilter(AuthenticationManager authManager, Key signingKey, ObjectMapper objectMapper) {
        super(authManager);
        this.signingKey = signingKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        // Логируем полученный заголовок Authorization
        logger.info("Received Authorization Header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Authorization header is missing or does not start with Bearer");
            chain.doFilter(request, response);
            return;
        }

        // Проверяем, если заголовок содержит '[object Object]', пытаемся преобразовать его
        if ("Bearer [object Object]".equals(header)) {
            logger.warn("Received token in incorrect format '[object Object]'. Attempting to parse.");
            chain.doFilter(request, response);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("User authenticated: {}", authentication.getName());
            } else {
                logger.warn("Authentication failed, no user found in token.");
            }
            chain.doFilter(request, response);
        } catch (MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
        } catch (Exception e) {
            logger.error("Failed to authenticate user: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to authenticate");
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        logger.info("Attempting to authenticate token");
        String user = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();

        if (user != null) {
            logger.info("Token authentication successful for user: {}", user);
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
        logger.warn("No user found in token");
        return null;
    }
}
