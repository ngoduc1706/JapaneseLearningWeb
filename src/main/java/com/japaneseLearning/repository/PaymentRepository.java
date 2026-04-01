package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(String userId);
    List<Payment> findByCourse_CourseId(Long courseId);
    Payment findByTransactionId(String transactionId);
    Optional<Payment> findTopByUserIdAndCourse_CourseIdAndStatusOrderByPaymentDateDesc(
            String userId,
            Long courseId,
            Payment.PaymentStatus status
    );
}
