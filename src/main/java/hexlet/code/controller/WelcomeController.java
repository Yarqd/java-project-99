package hexlet.code.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    /**
     * Обрабатывает запрос к маршруту "/welcome".
     *
     * @return приветственное сообщение
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
