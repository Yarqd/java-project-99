package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Label;
import hexlet.code.model.Role;
import hexlet.code.service.UserService;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Override
    public final void run(String... args) throws Exception {
        // Создаем роли, если их нет
        roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));
        roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        // Создаем администратора, если пользователи отсутствуют
        if (userService.getAllUsers().isEmpty()) {
            UserCreateDTO adminUser = new UserCreateDTO();
            adminUser.setEmail("hexlet@example.com");
            adminUser.setPassword("qwerty");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");

            // Назначаем роли
            List<String> adminRoles = Collections.singletonList("ADMIN");
            userService.createUserWithRoles(adminUser, adminRoles);
            System.out.println("Admin user created: hexlet@example.com");
        }

        // Инициализация статусов задач, если их нет
        if (taskStatusRepository.count() == 0) {
            taskStatusRepository.save(new TaskStatus("Draft", "draft"));
            taskStatusRepository.save(new TaskStatus("To Review", "to_review"));
            taskStatusRepository.save(new TaskStatus("To Be Fixed", "to_be_fixed"));
            taskStatusRepository.save(new TaskStatus("To Publish", "to_publish"));
            taskStatusRepository.save(new TaskStatus("Published", "published"));

            System.out.println("Default task statuses created: Draft, To Review, To Be Fixed, To Publish, Published");
        }

        // Инициализация меток, если их нет
        if (labelRepository.count() == 0) {
            labelRepository.save(new Label("feature"));
            labelRepository.save(new Label("bug"));

            System.out.println("Default labels created: feature, bug");
        }
    }
}
