package hexlet.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.security.JwtAuthenticationFilter;
import hexlet.code.security.JwtAuthorizationFilter;
import hexlet.code.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.logging.Logger;

/**
 * Конфигурация безопасности для приложения, включающая настройку фильтров JWT, обработку аутентификации и управления
 * сессиями.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());
    private final UserDetailsServiceImpl userDetailsService;
    private final Key signingKey;

    /**
     * Конструктор, инициализирующий сервис пользователя и ключ для подписи JWT.
     *
     * @param userDetailsService сервис для управления данными пользователей
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        LOGGER.info("Initialized signing key for JWT.");
    }

    /**
     * Определяет BCryptPasswordEncoder как механизм кодирования паролей.
     *
     * @return экземпляр BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Определяет цепочку фильтров безопасности, включая настройку CSRF, политики сессий и фильтры аутентификации.
     *
     * @param http        объект конфигурации безопасности HTTP
     * @param authManager менеджер аутентификации
     * @return настроенный SecurityFilterChain
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authManager, signingKey);
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(authManager, signingKey,
                new ObjectMapper());

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/", "/index.html", "/assets/**", "/api/pages",
                                "/api/pages/*", "/api/users", "/welcome").permitAll()  // Добавлено /welcome
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Настраивает менеджер аутентификации, используя сервис пользователей и кодировщик паролей.
     *
     * @param http объект конфигурации безопасности HTTP
     * @return настроенный AuthenticationManager
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    /**
     * Определяет DAO-провайдер аутентификации с использованием сервиса пользователя и кодировщика паролей.
     *
     * @return настроенный AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
