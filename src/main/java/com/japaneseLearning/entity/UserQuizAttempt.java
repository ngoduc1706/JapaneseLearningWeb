package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JL-43: UserQuizAttempt entity - Records user's quiz attempts and scores
 */
@Entity
@Table(name = "UserQuizAttempts")
@NoArgsConstructor
@AllArgsConstructor
public class UserQuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttemptId")
    private Long attemptId;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuizId", nullable = false)
    private Quiz quiz;

    @Column(name = "Score", nullable = false)
    private Integer score;

    @Column(name = "TotalQuestions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "CorrectAnswers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "Percentage")
    private Double percentage;

    @Column(name = "AttemptedAt", nullable = false)
    private LocalDateTime attemptedAt;

    @Column(name = "PassStatus") // "PASSED", "FAILED"
    private String passStatus;

    @PrePersist
    protected void onCreate() {
        if (attemptedAt == null) {
            attemptedAt = LocalDateTime.now();
        }
        if (percentage == null && totalQuestions > 0) {
            percentage = (correctAnswers.doubleValue() / totalQuestions) * 100;
        }
        if (passStatus == null) {
            passStatus = percentage >= 80 ? "PASSED" : "FAILED";
        }
    }

    // Getters and Setters
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }

    public String getPassStatus() { return passStatus; }
    public void setPassStatus(String passStatus) { this.passStatus = passStatus; }
}
