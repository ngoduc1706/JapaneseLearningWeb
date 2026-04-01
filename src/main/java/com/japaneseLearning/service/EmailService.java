package com.japaneseLearning.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendHtmlEmailWithTemplate(String to, String subject, String templateName, org.thymeleaf.context.Context context);
}
