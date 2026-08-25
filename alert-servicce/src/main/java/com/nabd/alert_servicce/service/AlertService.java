package com.nabd.alert_servicce.service;

import com.nabd.kafka.event.AlertingEvent;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {
  private final EmailService emailService;

  public AlertService(EmailService emailService) {
    this.emailService = emailService;
  }

  @KafkaListener(topics = "energy-alerts", groupId = "alert-service")
  public void energyUsageEvent(AlertingEvent alertingEvent) throws MessagingException {
    log.info("Message received with an event {}", alertingEvent);
    final String emailSubject = "Energy Usage Alert";
    final String message =
        String.format(
            """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif;">

                        <h1>⚡ Energy Usage Alert</h1>

                        <p>%s</p>

                        <p>
                            <strong>Threshold:</strong> %s
                        </p>

                        <p>
                            <strong>Energy Consumed:</strong> %s
                        </p>

                    </body>
                    </html>
                    """,
            alertingEvent.message(), alertingEvent.threshold(), alertingEvent.energyConsumed());

    emailService.sendMail(alertingEvent.email(), emailSubject, message, alertingEvent.userId());
  }
}
