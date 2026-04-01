package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse_CourseId(Long courseId);
    List<Lesson> findByCourse_CourseIdOrderByOrderInCourseAsc(Long courseId);
    long countByCourse_CourseId(Long courseId);

    /** Eagerly fetch grammars/kanjis/vocabularies to avoid LazyInit when open-in-view=false */
    @Query("SELECT DISTINCT l FROM Lesson l " +
           "LEFT JOIN FETCH l.grammars " +
           "LEFT JOIN FETCH l.kanjis " +
           "LEFT JOIN FETCH l.vocabularies " +
           "ORDER BY l.orderInCourse ASC NULLS LAST")
    List<Lesson> findAllWithCollections();

    /** For lesson detail page - fetch all related data for one lesson */
    @Query("SELECT DISTINCT l FROM Lesson l " +
           "LEFT JOIN FETCH l.course " +
           "LEFT JOIN FETCH l.grammars " +
           "LEFT JOIN FETCH l.kanjis " +
           "LEFT JOIN FETCH l.vocabularies " +
           "WHERE l.lessonId = :id")
    Optional<Lesson> findByIdWithAll(Long id);
    /** For course specific lesson list - eagerly fetch all collections */
    @Query("SELECT DISTINCT l FROM Lesson l " +
           "LEFT JOIN FETCH l.grammars " +
           "LEFT JOIN FETCH l.kanjis " +
           "LEFT JOIN FETCH l.vocabularies " +
           "WHERE l.course.courseId = :courseId " +
           "ORDER BY l.orderInCourse ASC NULLS LAST")
    List<Lesson> findByCourseIdWithCollections(Long courseId);
}
