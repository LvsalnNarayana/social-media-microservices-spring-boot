package com.example.social_media.logging_service.services;


import com.example.social_media.shared_libs.models.LogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogEventListener {

    private final LogStorageService storageService;

    public LogEventListener(LogStorageService storageService) {
        this.storageService = storageService;
    }

    @KafkaListener(
            topics = "${logging.kafka.topic}",
            groupId = "logging-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(LogEvent logEvent, Acknowledgment ack) {

        try {
            log.debug("Received log event: {}", logEvent);

            storageService.store(logEvent);

            ack.acknowledge(); // manually commit after success

        } catch (Exception ex) {
            log.error("Failed to process log event: {}", logEvent, ex);
        }
    }
}
