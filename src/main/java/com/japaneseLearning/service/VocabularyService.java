package com.japaneseLearning.service;

import com.japaneseLearning.entity.Course;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.exception.ResourceNotFoundException;
import com.japaneseLearning.repository.LessonRepository;
import com.japaneseLearning.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class VocabularyService {
    
    private final VocabularyRepository vocabularyRepository;
    private final LessonRepository lessonRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository, LessonRepository lessonRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.lessonRepository = lessonRepository;
    }

    public Vocabulary getVocabularyById(Long vocabId) {
        return vocabularyRepository.findById(vocabId)
            .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found with id: " + vocabId));
    }

    public List<Vocabulary> getVocabulariesByLesson(Lesson lesson) {
        return vocabularyRepository.findByLesson(lesson);
    }

    public List<Vocabulary> getVocabulariesByCourse(Course course) {
        return vocabularyRepository.findByLesson_Course(course);
    }

    public List<Vocabulary> getAllVocabularies() {
        return vocabularyRepository.findAll();
    }

    public List<Vocabulary> getVocabulariesByLessonId(Long lessonId) {
        return vocabularyRepository.findByLesson_LessonId(lessonId);
    }

    public List<Vocabulary> getVocabulariesByCourseId(Long courseId) {
        return vocabularyRepository.findByLesson_Course_CourseId(courseId);
    }

    public Vocabulary createVocabulary(Vocabulary vocabulary) {
        validateVocabulary(vocabulary);
        return vocabularyRepository.save(vocabulary);
    }

    public Vocabulary createVocabulary(Vocabulary vocabulary, Long lessonId) {
        validateVocabulary(vocabulary);
        if (lessonId != null) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
            vocabulary.setLesson(lesson);
        }
        return vocabularyRepository.save(vocabulary);
    }

    public Vocabulary updateVocabulary(Long vocabId, Vocabulary vocabularyDetails) {
        validateVocabulary(vocabularyDetails);
        Vocabulary vocabulary = getVocabularyById(vocabId);
        vocabulary.setKanji(vocabularyDetails.getKanji());
        vocabulary.setHiragana(vocabularyDetails.getHiragana());
        vocabulary.setRomaji(vocabularyDetails.getRomaji());
        vocabulary.setMeaning(vocabularyDetails.getMeaning());
        return vocabularyRepository.save(vocabulary);
    }

    public Vocabulary updateVocabulary(Long vocabId, Vocabulary vocabularyDetails, Long lessonId) {
        validateVocabulary(vocabularyDetails);
        Vocabulary vocabulary = getVocabularyById(vocabId);
        vocabulary.setKanji(vocabularyDetails.getKanji());
        vocabulary.setHiragana(vocabularyDetails.getHiragana());
        vocabulary.setRomaji(vocabularyDetails.getRomaji());
        vocabulary.setMeaning(vocabularyDetails.getMeaning());
        if (lessonId != null) {
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
            vocabulary.setLesson(lesson);
        }
        return vocabularyRepository.save(vocabulary);
    }

    public void deleteVocabulary(Long vocabId) {
        if (!vocabularyRepository.existsById(vocabId)) {
            throw new ResourceNotFoundException("Vocabulary not found with id: " + vocabId);
        }
        vocabularyRepository.deleteById(vocabId);
    }

    private void validateVocabulary(Vocabulary vocabulary) {
        if (vocabulary == null) {
            throw new IllegalArgumentException("Vocabulary payload is required");
        }
        if (vocabulary.getKanji() == null || vocabulary.getKanji().trim().isEmpty()) {
            throw new IllegalArgumentException("Vocabulary kanji is required");
        }
        if (vocabulary.getHiragana() == null || vocabulary.getHiragana().trim().isEmpty()) {
            throw new IllegalArgumentException("Vocabulary hiragana is required");
        }
    }
}
