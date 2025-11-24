package com.example.social_media.notification_service.models;

import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationModel {

    private UUID id;

    // User receiving the notification
    private String recipientId;

    // User who triggered the notification
    private String senderId;

    // Type: LIKE, COMMENT, FOLLOW, MENTION, SHARE, etc.
    private String type;

    // User-friendly message shown in UI
    private String message;

    // Domain reference: post/comment/etc.
    private String referenceId;

    // Flexible metadata
    private Map<String, Object> metadata;

    // Read/unread
    private boolean read;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
