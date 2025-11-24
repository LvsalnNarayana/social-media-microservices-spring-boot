package com.example.social_media.shared_libs.kafka;

import com.example.social_media.shared_libs.configuration.LoggingProperties;
import com.example.social_media.shared_libs.models.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "logging.enabled", havingValue = "true")
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, LogEvent> eventTemplate;
    private final KafkaTemplate<String, String> stringTemplate;
    private final LoggingProperties props;

    public KafkaProducer(
            @Qualifier("logEventKafkaTemplate") KafkaTemplate<String, LogEvent> eventTemplate,
            @Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> stringTemplate,
            LoggingProperties props) {
        this.eventTemplate = eventTemplate;
        this.stringTemplate = stringTemplate;
        this.props = props;
    }

    // ===============================================================
    // ----------- LOGEVENT PRODUCER (JSON STRUCTURED EVENTS) --------
    // ===============================================================
    public void publishEvent(String topic, LogEvent event) {
        publishEvent(topic, event, null, null);
    }

    public void publishEvent(String topic, LogEvent event, Integer partition, String key) {

        if (topic == null) {
            log.error("❌ Failed to send LogEvent: Topic is null");
            return;
        }

        eventTemplate.send(topic, partition, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Failed to send LogEvent → {}", ex.getMessage());
                    } else {
                        log.info("✅ LogEvent Sent → Topic={}, Partition={}, Offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishEventToPartition(String topic, LogEvent event, int partition) {
        publishEvent(topic, event, partition, null);
    }

    public void infoEvent(String topic, LogEvent event) {
        publishEvent(topic, event, null, null);
    }

    public void errorEvent(String topic, LogEvent event) {
        publishEvent(topic, event, null, null);
    }

    // ===============================================================
    // ------------------- STRING LOG PRODUCER ------------------------
    // ===============================================================

    public void publishRaw(String topic, String message) {
        publishRaw(topic, message, null, null);
    }

    public void publishRaw(String topic, String message, Integer partition, String key) {

        if (!props.isEnabled()) {
            return;
        }

        if (topic == null) {
            log.error("❌ Failed to send raw string: Topic is not configured (null)");
            return;
        }

        stringTemplate.send(topic, partition, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Failed to send raw string → {}", ex.getMessage());
                    } else {
                        log.info("✅ Raw Log Sent → Topic={}, Partition={}, Offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void publishRawToPartition(String topic, String message, int partition) {
        publishRaw(topic, message, partition, null);
    }

    public void infoRaw(String topic, String msg) {
        publishRaw(topic, "[INFO] " + msg);
    }

    public void errorRaw(String topic, String msg) {
        publishRaw(topic, "[ERROR] " + msg);
    }
}
