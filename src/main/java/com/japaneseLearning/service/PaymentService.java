package com.japaneseLearning.service;

import com.japaneseLearning.entity.Payment;
import com.japaneseLearning.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final EnrollmentService enrollmentService;
    private final com.japaneseLearning.repository.CourseRepository courseRepository;

    public PaymentService(PaymentRepository paymentRepository, 
                          EnrollmentService enrollmentService,
                          com.japaneseLearning.repository.CourseRepository courseRepository) {
        this.paymentRepository = paymentRepository;
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
    }

    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsByCourseId(Long courseId) {
        return paymentRepository.findByCourse_CourseId(courseId);
    }

    public Payment createPayment(String userId, Long courseId, Double amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        // Correctly link the course using injected repository
        payment.setCourse(courseRepository.findById(courseId).orElse(null));
        
        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(status);
        Payment saved = paymentRepository.save(payment);
        
        // AUTO ENROLL on success
        if (status == Payment.PaymentStatus.COMPLETED && saved.getCourse() != null) {
            enrollmentService.enrollUserInCourse(saved.getUserId(), saved.getCourse().getCourseId());
        }
        
        return saved;
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    public Payment updateTransactionId(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setTransactionId(transactionId);
        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatusByTransactionId(String transactionId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);
        if (payment == null) {
            throw new RuntimeException("Payment not found by transaction id: " + transactionId);
        }
        return updatePaymentStatus(payment.getPaymentId(), status);
    }
}
