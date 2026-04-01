package com.japaneseLearning.dto;

public record LessonDTO(
    Long lessonId,
    Long courseId,
    String title,
    String content,
    Integer orderInCourse,
    String vocabYouTubeLink,
    String grammarYouTubeLink
) {}

