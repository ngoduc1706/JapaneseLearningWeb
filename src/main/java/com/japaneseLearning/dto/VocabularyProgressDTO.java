package com.japaneseLearning.dto;

import java.time.LocalDateTime;

public record VocabularyProgressDTO(
    Long vocabularyId,
    String kanji,
    String hiragana,
    String meaning,
    Boolean isFavorite,
    Integer reviewCount,
    Integer srsLevel,
    Integer masteryPercent,
    LocalDateTime lastReviewedAt,
    LocalDateTime nextReviewAt
) {}
