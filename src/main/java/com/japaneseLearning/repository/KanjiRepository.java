package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KanjiRepository extends JpaRepository<Kanji, Long> {
    List<Kanji> findByLesson_LessonId(Long lessonId);
    Kanji findByCharacter(String character);
}
