package com.example.social_media.comment_service.models;

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
public class CommentResponseModel {

    private UUID id;

    private UUID postId;

    private String authorId;

    private String content;

    private List<String> mediaUrls;

    // Updated to match entity and service logic
    private List<UUID> replyIds;

    private int likesCount;

    private boolean edited;

    private boolean flagged;

    private String flaggedReason;

    private boolean pinned;

    private boolean deleted;

    private String visibility;

    private Instant createdAt;

    private Instant updatedAt;
}
