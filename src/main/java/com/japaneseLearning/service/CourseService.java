package com.japaneseLearning.service;

import com.japaneseLearning.dto.CourseDTO;
import com.japaneseLearning.entity.Course;
import com.japaneseLearning.exception.ResourceNotFoundException;
import com.japaneseLearning.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }

    public Course getCourseEntityById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }

    public List<CourseDTO> getFreeCourses() {
        return courseRepository.findByIsFreeTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategory_CategoryId(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getAccessibleCoursesByUser(String userId) {
        return courseRepository.findAccessibleCoursesByUser(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * JL-34: Search courses by keyword with pagination
     */
    public List<CourseDTO> searchCourses(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        
        List<Course> courses = courseRepository.searchByKeyword(keyword);
        
        // Simple pagination
        int startIdx = page * size;
        int endIdx = Math.min(startIdx + size, courses.size());
        
        if (startIdx >= courses.size()) {
            return List.of();
        }
        
        return courses.subList(startIdx, endIdx).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setIsFree(courseDTO.isFree());
        course.setPrice(courseDTO.price());
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    public CourseDTO updateCourse(Long courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setIsFree(courseDTO.isFree());
        course.setPrice(courseDTO.price());
        
        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.isIsFree(),
                course.getPrice(),
                course.getCategory().getCategoryId(),
                course.getCategory().getName(),
                course.getLessons().size(),
                course.getCreatedAt()
        );
    }
}
