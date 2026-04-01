package com.japaneseLearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JL-39/JL-40: Track favorite and SRS progress per user-vocabulary pair.
 */
@Entity
@Table(
    name = "UserVocabularyProgress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"UserId", "VocabularyId"})
)
public class UserVocabularyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VocabularyId", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "IsFavorite", nullable = false)
    private Boolean isFavorite = false;

    @Column(name = "ReviewCount", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "SrsLevel", nullable = false)
    private Integer srsLevel = 0;

    @Column(name = "MasteryPercent", nullable = false)
    private Integer masteryPercent = 0;

    @Column(name = "LastReviewedAt")
    private LocalDateTime lastReviewedAt;

    @Column(name = "NextReviewAt")
    private LocalDateTime nextReviewAt;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Vocabulary getVocabulary() { return vocabulary; }
    public void setVocabulary(Vocabulary vocabulary) { this.vocabulary = vocabulary; }

    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Integer getSrsLevel() { return srsLevel; }
    public void setSrsLevel(Integer srsLevel) { this.srsLevel = srsLevel; }

    public Integer getMasteryPercent() { return masteryPercent; }
    public void setMasteryPercent(Integer masteryPercent) { this.masteryPercent = masteryPercent; }

    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(LocalDateTime lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }

    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
