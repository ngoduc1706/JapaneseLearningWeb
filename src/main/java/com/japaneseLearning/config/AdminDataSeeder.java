package com.japaneseLearning.config;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Tự động tạo tài khoản Admin khi khởi động nếu chưa tồn tại.
 * Chạy sau tất cả DataSeeder khác (Order = 10).
 */
@Component
@Order(10)
public class AdminDataSeeder implements CommandLineRunner {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataSeeder(ApplicationUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createAdminIfAbsent(
            "admin@japanese-learning.com",
            "123456",
            "Quản",
            "Trị Viên"
        );
    }

    private void createAdminIfAbsent(String email, String password,
                                     String firstName, String lastName) {
        var existing = userRepository.findByEmail(email);

        // If already exists — ensure isAdmin flag is set, then done
        if (existing != null) {
            if (!Boolean.TRUE.equals(existing.getIsAdmin())) {
                existing.setIsAdmin(true);
                userRepository.save(existing);
                System.out.println("✅ Admin flag updated for: " + email);
            } else {
                System.out.println("✓ Admin account OK: " + email);
            }
            return;
        }

        // Create fresh admin account
        ApplicationUser admin = new ApplicationUser();
        admin.setId(UUID.randomUUID().toString());
        admin.setUsername(email);
        admin.setEmail(email);
        admin.setNormalizedEmail(email.toUpperCase());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setEmailConfirmed(true);
        admin.setLockoutEnabled(false);
        admin.setAccessFailedCount(0);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setIsAdmin(true);

        userRepository.save(admin);
        System.out.println("✅ Admin account created!");
        System.out.println("   Email   : " + email);
        System.out.println("   Password: " + password);
    }
}
