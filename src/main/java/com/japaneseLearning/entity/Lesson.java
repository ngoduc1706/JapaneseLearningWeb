package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Lesson entity representing individual lessons within a course
 */
@Entity
@Table(name = "Lessons")
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LessonId")
    private Long lessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseId", nullable = false)
    private Course course;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "OrderInCourse")
    private Integer orderInCourse;

    @Column(name = "VocabYouTubeLink")
    private String vocabYouTubeLink;

    @Column(name = "GrammarYouTubeLink")
    private String grammarYouTubeLink;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Vocabulary> vocabularies = new HashSet<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Grammar> grammars = new HashSet<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Kanji> kanjis = new HashSet<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<UserProgress> userProgress = new HashSet<>();

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getOrderInCourse() { return orderInCourse; }
    public void setOrderInCourse(Integer orderInCourse) { this.orderInCourse = orderInCourse; }

    public String getVocabYouTubeLink() { return vocabYouTubeLink; }
    public void setVocabYouTubeLink(String vocabYouTubeLink) { this.vocabYouTubeLink = vocabYouTubeLink; }

    public String getGrammarYouTubeLink() { return grammarYouTubeLink; }
    public void setGrammarYouTubeLink(String grammarYouTubeLink) { this.grammarYouTubeLink = grammarYouTubeLink; }

    public Set<Vocabulary> getVocabularies() { return vocabularies; }
    public void setVocabularies(Set<Vocabulary> vocabularies) { this.vocabularies = vocabularies; }

    public Set<Grammar> getGrammars() { return grammars; }
    public void setGrammars(Set<Grammar> grammars) { this.grammars = grammars; }

    public Set<Kanji> getKanjis() { return kanjis; }
    public void setKanjis(Set<Kanji> kanjis) { this.kanjis = kanjis; }

    public Set<Quiz> getQuizzes() { return quizzes; }
    public void setQuizzes(Set<Quiz> quizzes) { this.quizzes = quizzes; }

    public Set<UserProgress> getUserProgress() { return userProgress; }
    public void setUserProgress(Set<UserProgress> userProgress) { this.userProgress = userProgress; }
}
