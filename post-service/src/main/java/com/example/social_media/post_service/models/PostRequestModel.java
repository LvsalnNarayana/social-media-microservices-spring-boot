package com.example.social_media.post_service.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestModel {

    @NotNull(message = "authorId cannot be null")
    @JsonProperty("authorId")
    private String authorId;

    @NotBlank(message = "content cannot be empty")
    @JsonProperty("content")
    private String content;

    @JsonProperty("mediaUrls")
    private List<String> mediaUrls;

    @JsonProperty("hashtags")
    private List<String> hashtags;

    @JsonProperty("mentions")
    private List<String> mentions;

    @JsonProperty("visibility")
    private String visibility;  // PUBLIC, FRIENDS, PRIVATE

    @JsonProperty("location")
    private String location;

    @JsonProperty("postType")   // TEXT, IMAGE, VIDEO, LINK, POLL
    private String postType;
}
