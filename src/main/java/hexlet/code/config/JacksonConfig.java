package hexlet.code.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Возвращает настроенный объект {@link ObjectMapper}, который включает поддержку
     * типов времени Java 8 (например, {@link java.time.LocalDateTime}).
     *
     * Подклассы могут переопределять этот метод для добавления дополнительных настроек,
     * но должны вызвать {@code super.objectMapper()}, чтобы сохранить базовую конфигурацию.
     *
     * @return настроенный {@link ObjectMapper}
     */
    @Bean
    protected ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}