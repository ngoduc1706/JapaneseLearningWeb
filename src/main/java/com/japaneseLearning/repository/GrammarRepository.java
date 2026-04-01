package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, Long> {
    List<Grammar> findByLesson_LessonId(Long lessonId);
    List<Grammar> findByLevel(String level);

    /** Eagerly fetch Lesson to avoid LazyInitializationException (open-in-view=false) */
    @Query("SELECT g FROM Grammar g LEFT JOIN FETCH g.lesson ORDER BY g.grammarId ASC")
    List<Grammar> findAllWithLesson();
}
