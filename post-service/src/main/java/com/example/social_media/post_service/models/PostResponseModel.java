package com.example.social_media.post_service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseModel {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("authorId")
    private String authorId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("mediaUrls")
    private List<String> mediaUrls;

    @JsonProperty("hashtags")
    private List<String> hashtags;

    @JsonProperty("mentions")
    private List<String> mentions;

    @JsonProperty("commentIds")
    private List<UUID> commentIds;

    @JsonProperty("likesCount")
    private int likesCount;

    @JsonProperty("commentsCount")
    private int commentsCount;

    @JsonProperty("sharesCount")
    private int sharesCount;

    @JsonProperty("edited")
    private boolean edited;

    @JsonProperty("pinned")
    private boolean pinned;

    @JsonProperty("flagged")
    private boolean flagged;

    @JsonProperty("flaggedReason")
    private String flaggedReason;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("location")
    private String location;

    @JsonProperty("postType")
    private String postType;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
}
