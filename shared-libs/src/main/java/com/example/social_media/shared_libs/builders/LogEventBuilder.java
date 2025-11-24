package com.example.social_media.shared_libs.builders;

import com.example.social_media.shared_libs.models.LogEvent;
import lombok.SneakyThrows;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LogEventBuilder {

    private final LogEvent.LogEventBuilder builder;

    public LogEventBuilder(String level, String message) {
        this.builder = LogEvent.builder()
                .id(UUID.fromString(UUID.randomUUID().toString()))
                .timestamp(Instant.now())
                .level(level)
                .message(message)
                .thread(Thread.currentThread().getName());
    }

    public LogEventBuilder service(String serviceName) {
        builder.service(serviceName);
        return this;
    }

    public LogEventBuilder logger(Class<?> clazz) {
        builder.logger(clazz.getName());
        return this;
    }

    public LogEventBuilder request(Map<String, Object> req) {
        builder.request(req);
        return this;
    }

    public LogEventBuilder context(Map<String, Object> ctx) {
        builder.context(ctx);
        return this;
    }

    public LogEventBuilder tags(List<String> tags) {
        builder.tags(tags);
        return this;
    }

    public LogEventBuilder exception(Throwable ex) {
        if (ex != null) {
            builder.exception(Map.of(
                    "type", ex.getClass().getName(),
                    "message", ex.getMessage(),
                    "stacktrace", Arrays.toString(ex.getStackTrace())
            ));
        }
        return this;
    }

    public LogEventBuilder payload(Map<String, Object> payload) {
        builder.payload(payload);
        return this;
    }

    @SneakyThrows
    public LogEvent build() {
        return builder.build();
    }
}
