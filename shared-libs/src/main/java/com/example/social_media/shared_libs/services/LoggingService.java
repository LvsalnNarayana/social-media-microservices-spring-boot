package com.example.social_media.shared_libs.services;


import com.example.social_media.shared_libs.builders.LogEventBuilder;
import com.example.social_media.shared_libs.models.LogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private static final String TOPIC = "logs";
    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    public void info(String message, Map<String, Object> payload) {
        LogEvent event = new LogEventBuilder("INFO", message)
                .payload(payload)
                .build();

        kafkaTemplate.send(TOPIC, event.getId().toString(), event);
    }

    public void warn(String message, Map<String, Object> payload) {
        LogEvent event = new LogEventBuilder("WARN", message)
                .payload(payload)
                .build();

        kafkaTemplate.send(TOPIC, event.getId().toString(), event);
    }

    public void error(String message, Throwable ex, Map<String, Object> payload) {
        LogEvent event = new LogEventBuilder("ERROR", message)
                .exception(ex)
                .payload(payload)
                .build();

        kafkaTemplate.send(TOPIC, event.getId().toString(), event);
    }
}
