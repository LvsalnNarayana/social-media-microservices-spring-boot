package com.example.social_media.logging_service.configuration;

import com.example.social_media.shared_libs.configuration.LoggingProperties;
import com.example.social_media.shared_libs.models.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    // -------------------------------------------------------------------------
    // STRING CONSUMER FACTORY
    // -------------------------------------------------------------------------
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory(LoggingProperties props) {

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "logging-service-group-string");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    @Bean(name = "kafkaStringListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaStringListenerContainerFactory(
            ConsumerFactory<String, String> stringConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(stringConsumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }


    // -------------------------------------------------------------------------
    // LOGEVENT (JSON) CONSUMER FACTORY — WITH INSTANT SUPPORT
    // -------------------------------------------------------------------------
    @Bean
    public ConsumerFactory<String, LogEvent> logEventConsumerFactory(LoggingProperties props) {

        // ---- Custom ObjectMapper for Instant, LocalDateTime, etc ----
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JsonDeserializer<LogEvent> deserializer =
                new JsonDeserializer<>(LogEvent.class, mapper);
        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "logging-service-group-event");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "kafkaEventListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LogEvent> kafkaEventListenerContainerFactory(
            ConsumerFactory<String, LogEvent> logEventConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LogEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(logEventConsumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

        return factory;
    }
}
