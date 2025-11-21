package com.example.social_media.comment_service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestModel {

    // Required: comment belongs to a post
    @NotNull(message = "postId is required")
    private UUID postId;

    // Required: ID of the user writing the comment
    @NotBlank(message = "authorId is required")
    private String authorId;

    // Required: main comment text
    @NotBlank(message = "content cannot be empty")
    private String content;

    // Optional: media attached to the comment
    private List<String> mediaUrls;

    // Optional visibility level: PUBLIC, FOLLOWERS, PRIVATE, etc.
    private String visibility;
}
