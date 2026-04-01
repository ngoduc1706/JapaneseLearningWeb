package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * UserCourseEnrollment entity tracking course enrollments and status
 */
@Entity
@Table(name = "UserCourseEnrollments")
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseId", nullable = false)
    private Course course;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(name = "EnrolledAt")
    private java.time.LocalDateTime enrolledAt;

    @Column(name = "CompletedAt")
    private java.time.LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (enrolledAt == null) {
            enrolledAt = java.time.LocalDateTime.now();
        }
        if (status == null) {
            status = EnrollmentStatus.ENROLLED;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public java.time.LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(java.time.LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public java.time.LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(java.time.LocalDateTime completedAt) { this.completedAt = completedAt; }

    public enum EnrollmentStatus {
        ENROLLED, IN_PROGRESS, COMPLETED
    }
}
