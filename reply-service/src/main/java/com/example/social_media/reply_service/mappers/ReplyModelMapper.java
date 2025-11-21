package com.example.social_media.reply_service.mappers;

import com.example.social_media.reply_service.entity.ReplyEntity;
import com.example.social_media.reply_service.models.ReplyModel;
import com.example.social_media.reply_service.models.ReplyRequestModel;
import com.example.social_media.reply_service.models.ReplyResponseModel;
import org.springframework.stereotype.Component;

@Component
public class ReplyModelMapper {

    // ---------------------------------------------------------
    // Convert Request → Entity (Used for createReply)
    // ---------------------------------------------------------
    public ReplyEntity toEntity(ReplyRequestModel request) {
        if (request == null) return null;

        return ReplyEntity.builder()
                .postId(request.getPostId())
                .parentCommentId(request.getParentCommentId())
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .mediaUrls(request.getMediaUrls())
                .visibility(request.getVisibility())
                .edited(false)
                .flagged(false)
                .deleted(false)
                .likesCount(0)
                .build();
    }

    // ---------------------------------------------------------
    // Convert Entity → ResponseModel (Used for returning data)
    // ---------------------------------------------------------
    public ReplyResponseModel toResponseModel(ReplyEntity entity) {
        if (entity == null) return null;

        return ReplyResponseModel.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .parentCommentId(entity.getParentCommentId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(entity.getMediaUrls())
                .likesCount(entity.getLikesCount())       // ✅ Added
                .edited(entity.isEdited())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .deleted(entity.isDeleted())
                .visibility(entity.getVisibility())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ---------------------------------------------------------
    // Convert Entity → Internal Model
    // ---------------------------------------------------------
    public ReplyModel toModel(ReplyEntity entity) {
        if (entity == null) return null;

        return ReplyModel.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .parentCommentId(entity.getParentCommentId())
                .authorId(entity.getAuthorId())
                .content(entity.getContent())
                .mediaUrls(entity.getMediaUrls())
                .likesCount(entity.getLikesCount())
                .edited(entity.isEdited())
                .flagged(entity.isFlagged())
                .flaggedReason(entity.getFlaggedReason())
                .deleted(entity.isDeleted())
                .visibility(entity.getVisibility())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ---------------------------------------------------------
    // Update entity content (Used for editing reply)
    // ---------------------------------------------------------
    public void updateEntityFromContent(ReplyEntity entity, String updatedContent) {
        if (entity == null || updatedContent == null) return;

        entity.setContent(updatedContent);
        entity.setEdited(true);
    }
}
