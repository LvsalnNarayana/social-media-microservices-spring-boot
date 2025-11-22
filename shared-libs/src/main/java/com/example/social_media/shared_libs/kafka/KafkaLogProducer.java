package com.example.social_media.shared_libs.kafka;


import com.example.social_media.shared_libs.builders.LogEventBuilder;
import com.example.social_media.shared_libs.configuration.LoggingProperties;
import com.example.social_media.shared_libs.models.LogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaLogProducer {

    private final KafkaTemplate<String, LogEvent> template;
    private final LoggingProperties props;

    public void publish(LogEvent event) {
        if (!props.isEnabled()) {
            return;
        }
        template.send(props.getTopic(), event).whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println("❌ Failed to send log event to Kafka");
            } else {
                System.out.println("✅ Log event sent to Kafka. Topic={}, Partition={}, Offset={}" +
                        result.getRecordMetadata().topic() +
                        result.getRecordMetadata().partition() +
                        result.getRecordMetadata().offset());

            }
        });
        ;
    }

    public void info(String msg, Map<String, Object> ctx) {
        System.out.println(props.getTopic());
        publish(new LogEventBuilder("INFO", msg).context(ctx).build());
    }

    public void error(String msg, Throwable ex, Map<String, Object> ctx) {
        publish(new LogEventBuilder("ERROR", msg).exception(ex).context(ctx).build());
    }
}
