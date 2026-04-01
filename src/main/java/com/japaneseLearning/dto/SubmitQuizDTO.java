package com.japaneseLearning.dto;

public record SubmitQuizDTO(
    Long quizId,
    Integer[] selectedAnswers  // Array of option IDs selected by user
) {}
