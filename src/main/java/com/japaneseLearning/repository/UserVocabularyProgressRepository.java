package com.japaneseLearning.repository;

import com.japaneseLearning.entity.UserVocabularyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, Long> {
    Optional<UserVocabularyProgress> findByUserIdAndVocabulary_Id(String userId, Long vocabularyId);

    List<UserVocabularyProgress> findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(String userId);

    List<UserVocabularyProgress> findByUserIdAndNextReviewAtLessThanEqualOrderByNextReviewAtAsc(String userId, LocalDateTime now);
}
