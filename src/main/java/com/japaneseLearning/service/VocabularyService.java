package com.japaneseLearning.service;

import com.japaneseLearning.entity.Course;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class VocabularyService {
    
    private final VocabularyRepository vocabularyRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    public Vocabulary getVocabularyById(Long vocabId) {
        return vocabularyRepository.findById(vocabId)
            .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + vocabId));
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

    public Vocabulary createVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    public Vocabulary updateVocabulary(Long vocabId, Vocabulary vocabularyDetails) {
        Vocabulary vocabulary = getVocabularyById(vocabId);
        vocabulary.setKanji(vocabularyDetails.getKanji());
        vocabulary.setHiragana(vocabularyDetails.getHiragana());
        vocabulary.setRomaji(vocabularyDetails.getRomaji());
        vocabulary.setMeaning(vocabularyDetails.getMeaning());
        return vocabularyRepository.save(vocabulary);
    }

    public void deleteVocabulary(Long vocabId) {
        vocabularyRepository.deleteById(vocabId);
    }
}
