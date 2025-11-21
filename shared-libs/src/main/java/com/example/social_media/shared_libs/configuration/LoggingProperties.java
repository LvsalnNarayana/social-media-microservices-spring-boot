package com.example.social_media.shared_libs.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "logging")
public class LoggingProperties {
    private String topic;
    private String bootstrapServers;
    private boolean enabled = true;
}
