package com.example.social_media.notification_service.models;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequestModel {

    // The user receiving the notification
    private String recipientId;

    // The user who triggered the notification (optional)
    private String senderId;

    // Notification type: LIKE, COMMENT, FOLLOW, MENTION, SHARE, etc.
    private String type;

    // Optional UI-friendly message
    private String message;

    // Associated resource (postId, commentId, etc.)
    private String referenceId;

    // Additional flexible metadata
    private Map<String, Object> metadata;
}
