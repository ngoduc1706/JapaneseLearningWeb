package com.japaneseLearning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO {
    private Long quizId;
    private Long courseId;
    private Long lessonId;
    private String title;
    private int questionCount;
    private java.time.LocalDateTime createdAt;
}
