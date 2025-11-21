package com.example.social_media.post_service.models;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostModel {

    private UUID id;

    private String authorId;
    private String content;

    private List<String> mediaUrls;
    private List<String> hashtags;
    private List<String> mentions;

    private List<UUID> commentIds;

    private int likesCount;
    private int commentsCount;
    private int sharesCount;

    private boolean edited;
    private boolean pinned;
    private boolean flagged;
    private String flaggedReason;

    private String visibility; // PUBLIC, FRIENDS, PRIVATE
    private String location;
    private String postType;   // TEXT, IMAGE, VIDEO, etc.

    private Instant createdAt;
    private Instant updatedAt;
}
