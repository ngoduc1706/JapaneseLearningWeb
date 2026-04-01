package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Course entity representing individual courses in the learning platform
 */
@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseId")
    private Long courseId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "IsFree", nullable = false)
    private boolean isFree;

    @Column(name = "Price")
    private Double price;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Lesson> lessons = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<UserCourseEnrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Quiz> quizzes = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isIsFree() { return isFree; }
    public boolean getIsFree() { return isFree; }
    public void setIsFree(boolean isFree) { this.isFree = isFree; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<Lesson> getLessons() { return lessons; }
    public void setLessons(Set<Lesson> lessons) { this.lessons = lessons; }

    public Set<UserCourseEnrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<UserCourseEnrollment> enrollments) { this.enrollments = enrollments; }

    public Set<Quiz> getQuizzes() { return quizzes; }
    public void setQuizzes(Set<Quiz> quizzes) { this.quizzes = quizzes; }
}
