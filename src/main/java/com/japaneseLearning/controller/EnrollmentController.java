package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.entity.UserCourseEnrollment;
import com.japaneseLearning.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/{userId}/{courseId}")
    public ResponseEntity<ApiResponse<UserCourseEnrollment>> enrollCourse(
            @PathVariable String userId,
            @PathVariable Long courseId) {
        UserCourseEnrollment enrollment = enrollmentService.enrollUserInCourse(userId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(enrollment, "Enrolled successfully"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<UserCourseEnrollment>>> getUserEnrollments(@PathVariable String userId) {
        List<UserCourseEnrollment> enrollments = enrollmentService.getUserEnrollments(userId);
        return ResponseEntity.ok(ApiResponse.success(enrollments, "Enrollments retrieved"));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeEnrollment(@PathVariable Long id) {
        enrollmentService.completeEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Enrollment completed"));
    }

    /**
     * Check if the current authenticated user is enrolled in a course.
     * Used by the frontend to determine which button to show.
     */
    @GetMapping("/check/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> checkEnrollment(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return ResponseEntity.ok(ApiResponse.success(false, "Not authenticated"));
        }
        // Admins are always considered 'enrolled'
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok(ApiResponse.success(true, "Admin access"));
        }
        boolean enrolled = enrollmentService.isUserEnrolledInCourse(auth.getName(), courseId);
        return ResponseEntity.ok(ApiResponse.success(enrolled, "Check successful"));
    }

    /**
     * Self-enroll the current authenticated user into a FREE course.
     * Used by the frontend "Đăng ký ngay" button.
     */
    @PostMapping("/free/{courseId}")
    public ResponseEntity<ApiResponse<UserCourseEnrollment>> enrollFree(@PathVariable Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() ||
            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Bạn cần đăng nhập để đăng ký khóa học"));
        }
        UserCourseEnrollment enrollment = enrollmentService.enrollUserInCourse(auth.getName(), courseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(enrollment, "Đăng ký thành công!"));
    }
}
