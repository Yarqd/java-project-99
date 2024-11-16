package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Label;
import hexlet.code.model.Role;
import hexlet.code.repository.RoleRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.UserService;
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
    private RoleRepository roleRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Override
    public final void run(String... args) throws Exception {
        initializeRoles();
        initializeAdminUser();
        initializeTaskStatuses();
        initializeLabels();
    }

    /**
     * Инициализация ролей, если они отсутствуют.
     */
    private void initializeRoles() {
        System.out.println("Initializing roles...");
        roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(new Role("USER")));
        roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        System.out.println("Roles initialized.");
    }

    /**
     * Создание администратора, если пользователи отсутствуют.
     */
    private void initializeAdminUser() {
        if (userService.getAllUsers().isEmpty()) {
            System.out.println("No users found. Creating admin user...");
            UserCreateDTO adminUser = new UserCreateDTO();
            adminUser.setEmail("hexlet@example.com");
            adminUser.setPassword("qwerty");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");

            List<String> adminRoles = Collections.singletonList("ADMIN");
            userService.createUserWithRoles(adminUser, adminRoles);
            System.out.println("Admin user created: hexlet@example.com");
        } else {
            System.out.println("Users already exist. Skipping admin user creation.");
        }
    }

    /**
     * Инициализация статусов задач, если они отсутствуют.
     */
    private void initializeTaskStatuses() {
        if (taskStatusRepository.count() == 0) {
            System.out.println("No task statuses found. Creating default statuses...");
            taskStatusRepository.save(new TaskStatus("Draft", "draft"));
            taskStatusRepository.save(new TaskStatus("To Review", "to_review"));
            taskStatusRepository.save(new TaskStatus("To Be Fixed", "to_be_fixed"));
            taskStatusRepository.save(new TaskStatus("To Publish", "to_publish"));
            taskStatusRepository.save(new TaskStatus("Published", "published"));
            System.out.println("Default task statuses created: Draft, To Review, To Be Fixed, To Publish, Published");
        } else {
            System.out.println("Task statuses already exist. Skipping initialization.");
        }
    }

    /**
     * Инициализация меток, если они отсутствуют.
     */
    private void initializeLabels() {
        if (labelRepository.count() == 0) {
            System.out.println("No labels found. Creating default labels...");
            labelRepository.save(new Label("feature"));
            labelRepository.save(new Label("bug"));
            System.out.println("Default labels created: feature, bug");
        } else {
            System.out.println("Labels already exist. Skipping initialization.");
        }
    }
}
