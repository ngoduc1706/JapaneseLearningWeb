package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourse_CourseId(Long courseId);
    List<Quiz> findByLesson_LessonId(Long lessonId);
}
