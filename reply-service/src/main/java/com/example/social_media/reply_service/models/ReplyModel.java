package com.example.social_media.reply_service.models;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyModel {

    private UUID id;

    // Parent relationship
    private UUID postId;
    private UUID parentCommentId;

    // Author + content
    private String authorId;
    private String content;

    // Optional media
    private List<String> mediaUrls;

    // Engagement
    private int likesCount;

    // Moderation
    private boolean edited;
    private boolean flagged;
    private String flaggedReason;
    private boolean deleted;

    // Visibility: PUBLIC, FOLLOWERS, PRIVATE, etc.
    private String visibility;

    // Audit timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
