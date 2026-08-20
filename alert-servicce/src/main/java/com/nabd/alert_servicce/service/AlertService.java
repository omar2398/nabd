package com.nabd.alert_servicce.service;

import com.nabd.kafka.event.AlertingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {
    @KafkaListener(topics = "energy-alerts", groupId = "alert-service")
    public void energyUsageEvent(AlertingEvent alertingEvent){
        log.info("Message received with an event {}", alertingEvent);

    }
}
