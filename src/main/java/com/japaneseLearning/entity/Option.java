package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Option entity representing multiple choice options for quiz questions
 */
@Entity
@Table(name = "Options")
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OptionId")
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuestionId", nullable = false)
    private Question question;

    @Column(name = "Text", columnDefinition = "LONGTEXT")
    private String text;

    @Column(name = "IsCorrect")
    private Boolean isCorrect = false;

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}
