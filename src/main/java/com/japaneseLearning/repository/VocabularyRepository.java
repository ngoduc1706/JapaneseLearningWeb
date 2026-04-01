package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByLesson_LessonId(Long lessonId);
    List<Vocabulary> findByLesson(Lesson lesson);
    List<Vocabulary> findByLesson_Course_CourseId(Long courseId);
    List<Vocabulary> findByLesson_Course(Course course);
    Vocabulary findByKanjiAndHiragana(String kanji, String hiragana);
}
