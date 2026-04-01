package com.japaneseLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Payment entity recording payment transactions
 */
@Entity
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentId")
    private Long paymentId;

    @Column(name = "UserId", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseId")
    private Course course;

    @Column(name = "Amount")
    private Double amount;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @Column(name = "Description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "TransactionId")
    private String transactionId;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
