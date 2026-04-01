package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.LessonDTO;
import com.japaneseLearning.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {
    
    private final LessonService lessonService;
    private final com.japaneseLearning.service.EnrollmentService enrollmentService;

    public LessonController(LessonService lessonService, com.japaneseLearning.service.EnrollmentService enrollmentService) {
        this.lessonService = lessonService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDTO>> getLessonById(@PathVariable Long id) {
        LessonDTO lesson = lessonService.getLessonById(id);
        
        // SECURITY CHECK
        if (!isUserAuthorizedForCourse(lesson.courseId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(lesson, "Lesson retrieved successfully"));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<LessonDTO>>> getLessonsByCourse(@PathVariable Long courseId) {
        // SECURITY CHECK
        if (!isUserAuthorizedForCourse(courseId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }
        
        List<LessonDTO> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(ApiResponse.success(lessons, "Lessons retrieved successfully"));
    }

    /**
     * JL-36: Get next lesson in personalized route.
     */
    @GetMapping("/{lessonId}/next")
    public ResponseEntity<ApiResponse<LessonDTO>> getNextLesson(@PathVariable Long lessonId) {
        LessonDTO currentLesson = lessonService.getLessonById(lessonId);

        if (!isUserAuthorizedForCourse(currentLesson.courseId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn chưa đăng ký khóa học này"));
        }

        Optional<LessonDTO> nextLesson = lessonService.getNextLesson(lessonId);
        if (nextLesson.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(nextLesson.get(), "Next lesson retrieved successfully"));
        }

        return ResponseEntity.ok(ApiResponse.success(null, "Bạn đã hoàn thành bài học cuối cùng trong lộ trình"));
    }

    private boolean isUserAuthorizedForCourse(Long courseId) {
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || 
            auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return false;
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        return enrollmentService.isUserEnrolledInCourse(auth.getName(), courseId);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LessonDTO>> createLesson(@RequestBody LessonDTO lessonDTO) {
        LessonDTO created = lessonService.createLesson(lessonDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Lesson created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonDTO>> updateLesson(@PathVariable Long id, @RequestBody LessonDTO lessonDTO) {
        LessonDTO updated = lessonService.updateLesson(id, lessonDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Lesson updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Lesson deleted successfully"));
    }
}
