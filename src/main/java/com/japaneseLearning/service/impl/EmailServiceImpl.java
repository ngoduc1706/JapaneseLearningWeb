package com.japaneseLearning.service.impl;

import com.japaneseLearning.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final org.thymeleaf.spring6.SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}", to, e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (to == null || subject == null || htmlContent == null) {
            log.error("Cannot send HTML email: missing mandatory recipients or content");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, 
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
                StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail != null ? fromEmail : "noreply@japaneselearning.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", to, e);
        }
    }

    @Override
    public void sendHtmlEmailWithTemplate(String to, String subject, String templateName, org.thymeleaf.context.Context context) {
        try {
            String htmlContent = templateEngine.process("email/" + templateName, context);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to process email template: {}", templateName, e);
        }
    }
}
