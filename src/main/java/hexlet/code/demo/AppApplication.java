package hexlet.code.demo;

import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, есть ли пользователь-администратор
        if (userService.getAllUsers().isEmpty()) {
            // Создаем пользователя-администратора
            UserCreateDTO adminUser = new UserCreateDTO();
            adminUser.setEmail("hexlet@example.com");
            adminUser.setPassword("qwerty");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");

            userService.createUser(adminUser);
            System.out.println("Admin user created: hexlet@example.com");
        }
    }
}
