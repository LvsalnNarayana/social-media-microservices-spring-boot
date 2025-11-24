package com.example.social_media.shared_libs.configuration;

import com.example.social_media.shared_libs.models.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "logging.enabled", havingValue = "true")
public class EventLogKafkaConfig {

    @Autowired
    private LoggingProperties properties;

//    public EventLogKafkaConfig(LoggingProperties properties) {
//        this.properties = properties;
//    }

    @Bean("logEventProducerFactory")
    public ProducerFactory<String, LogEvent> logEventProducerFactory() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Optional but recommended for JSON + Instant
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "logEventKafkaTemplate")
    public KafkaTemplate<String, LogEvent> logEventKafkaTemplate(
            ProducerFactory<String, LogEvent> logEventProducerFactory) {
        return new KafkaTemplate<>(logEventProducerFactory);
    }
}
