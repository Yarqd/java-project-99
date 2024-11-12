package hexlet.code.config;

import hexlet.code.service.UserDetailsServiceImpl;
import hexlet.code.util.JWTUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JWTUtils jwtUtils;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JWTUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Конфигурация цепочки фильтров безопасности.
     * Настраивает обработку запросов, исключая CSRF, и добавляет фильтр аутентификации JWT.
     *
     * @param http HttpSecurity для настройки безопасности HTTP.
     * @return настроенная цепочка фильтров безопасности.
     * @throws Exception если возникнет ошибка конфигурации.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/pages/*", "/api/pages", "/",
                                "/index.html", "/assets/**", "/welcome").permitAll() // разрешаем доступ к /welcome
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Создает экземпляр JWTAuthenticationFilter.
     *
     * @return фильтр для обработки JWT аутентификации.
     */
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtUtils, userDetailsService);
    }

    /**
     * Создает и настраивает AuthenticationManager.
     *
     * @param http HttpSecurity для настройки аутентификации.
     * @return настроенный AuthenticationManager.
     * @throws Exception если возникнет ошибка конфигурации.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(daoAuthProvider());
        return auth.build();
    }

    /**
     * Создает и настраивает AuthenticationProvider.
     * Использует DaoAuthenticationProvider для аутентификации через UserDetailsService.
     *
     * @return настроенный AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Создает и настраивает PasswordEncoder.
     * Использует BCryptPasswordEncoder для хэширования паролей.
     *
     * @return настроенный PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
