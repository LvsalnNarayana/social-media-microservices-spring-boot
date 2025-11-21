package com.example.social_media.reply_service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyRequestModel {

    // Required: reply belongs to a post
    @NotNull(message = "postId is required")
    private UUID postId;

    // Required: reply belongs to a parent comment
    @NotNull(message = "parentCommentId is required")
    private UUID parentCommentId;

    // Required: ID of the user writing the reply
    @NotBlank(message = "authorId is required")
    private String authorId;

    // Required: text of the reply
    @NotBlank(message = "content cannot be empty")
    private String content;

    // Optional: media attachments
    private List<String> mediaUrls;

    // Optional: visibility settings — PUBLIC, FOLLOWERS, PRIVATE, etc.
    private String visibility;
}
