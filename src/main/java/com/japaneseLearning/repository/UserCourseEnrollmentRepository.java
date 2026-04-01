package com.japaneseLearning.repository;

import com.japaneseLearning.entity.UserCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseEnrollmentRepository extends JpaRepository<UserCourseEnrollment, Long> {
    List<UserCourseEnrollment> findByUserId(String userId);
    List<UserCourseEnrollment> findByCourse_CourseId(Long courseId);
    Optional<UserCourseEnrollment> findByUserIdAndCourse_CourseId(String userId, Long courseId);
    List<UserCourseEnrollment> findByStatus(UserCourseEnrollment.EnrollmentStatus status);
}
