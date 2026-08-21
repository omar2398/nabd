package com.nabd.alert_servicce.service;

import com.nabd.alert_servicce.entity.Alert;
import com.nabd.alert_servicce.repository.AlertRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmailService {
  private final JavaMailSender mailSender;
  private final AlertRepository repository;

  @Value("${message.from}")
  private String messageFrom;

  public EmailService(JavaMailSender mailSender, AlertRepository repository) {
    this.mailSender = mailSender;
    this.repository = repository;
  }

  public void sendMail(String to, String subject, String body, Long userId) throws MessagingException{
    try {
    MimeMessage mimeMessage = mailSender.createMimeMessage();

    MimeMessageHelper helper =
            new MimeMessageHelper(mimeMessage, true, "UTF-8");

    helper.setTo(to);
    helper.setSubject(subject);
    helper.setFrom(messageFrom);
    helper.setText(body, true);

    mailSender.send(mimeMessage);
      Alert alert =
          Alert.builder().sent(true).created_at(LocalDateTime.now()).userId(userId).build();
      repository.save(alert);
      log.info("The email sent to {}", to);
    } catch (MailException ex) {
      log.error(
          "The message failed to sent to : {}, with the exception: {}", userId, ex.toString());
      Alert alert =
              Alert.builder().sent(false).created_at(LocalDateTime.now()).userId(userId).build();
      repository.save(alert);
    }
  }
}
