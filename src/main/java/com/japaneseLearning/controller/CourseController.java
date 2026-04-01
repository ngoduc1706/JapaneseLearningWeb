package com.japaneseLearning.controller;

import com.japaneseLearning.dto.ApiResponse;
import com.japaneseLearning.dto.CourseDTO;
import com.japaneseLearning.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {
    
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses, "Courses retrieved successfully"));
    }

    @GetMapping("/free")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getFreeCourses() {
        List<CourseDTO> courses = courseService.getFreeCourses();
        return ResponseEntity.ok(ApiResponse.success(courses, "Free courses retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success(course, "Course retrieved successfully"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByCategory(@PathVariable Long categoryId) {
        List<CourseDTO> courses = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(courses, "Courses by category retrieved successfully"));
    }

    /**
     * JL-34: Search courses by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CourseDTO> courses = courseService.searchCourses(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(courses, "Search results retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO created = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Course created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        CourseDTO updated = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Course updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Course deleted successfully"));
    }
}
