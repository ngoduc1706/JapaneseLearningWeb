package com.japaneseLearning.controller;

import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import com.japaneseLearning.security.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * AuthApiController handles API authentication requests (for AJAX forms)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.japaneseLearning.repository.PasswordResetTokenRepository tokenRepository;

    @Autowired
    private com.japaneseLearning.service.EmailService emailService;

    /**
     * API endpoint for forgot password request
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        ObjectNode response = objectMapper.createObjectNode();
        try {
            ApplicationUser user = userRepository.findByEmail(email);
            if (user == null) {
                response.put("success", false);
                response.put("message", "Email không tồn tại trong hệ thống");
                return ResponseEntity.badRequest().body(response);
            }

            // Generate token
            String token = UUID.randomUUID().toString();
            com.japaneseLearning.entity.PasswordResetToken resetToken = new com.japaneseLearning.entity.PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(java.time.LocalDateTime.now().plusMinutes(15));
            
            tokenRepository.save(resetToken);

            // Send Email
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("userName", user.getFirstName());
            context.setVariable("resetToken", token);
            context.setVariable("resetLink", "http://localhost:8080/reset-password?token=" + token);

            emailService.sendHtmlEmailWithTemplate(email, "Yêu cầu khôi phục mật khẩu - Japanese Learning", "forgot-password-email", context);

            response.put("success", true);
            response.put("message", "Link khôi phục mật khẩu đã được gửi đến email của bạn.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xử lý yêu cầu: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * API endpoint for reset password action
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        ObjectNode response = objectMapper.createObjectNode();
        try {
            if (newPassword == null || newPassword.length() < 6) {
                response.put("success", false);
                response.put("message", "Mật khẩu phải có ít nhất 6 ký tự");
                return ResponseEntity.badRequest().body(response);
            }

            java.util.Optional<com.japaneseLearning.entity.PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
            
            if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
                response.put("success", false);
                response.put("message", "Mã xác nhận không hợp lệ hoặc đã hết hạn");
                return ResponseEntity.badRequest().body(response);
            }

            ApplicationUser user = tokenOpt.get().getUser();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            // Delete token after use
            tokenRepository.delete(tokenOpt.get());

            response.put("success", true);
            response.put("message", "Mật khẩu của bạn đã được cập nhật thành công.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi đặt lại mật khẩu: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * API endpoint for user registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword
    ) {
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            // Validate fullName
            if (fullName == null || fullName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Họ tên không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate password
            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Mật khẩu phải có ít nhất 6 ký tự");
                return ResponseEntity.badRequest().body(response);
            }

            // Check password confirmation
            if (!password.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Mật khẩu xác nhận không khớp");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if email already exists
            if (userRepository.findByEmail(email) != null) {
                response.put("success", false);
                response.put("message", "Email này đã được đăng ký");
                return ResponseEntity.badRequest().body(response);
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

            // JL-24: Send welcome email template after registration.
            try {
                org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
                context.setVariable("userName", fullName.trim());
                emailService.sendHtmlEmailWithTemplate(
                        email,
                        "Chào mừng bạn đến với Japanese Learning",
                        "welcome-email",
                        context
                );
            } catch (Exception ignored) {
                // Do not fail registration if email sending fails.
            }
            
            response.put("success", true);
            response.put("message", "Đăng ký thành công! Hãy đăng nhập.");
            response.put("redirect", "/login");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * API endpoint for user login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session
    ) {
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            // Validate inputs
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "Mật khẩu không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // Find user by email
            ApplicationUser user = userRepository.findByEmail(email);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "Email không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }

            // Verify password
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                response.put("success", false);
                response.put("message", "Mật khẩu không chính xác");
                return ResponseEntity.badRequest().body(response);
            }

            // Authenticate user - set in SecurityContext and session
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), 
                null, 
                userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            
            response.put("success", true);
            response.put("message", "Đăng nhập thành công!");
            response.put("redirect", "/dashboard");
            response.put("userId", user.getId());
            response.put("userName", user.getFirstName() + " " + user.getLastName());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi đăng nhập: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Check if user is authenticated
     */
    @PostMapping("/check")
    public ResponseEntity<?> check(HttpSession session) {
        ObjectNode response = objectMapper.createObjectNode();
        
        String userId = (String) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");
        
        if (userId != null && !userId.isEmpty()) {
            response.put("authenticated", true);
            response.put("userId", userId);
            response.put("userName", userName);
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        ObjectNode response = objectMapper.createObjectNode();
        
        try {
            SecurityContextHolder.clearContext();
            session.invalidate();
            
            response.put("success", true);
            response.put("message", "Đăng xuất thành công");
            response.put("redirect", "/");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi đăng xuất: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
