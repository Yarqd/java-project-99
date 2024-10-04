package hexlet.code.demo.config;

import hexlet.code.demo.security.JwtAuthorizationFilter;
import hexlet.code.demo.service.UserDetailsServiceImpl;
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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Конфигурируем BCryptPasswordEncoder для шифрования паролей.
     *
     * @return экземпляр BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настраивает фильтры безопасности, авторизацию и маршруты.
     *
     * @param http        объект HttpSecurity для настройки
     * @param authManager менеджер аутентификации
     * @return настроенный SecurityFilterChain
     * @throws Exception если возникнет ошибка при настройке
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Отключаем CSRF
                .authorizeHttpRequests(auth -> auth
                        // Открываем доступ для создания пользователей без авторизации
                        .requestMatchers("/api/users").permitAll()
                        // Защищаем маршруты API для остальных операций с пользователями
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().permitAll()  // Разрешаем доступ к остальным маршрутам
                )
                .addFilterBefore(new JwtAuthorizationFilter(authManager), UsernamePasswordAuthenticationFilter.class)
                // Добавляем фильтр для проверки токенов
                .httpBasic(withDefaults())  // Используем Basic Auth
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL для логаута
                        .logoutSuccessUrl("/")  // Куда перенаправить после логаута
                        .permitAll());  // Открываем доступ к логауту

        return http.build();
    }

    /**
     * Настраивает менеджер аутентификации с использованием UserDetailsService и BCryptPasswordEncoder.
     *
     * @param http объект HttpSecurity для настройки менеджера аутентификации
     * @return настроенный AuthenticationManager
     * @throws Exception если возникнет ошибка при настройке
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
