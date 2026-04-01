package com.japaneseLearning.controller;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;

/**
 * AuthController handles user authentication (login, register)
 */
@Controller
public class AuthController {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Validate inputs
            if (fullName == null || fullName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ tên không được để trống");
                return "redirect:/register";
            }
            
            if (email == null || email.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống");
                return "redirect:/register";
            }

            if (password == null || password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
                return "redirect:/register";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
                return "redirect:/register";
            }

            // Check if email already exists
            if (userRepository.findByEmail(email) != null) {
                redirectAttributes.addFlashAttribute("error", "Email này đã được đăng ký");
                return "redirect:/register";
            }

            // Create new user
            ApplicationUser newUser = new ApplicationUser();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setNormalizedEmail(email.toUpperCase());
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setEmailConfirmed(false);
            newUser.setLockoutEnabled(true);
            newUser.setAccessFailedCount(0);

            // Split fullName into firstName and lastName
            String[] nameParts = fullName.trim().split(" ", 2);
            newUser.setFirstName(nameParts[0]);
            newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");

            // Save user
            userRepository.save(newUser);
            
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
