package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.UserProfileDTO;
import com.japaneseLearning.dto.UpdateProfileDTO;
import com.japaneseLearning.dto.ChangePasswordDTO;
import com.japaneseLearning.entity.ApplicationUser;
import com.japaneseLearning.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JL-26, JL-27, JL-28, JL-29: User Profile Management APIs
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserProfileController {
    
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final String UPLOAD_DIR = "uploads/avatars/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * JL-26: Get Current User Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile() {
        try {
            ApplicationUser user = getCurrentUser();
            
            UserProfileDTO dto = new UserProfileDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    user.getAvatarUrl(),
                    user.getProvider(),
                    user.getEmailConfirmed(),
                    user.getIsAdmin()
            );
            
            return ResponseEntity.ok(ApiResponse.success(dto, "Profile retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving profile: " + e.getMessage()));
        }
    }

    /**
     * JL-27: Update User Profile (Name, Phone)
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @RequestBody UpdateProfileDTO updateDTO) {
        try {
            ApplicationUser user = getCurrentUser();
            
            if (updateDTO.firstName() != null && !updateDTO.firstName().isBlank()) {
                user.setFirstName(updateDTO.firstName());
            }
            if (updateDTO.lastName() != null && !updateDTO.lastName().isBlank()) {
                user.setLastName(updateDTO.lastName());
            }
            if (updateDTO.phoneNumber() != null && !updateDTO.phoneNumber().isBlank()) {
                user.setPhoneNumber(updateDTO.phoneNumber());
            }
            
            userRepository.save(user);
            
            UserProfileDTO dto = new UserProfileDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    user.getAvatarUrl(),
                    user.getProvider(),
                    user.getEmailConfirmed(),
                    user.getIsAdmin()
            );
            
            return ResponseEntity.ok(ApiResponse.success(dto, "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating profile: " + e.getMessage()));
        }
    }

    /**
     * JL-28: Upload User Avatar
     */
    @PostMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File is empty"));
            }
            
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("File size exceeds 5MB limit"));
            }
            
            // Validate file type (only images)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Only image files are allowed"));
            }
            
            // Create upload directory if not exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generate unique filename
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);
            
            // Save file
            Files.write(filePath, file.getBytes());
            
            // Update user avatar URL
                ApplicationUser user = getCurrentUser();
            
            String avatarUrl = "/uploads/avatars/" + uniqueFileName;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            
            Map<String, String> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);
            
            return ResponseEntity.ok(ApiResponse.success(result, "Avatar uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error uploading file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error processing avatar: " + e.getMessage()));
        }
    }

    /**
     * JL-29: Change Password
     */
    @PostMapping("/profile/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            // Validate input
            if (changePasswordDTO.newPassword() == null || changePasswordDTO.newPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("New password must be at least 6 characters"));
            }
            
            if (!changePasswordDTO.newPassword().equals(changePasswordDTO.confirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Passwords do not match"));
            }
            
                ApplicationUser user = getCurrentUser();

                if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Tai khoan nay khong ho tro doi mat khau bang form"));
                }
            
            // Verify current password
            if (!passwordEncoder.matches(changePasswordDTO.currentPassword(), user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Current password is incorrect"));
            }
            
            // Update password
            user.setPasswordHash(passwordEncoder.encode(changePasswordDTO.newPassword()));
            userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error changing password: " + e.getMessage()));
        }
    }

    /**
     * Helper method to get current user ID from authentication
     */
    private ApplicationUser getCurrentUser() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();

        ApplicationUser user = userRepository.findById(principal).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(principal);
        }
        if (user == null) {
            user = userRepository.findByUsername(principal);
        }
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    /**
     * Helper method to extract file extension
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg"; // default
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
