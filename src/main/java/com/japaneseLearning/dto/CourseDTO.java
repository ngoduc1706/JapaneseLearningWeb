package com.japaneseLearning.dto;

import java.time.LocalDateTime;

public record CourseDTO(
    Long courseId,
    String title,
    String description,
    boolean isFree,
    Double price,
    Long categoryId,
    String categoryName,
    int lessonCount,
    LocalDateTime createdAt
) {}

