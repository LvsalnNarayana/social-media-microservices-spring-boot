package com.example.social_media.shared_libs.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LoggingProperties {

    @Value("${logging.bootstrap-servers:}")
    private String bootstrapServers;

    @Value("${logging.enabled:false}")
    private boolean enabled;

    @Value("${logging.topic-strings:}")
    private String topicStrings;

    @Value("${logging.topic-json:}")
    private String topicJson;
}
