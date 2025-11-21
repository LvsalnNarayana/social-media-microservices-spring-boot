package com.example.social_media.comment_service.models;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentModel {

    private UUID id;

    // Post reference
    private UUID postId;

    // Author details
    private String authorId;

    // Comment content
    private String content;

    // Media URLs (JSON)
    private List<String> mediaUrls;

    // Reply IDs (JSON)
    private List<UUID> replyIds;

    // Engagement metrics
    private int likesCount;

    // Moderation + system flags
    private boolean edited;
    private boolean flagged;
    private String flaggedReason;
    private boolean pinned;
    private boolean deleted;

    // Visibility options: PUBLIC, FOLLOWERS, PRIVATE, RESTRICTED
    private String visibility;

    // Audit timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
