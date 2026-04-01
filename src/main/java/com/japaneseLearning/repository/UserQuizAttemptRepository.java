package com.japaneseLearning.repository;

import com.japaneseLearning.entity.UserQuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {
    
    /**
     * JL-43: Get all attempts for a user's quiz
     */
    List<UserQuizAttempt> findByUserIdAndQuiz_QuizId(String userId, Long quizId);

    /**
     * Get best score for user's quiz
     */
    @Query("SELECT MAX(a.score) FROM UserQuizAttempt a WHERE a.userId = :userId AND a.quiz.quizId = :quizId")
    Optional<Integer> findBestScoreByUserAndQuiz(@Param("userId") String userId, @Param("quizId") Long quizId);

    /**
     * Get all attempts by user
     */
    List<UserQuizAttempt> findByUserId(String userId);

    /**
     * Get attempts by lesson
     */
    @Query("SELECT a FROM UserQuizAttempt a WHERE a.userId = :userId AND a.quiz.lesson.lessonId = :lessonId")
    List<UserQuizAttempt> findByUserIdAndLessonId(@Param("userId") String userId, @Param("lessonId") Long lessonId);
}
