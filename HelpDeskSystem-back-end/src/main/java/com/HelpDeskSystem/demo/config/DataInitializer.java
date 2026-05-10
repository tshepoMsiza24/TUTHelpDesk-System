package com.HelpDeskSystem.demo.config;

import com.HelpDeskSystem.demo.model.Category;
import com.HelpDeskSystem.demo.model.User;
import com.HelpDeskSystem.demo.repository.CategoryRepository;
import com.HelpDeskSystem.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@tut.ac.za")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin / admin123");
        }

        // Create default student if not exists
        if (!userRepository.existsByUsername("student1")) {
            User student = User.builder()
                    .username("student1")
                    .email("student1@tut.ac.za")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Tshepo Msiza")
                    .role(User.Role.STUDENT)
                    .build();
            userRepository.save(student);
            System.out.println("✅ Default student created: student1 / student123");
        }
        if (!userRepository.existsByUsername("student2")) {
            User student = User.builder()
                    .username("student2")
                    .email("student2@tut.ac.za")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Hope Mathoko")
                    .role(User.Role.STUDENT)
                    .build();
            userRepository.save(student);
            System.out.println("✅ Default student created: student2 / student123");
        }

        if (!userRepository.existsByUsername("student3")) {
            User student = User.builder()
                    .username("student3")
                    .email("student3@tut.ac.za")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Masina Prayer")
                    .role(User.Role.STUDENT)
                    .build();
            userRepository.save(student);
            System.out.println("✅ Default student created: student3 / student123");
        }

        // Create default categories if not exist
        String[][] categories = {
            {"Technical Support", "Hardware and software issues"},
            {"Account Issues", "Login, password, and account-related problems"},
            {"Course Registration", "Issues with course enrollment and registration"},
            {"Financial Aid", "Questions about fees, payments, and financial aid"},
            {"General Inquiry", "General questions and other issues"}
        };

        for (String[] cat : categories) {
            if (!categoryRepository.existsByName(cat[0])) {
                Category category = Category.builder()
                        .name(cat[0])
                        .description(cat[1])
                        .build();
                categoryRepository.save(category);
            }
        }
        System.out.println("✅ Default categories initialized");
    }
}
