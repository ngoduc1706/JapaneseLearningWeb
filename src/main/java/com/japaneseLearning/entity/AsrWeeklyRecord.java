package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * AsrWeeklyRecord entity tracking weekly speech recognition practice
 */
@Entity
@Table(name = "AsrWeeklyRecords")
@NoArgsConstructor
@AllArgsConstructor
public class AsrWeeklyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @Column(name = "record_year", nullable = false)
    private Integer year;

    @Column(name = "Week", nullable = false)
    private Integer week;

    @Column(name = "TargetCount")
    private Integer targetCount = 6;

    @Column(name = "DoneCount")
    private Integer doneCount = 0;

    @Column(name = "RewardUnlocked")
    private Boolean rewardUnlocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherId")
    private Voucher voucher;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public Integer getTargetCount() { return targetCount; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

    public Integer getDoneCount() { return doneCount; }
    public void setDoneCount(Integer doneCount) { this.doneCount = doneCount; }

    public Boolean getRewardUnlocked() { return rewardUnlocked; }
    public void setRewardUnlocked(Boolean rewardUnlocked) { this.rewardUnlocked = rewardUnlocked; }

    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
