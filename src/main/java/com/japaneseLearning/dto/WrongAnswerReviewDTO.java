package com.japaneseLearning.dto;

public record WrongAnswerReviewDTO(
    Long questionId,
    String questionText,
    Long selectedOptionId,
    String selectedOptionText,
    Long correctOptionId,
    String correctOptionText
) {}
