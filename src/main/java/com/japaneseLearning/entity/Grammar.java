package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Grammar entity representing Japanese grammar patterns and rules
 */
@Entity
@Table(name = "Grammars")
@NoArgsConstructor
@AllArgsConstructor
public class Grammar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GrammarId")
    private Long grammarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonId")
    private Lesson lesson;

    @Column(name = "Rule", columnDefinition = "LONGTEXT")
    private String rule;

    @Column(name = "Example", columnDefinition = "LONGTEXT")
    private String example;

    @Column(name = "Level")
    private String level;

    @Column(name = "Notes", columnDefinition = "LONGTEXT")
    private String notes;

    // Getters and Setters
    public Long getGrammarId() { return grammarId; }
    public void setGrammarId(Long grammarId) { this.grammarId = grammarId; }

    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }

    public String getRule() { return rule; }
    public void setRule(String rule) { this.rule = rule; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
