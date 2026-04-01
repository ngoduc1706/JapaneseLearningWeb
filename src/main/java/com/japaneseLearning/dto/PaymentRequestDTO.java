package com.japaneseLearning.dto;

public class PaymentRequestDTO {
    private Long courseId;
    private String voucherCode;
    private String paymentMethod;
    private Double amount;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Long courseId, String voucherCode, String paymentMethod, Double amount) {
        this.courseId = courseId;
        this.voucherCode = voucherCode;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
