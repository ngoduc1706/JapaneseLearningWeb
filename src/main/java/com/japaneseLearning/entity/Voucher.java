package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Voucher entity representing discount codes
 */
@Entity
@Table(name = "Vouchers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherId")
    private Long voucherId;

    @Column(name = "Code", nullable = false, unique = true)
    private String code;

    @Column(name = "DiscountAmount")
    private Double discountAmount;

    @Column(name = "DiscountPercentage")
    private Double discountPercentage;

    @Column(name = "OnlyPaidCourse")
    @Builder.Default
    private Boolean onlyPaidCourse = false;

    @Column(name = "ExpireAt")
    private LocalDateTime expireAt;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "MaxUsageCount")
    private Integer maxUsageCount;

    @Column(name = "CurrentUsageCount")
    @Builder.Default
    private Integer currentUsageCount = 0;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private Set<UserVoucher> userVouchers = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Boolean getOnlyPaidCourse() {
        return onlyPaidCourse;
    }

    public void setOnlyPaidCourse(Boolean onlyPaidCourse) {
        this.onlyPaidCourse = onlyPaidCourse;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getMaxUsageCount() {
        return maxUsageCount;
    }

    public void setMaxUsageCount(Integer maxUsageCount) {
        this.maxUsageCount = maxUsageCount;
    }

    public Integer getCurrentUsageCount() {
        return currentUsageCount;
    }

    public void setCurrentUsageCount(Integer currentUsageCount) {
        this.currentUsageCount = currentUsageCount;
    }

    public Set<UserVoucher> getUserVouchers() {
        return userVouchers;
    }

    public void setUserVouchers(Set<UserVoucher> userVouchers) {
        this.userVouchers = userVouchers;
    }
}
