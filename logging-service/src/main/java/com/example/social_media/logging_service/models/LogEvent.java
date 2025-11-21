package com.example.social_media.logging_service.models;

import lombok.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEvent {

    // Core Identifiers
    private UUID id;                 // unique event ID
    private Instant timestamp;       // event timestamp

    // Service Info
    private String service;          // comment-service, post-service, etc.
    private String level;            // INFO, WARN, ERROR
    private String message;          // log message text

    // Distributed Tracing
    private String traceId;
    private String spanId;

    // Request Context
    private RequestInfo request;

    // Domain Context (Post/Comment/Reply IDs etc.)
    private Map<String, String> context;

    // Error Block (Only for EXCEPTION logs)
    private ExceptionInfo exception;


    // --------------------------
    // Nested Request DTO
    // --------------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestInfo {
        private String method;       // GET, POST, PUT
        private String path;         // /api/v1/comments
        private String userId;       // acting user
    }


    // --------------------------
    // Nested Exception DTO
    // --------------------------
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExceptionInfo {
        private String type;         // IllegalArgumentException
        private String message;      // exception message
    }
}
