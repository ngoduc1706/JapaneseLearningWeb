package com.japaneseLearning.service;

import com.japaneseLearning.dto.CourseDTO;
import com.japaneseLearning.entity.Category;
import com.japaneseLearning.entity.Course;
import com.japaneseLearning.exception.ResourceNotFoundException;
import com.japaneseLearning.repository.CategoryRepository;
import com.japaneseLearning.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAllWithLessons().stream()
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
        return searchCourses(keyword, null, null, null, null, page, size);
    }

    public List<CourseDTO> searchCourses(String keyword,
                                         Long categoryId,
                                         Boolean isFree,
                                         Double minPrice,
                                         Double maxPrice,
                                         int page,
                                         int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        return courseRepository.searchWithFilters(
                        keyword == null ? "" : keyword.trim(),
                        categoryId,
                        isFree,
                        minPrice,
                        maxPrice,
                        pageable
                )
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        validateCourseDTO(courseDTO);

        Course course = new Course();
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setIsFree(courseDTO.isFree());
        course.setPrice(courseDTO.isFree() ? 0D : courseDTO.price());
        course.setCategory(resolveCategory(courseDTO.categoryId()));

        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    public CourseDTO updateCourse(Long courseId, CourseDTO courseDTO) {
        validateCourseDTO(courseDTO);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setIsFree(courseDTO.isFree());
        course.setPrice(courseDTO.isFree() ? 0D : courseDTO.price());
        course.setCategory(resolveCategory(courseDTO.categoryId()));
        
        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private void validateCourseDTO(CourseDTO courseDTO) {
        if (courseDTO == null) {
            throw new IllegalArgumentException("Course payload is required");
        }
        if (courseDTO.title() == null || courseDTO.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title is required");
        }
        if (!courseDTO.isFree() && (courseDTO.price() == null || courseDTO.price() < 0)) {
            throw new IllegalArgumentException("Paid course must have a valid non-negative price");
        }
    }

    private CourseDTO convertToDTO(Course course) {
        Long categoryId = course.getCategory() != null ? course.getCategory().getCategoryId() : null;
        String categoryName = course.getCategory() != null ? course.getCategory().getName() : null;
        int lessonCount = course.getLessons() != null ? course.getLessons().size() : 0;

        return new CourseDTO(
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.isIsFree(),
                course.getPrice(),
                categoryId,
                categoryName,
                lessonCount,
                course.getCreatedAt()
        );
    }
}
