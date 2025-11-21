package com.example.social_media.shared_libs.models;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEvent {

    private UUID id;                       // Unique log id
    private Instant timestamp;

    private String service;                // comment-service, post-service
    private String level;                  // INFO, ERROR, WARN
    private String message;

    private String logger;                 // class name
    private String thread;                 // Thread name

    private String traceId;                // tracing
    private String spanId;

    private Map<String, Object> request;   // {method, path, userId}
    private Map<String, Object> context;   // dynamic context
    private List<String> tags;             // ["auth", "db"]

    private Map<String, Object> exception; // type, stacktrace
    private Map<String, Object> payload;   // custom extra data
}
