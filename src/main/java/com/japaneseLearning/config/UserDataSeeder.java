package com.japaneseLearning.config;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserDataSeeder implements CommandLineRunner {
    
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataSeeder(ApplicationUserRepository userRepository, 
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if test user already exists
        if (userRepository.findByEmail("test@japanese-learning.com") != null) {
            System.out.println("Test users already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding test user accounts...");

        // Create Test User 1
        createTestUser(
            "test@japanese-learning.com",
            "123456",
            "Nguyễn",
            "Vinh",
            "0123456789"
        );

        // Create Test User 2
        createTestUser(
            "student@japanese-learning.com",
            "123456",
            "Trần",
            "Huy",
            "0987654321"
        );

        // Create Test User 3
        createTestUser(
            "user@example.com",
            "password123",
            "Phạm",
            "Thanh",
            "0912345678"
        );

        System.out.println("Test users seeded successfully!");
    }

    private void createTestUser(String email, String password, 
                               String firstName, String lastName, String phone) {
        ApplicationUser user = new ApplicationUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(email);
        user.setEmail(email);
        user.setNormalizedEmail(email.toUpperCase());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmailConfirmed(true);
        user.setLockoutEnabled(false);
        user.setAccessFailedCount(0);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phone);

        userRepository.save(user);
        System.out.println("✓ Tài khoản tạo: " + email + " (Mật khẩu: " + password + ")");
    }
}
