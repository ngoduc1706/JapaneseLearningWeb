package com.japaneseLearning.service;

import com.japaneseLearning.dto.VocabularyProgressDTO;
import com.japaneseLearning.entity.UserVocabularyProgress;
import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.repository.UserVocabularyProgressRepository;
import com.japaneseLearning.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class VocabularyProgressService {

    private final UserVocabularyProgressRepository progressRepository;
    private final VocabularyRepository vocabularyRepository;

    public VocabularyProgressService(UserVocabularyProgressRepository progressRepository,
                                     VocabularyRepository vocabularyRepository) {
        this.progressRepository = progressRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    public VocabularyProgressDTO setFavorite(String userId, Long vocabularyId, boolean favorite) {
        UserVocabularyProgress progress = getOrCreate(userId, vocabularyId);
        progress.setIsFavorite(favorite);
        return toDTO(progressRepository.save(progress));
    }

    public List<VocabularyProgressDTO> getFavorites(String userId) {
        return progressRepository.findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public VocabularyProgressDTO reviewSrs(String userId, Long vocabularyId, int quality) {
        int boundedQuality = Math.max(0, Math.min(5, quality));
        UserVocabularyProgress progress = getOrCreate(userId, vocabularyId);

        int currentLevel = progress.getSrsLevel() == null ? 0 : progress.getSrsLevel();
        int currentMastery = progress.getMasteryPercent() == null ? 0 : progress.getMasteryPercent();
        int previousReviewCount = progress.getReviewCount() == null ? 0 : progress.getReviewCount();

        if (boundedQuality >= 3) {
            currentLevel = Math.min(currentLevel + 1, 10);
            currentMastery = Math.min(currentMastery + 10, 100);
        } else {
            currentLevel = Math.max(currentLevel - 1, 0);
            currentMastery = Math.max(currentMastery - 10, 0);
        }

        int intervalDays;
        if (currentLevel <= 0) {
            intervalDays = 1;
        } else if (currentLevel == 1) {
            intervalDays = 2;
        } else if (currentLevel == 2) {
            intervalDays = 4;
        } else if (currentLevel == 3) {
            intervalDays = 7;
        } else if (currentLevel == 4) {
            intervalDays = 14;
        } else {
            intervalDays = 30;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReviewAt;

        // If user forgets (0-2), keep item due now to allow immediate relearning.
        if (boundedQuality <= 2) {
            nextReviewAt = now;
        } else if (previousReviewCount == 0) {
            // First successful review should remain visible for quick repetition.
            nextReviewAt = now;
        } else {
            nextReviewAt = now.plusDays(intervalDays);
        }

        progress.setReviewCount(previousReviewCount + 1);
        progress.setSrsLevel(currentLevel);
        progress.setMasteryPercent(currentMastery);
        progress.setLastReviewedAt(now);
        progress.setNextReviewAt(nextReviewAt);

        return toDTO(progressRepository.save(progress));
    }

    public List<VocabularyProgressDTO> getDueReviews(String userId) {
        return progressRepository.findByUserIdAndNextReviewAtLessThanEqualOrderByNextReviewAtAsc(userId, LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private UserVocabularyProgress getOrCreate(String userId, Long vocabularyId) {
        return progressRepository.findByUserIdAndVocabulary_Id(userId, vocabularyId)
                .orElseGet(() -> {
                    Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                            .orElseThrow(() -> new RuntimeException("Vocabulary not found with id: " + vocabularyId));
                    UserVocabularyProgress created = new UserVocabularyProgress();
                    created.setUserId(userId);
                    created.setVocabulary(vocabulary);
                    return created;
                });
    }

    private VocabularyProgressDTO toDTO(UserVocabularyProgress progress) {
        Vocabulary v = progress.getVocabulary();
        return new VocabularyProgressDTO(
                v.getId(),
                v.getKanji(),
                v.getHiragana(),
                v.getMeaning(),
                progress.getIsFavorite(),
                progress.getReviewCount(),
                progress.getSrsLevel(),
                progress.getMasteryPercent(),
                progress.getLastReviewedAt(),
                progress.getNextReviewAt()
        );
    }
}
