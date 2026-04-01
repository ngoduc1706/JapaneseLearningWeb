package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * UserVoucher entity tracking which users have used which vouchers
 */
@Entity
@Table(name = "UserVouchers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherId", nullable = false)
    private Voucher voucher;

    @Column(name = "UsedAt")
    private java.time.LocalDateTime usedAt;

    @PrePersist
    protected void onCreate() {
        if (usedAt == null) {
            usedAt = java.time.LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }

    public java.time.LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(java.time.LocalDateTime usedAt) { this.usedAt = usedAt; }
}
