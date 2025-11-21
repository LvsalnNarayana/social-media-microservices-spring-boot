package com.example.social_media.post_service.mappers;

import com.example.social_media.post_service.entity.PostEntity;
import com.example.social_media.post_service.models.PostModel;
import com.example.social_media.post_service.models.PostRequestModel;
import com.example.social_media.post_service.models.PostResponseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostModelMapper {

    /**
     * Convert PostRequestModel -> PostEntity (for creation).
     * ID, createdAt, updatedAt are handled by JPA.
     */
    public PostEntity toEntity(PostRequestModel request) {
        if (request == null) {
            return null;
        }

        return PostEntity.builder()
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .mediaUrls(cloneList(request.getMediaUrls()))
                .hashtags(cloneList(request.getHashtags()))
                .mentions(cloneList(request.getMentions()))
                .visibility(request.getVisibility())
                .location(request.getLocation())
                .postType(request.getPostType())
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .edited(false)
                .pinned(false)
                .flagged(false)
                .build();
    }

    /**
     * Convert PostEntity -> PostModel (internal model).
     */
    public PostModel toModel(PostEntity entity) {
        if (entity == null) {
            return null;
        }

        return PostModel.builder()
                .id(entity.getId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(cloneList(entity.getMediaUrls()))
                .hashtags(cloneList(entity.getHashtags()))
                .mentions(cloneList(entity.getMentions()))
                .commentIds(entity.getCommentIds())
                .likesCount(entity.getLikesCount())
                .commentsCount(entity.getCommentsCount())
                .sharesCount(entity.getSharesCount())
                .edited(entity.isEdited())
                .pinned(entity.isPinned())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .visibility(entity.getVisibility())
                .location(entity.getLocation())
                .postType(entity.getPostType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert PostEntity -> PostResponseModel (public API response).
     */
    public PostResponseModel toResponse(PostEntity entity) {
        if (entity == null) {
            return null;
        }

        return PostResponseModel.builder()
                .id(entity.getId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(cloneList(entity.getMediaUrls()))
                .hashtags(cloneList(entity.getHashtags()))
                .mentions(cloneList(entity.getMentions()))
                .commentIds(entity.getCommentIds())
                .likesCount(entity.getLikesCount())
                .commentsCount(entity.getCommentsCount())
                .sharesCount(entity.getSharesCount())
                .edited(entity.isEdited())
                .pinned(entity.isPinned())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .visibility(entity.getVisibility())
                .location(entity.getLocation())
                .postType(entity.getPostType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Update an existing PostEntity with non-null request values (partial update).
     */
    public void updateEntity(PostRequestModel request, PostEntity entity) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getContent() != null) {
            entity.setContent(request.getContent());
        }
        if (request.getMediaUrls() != null) {
            entity.setMediaUrls(cloneList(request.getMediaUrls()));
        }
        if (request.getHashtags() != null) {
            entity.setHashtags(cloneList(request.getHashtags()));
        }
        if (request.getMentions() != null) {
            entity.setMentions(cloneList(request.getMentions()));
        }
        if (request.getVisibility() != null) {
            entity.setVisibility(request.getVisibility());
        }
        if (request.getLocation() != null) {
            entity.setLocation(request.getLocation());
        }
        if (request.getPostType() != null) {
            entity.setPostType(request.getPostType());
        }
    }

    /**
     * Helper to safely clone a list to avoid mutation side effects.
     */
    private <T> List<T> cloneList(List<T> list) {
        return list == null ? null : new ArrayList<>(list);
    }
}
