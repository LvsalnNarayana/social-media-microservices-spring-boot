package com.example.social_media.comment_service.mappers;

import com.example.social_media.comment_service.entity.CommentEntity;
import com.example.social_media.comment_service.models.CommentModel;
import com.example.social_media.comment_service.models.CommentRequestModel;
import com.example.social_media.comment_service.models.CommentResponseModel;
import org.springframework.stereotype.Component;

@Component
public class CommentModelMapper {

    // ---------------------------------------------------------
    // Convert RequestModel → Entity (CREATE comment only)
    // ---------------------------------------------------------
    public CommentEntity toEntity(CommentRequestModel request) {
        if (request == null) {
            return null;
        }

        return CommentEntity.builder()
                .postId(request.getPostId())
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .mediaUrls(request.getMediaUrls())
                .replyIds(null)   // replies handled separately, NOT here
                .visibility(request.getVisibility())
                .edited(false)
                .flagged(false)
                .flaggedReason(null)
                .deleted(false)
                .pinned(false)
                .likesCount(0)
                .build();
    }

    // ---------------------------------------------------------
    // Convert Entity → ResponseModel
    // ---------------------------------------------------------
    public CommentResponseModel toResponseModel(CommentEntity entity) {
        if (entity == null) {
            return null;
        }

        return CommentResponseModel.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(entity.getMediaUrls())
                .replyIds(entity.getReplyIds())
                .likesCount(entity.getLikesCount())
                .edited(entity.isEdited())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .pinned(entity.isPinned())
                .deleted(entity.isDeleted())
                .visibility(entity.getVisibility())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ---------------------------------------------------------
    // Convert Entity → Internal Model (optional use)
    // ---------------------------------------------------------
    public CommentModel toModel(CommentEntity entity) {
        if (entity == null) {
            return null;
        }

        return CommentModel.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(entity.getMediaUrls())
                .replyIds(entity.getReplyIds())
                .likesCount(entity.getLikesCount())
                .edited(entity.isEdited())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .pinned(entity.isPinned())
                .deleted(entity.isDeleted())
                .visibility(entity.getVisibility())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ---------------------------------------------------------
    // Update existing entity
    // ---------------------------------------------------------
    public void updateEntityFromRequest(CommentEntity entity, String updatedContent) {
        if (entity == null || updatedContent == null) {
            return;
        }

        entity.setContent(updatedContent);
        entity.setEdited(true);
    }
}
