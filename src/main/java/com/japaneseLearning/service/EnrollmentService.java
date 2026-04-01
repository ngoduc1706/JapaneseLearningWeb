package com.japaneseLearning.service;

import com.japaneseLearning.entity.UserCourseEnrollment;
import com.japaneseLearning.entity.Course;
import com.japaneseLearning.repository.UserCourseEnrollmentRepository;
import com.japaneseLearning.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentService {
    
    private final UserCourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(UserCourseEnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public UserCourseEnrollment enrollUserInCourse(String userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        // Check if already enrolled
        Optional<UserCourseEnrollment> existing = 
            enrollmentRepository.findByUserIdAndCourse_CourseId(userId, courseId);
        
        if (existing.isPresent()) {
            // Already enrolled - return existing enrollment instead of throwing
            return existing.get();
        }

        UserCourseEnrollment enrollment = new UserCourseEnrollment();
        enrollment.setUserId(userId);
        enrollment.setCourse(course);
        enrollment.setStatus(UserCourseEnrollment.EnrollmentStatus.ENROLLED);
        enrollment.setEnrolledAt(LocalDateTime.now());
        
        return enrollmentRepository.save(enrollment);
    }

    public void completeEnrollment(Long enrollmentId) {
        UserCourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setStatus(UserCourseEnrollment.EnrollmentStatus.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }

    public List<UserCourseEnrollment> getUserEnrollments(String userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public boolean isUserEnrolledInCourse(String userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourse_CourseId(userId, courseId).isPresent();
    }
}
