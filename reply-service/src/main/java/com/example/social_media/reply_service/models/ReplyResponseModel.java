package com.example.social_media.reply_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyResponseModel {

    private UUID id;

    // Relationship
    private UUID postId;
    private UUID parentCommentId;

    // User + content
    private String authorId;
    private String content;

    private List<String> mediaUrls;

    // Engagement
    private int likesCount;

    // Moderation
    private boolean edited;
    private boolean flagged;
    private String flaggedReason;
    private boolean deleted;

    // Visibility
    private String visibility;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;
}
