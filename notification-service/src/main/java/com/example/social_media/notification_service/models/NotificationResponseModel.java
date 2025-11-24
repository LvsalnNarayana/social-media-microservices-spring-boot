package com.example.social_media.notification_service.models;

import lombok.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponseModel {

    private UUID id;

    // The user receiving the notification
    private String recipientId;

    // Who triggered it
    private String senderId;

    // Type: LIKE, COMMENT, FOLLOW, SHARE, MENTION, etc.
    private String type;

    // Display-friendly message
    private String message;

    // Associated post, comment, or resource
    private String referenceId;

    // Flexible extra payload
    private Map<String, Object> metadata;

    // Status
    private boolean read;

    // For UI sorting
    private Instant createdAt;
    private Instant updatedAt;
}
