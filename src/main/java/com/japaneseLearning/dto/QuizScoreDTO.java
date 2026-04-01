package com.japaneseLearning.dto;

import java.time.LocalDateTime;

public record QuizScoreDTO(
    Long attemptId,
    Long quizId,
    String quizTitle,
    Integer score,
    Integer correctAnswers,
    Integer totalQuestions,
    Double percentage,
    String passStatus,
    LocalDateTime attemptedAt
) {}
