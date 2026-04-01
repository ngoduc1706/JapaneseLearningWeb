package com.japaneseLearning.repository;

import com.japaneseLearning.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserId(String userId);
    Optional<UserProgress> findByUserIdAndLesson_LessonId(String userId, Long lessonId);
    
    @Query("SELECT u FROM UserProgress u WHERE u.lesson.course.courseId = :courseId AND u.userId = :userId")
    List<UserProgress> findByCourseAndUser(@Param("courseId") Long courseId, @Param("userId") String userId);
}
