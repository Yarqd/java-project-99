package hexlet.code.config;

import hexlet.code.security.JwtAuthenticationFilter;
import hexlet.code.security.JwtAuthorizationFilter;
import hexlet.code.service.UserDetailsServiceImpl;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Key;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурация безопасности для приложения.
 * Определяет настройки аутентификации и авторизации.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final Key signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Генерация ключа с длиной 256 бит

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Возвращает ключ подписи для JWT токенов.
     * Этот метод предназначен для безопасного создания ключа и не должен быть изменен.
     *
     * @return ключ подписи
     */
    @Bean
    public Key signingKey() {
        return signingKey;
    }

    /**
     * Возвращает BCryptPasswordEncoder для хеширования паролей.
     * Не должен быть изменен для поддержания безопасности.
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Определяет цепочку фильтров безопасности для приложения.
     * Этот метод настраивает фильтры для обработки JWT токенов.
     *
     * @param http объект HttpSecurity
     * @param authManager менеджер аутентификации
     * @return объект SecurityFilterChain
     * @throws Exception если возникла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(authManager, signingKey),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authManager, signingKey),
                        UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    /**
     * Создает и настраивает AuthenticationManager для аутентификации пользователей.
     * Этот метод обеспечивает настройку аутентификации для приложения.
     *
     * @param http объект HttpSecurity
     * @return AuthenticationManager
     * @throws Exception если возникла ошибка конфигурации
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
