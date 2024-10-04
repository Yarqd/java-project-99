package hexlet.code.demo;

import hexlet.code.demo.dto.UserCreateDTO;
import hexlet.code.demo.model.TaskStatus;
import hexlet.code.demo.model.Label;
import hexlet.code.demo.service.UserService;
import hexlet.code.demo.repository.TaskStatusRepository;
import hexlet.code.demo.repository.LabelRepository;
import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    public static void main(String[] args) {
        // Инициализация Sentry
        Sentry.init(options -> {
            options.setDsn(System.getenv("SENTRY_AUTH_TOKEN"));
            options.setTracesSampleRate(1.0);
            options.setDebug(true);
        });

        SpringApplication.run(AppApplication.class, args);
    }

    /**
     * Метод, который запускается при старте приложения.
     * Выполняет инициализацию администратора, статусов задач и меток.
     *
     * @param args аргументы командной строки
     * @throws Exception если возникает ошибка во время выполнения
     */
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

        // Инициализация статусов задач
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus("Draft", "draft"));
            taskStatusRepository.save(new TaskStatus("To Review", "to_review"));
            taskStatusRepository.save(new TaskStatus("To Be Fixed", "to_be_fixed"));
            taskStatusRepository.save(new TaskStatus("To Publish", "to_publish"));
            taskStatusRepository.save(new TaskStatus("Published", "published"));

            System.out.println("Default task statuses created: Draft, To Review, To Be Fixed, To Publish, Published");
        }

        // Инициализация дефолтных меток
        if (labelRepository.count() == 0) {
            labelRepository.save(new Label("feature"));
            labelRepository.save(new Label("bug"));

            System.out.println("Default labels created: feature, bug");
        }
    }
}
